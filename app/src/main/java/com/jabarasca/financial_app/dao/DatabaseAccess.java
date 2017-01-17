package com.jabarasca.financial_app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.SparseArray;

import com.jabarasca.financial_app.CalendarActivity;
import com.jabarasca.financial_app.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    private static DatabaseAccess dbAccessInstance;
    private final String DATABASE_FILE_NAME = "financial_app_db";
    private final int DATABASE_VERSION = 1;
    private SQLiteOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private SQLiteStatement minPickerSqlStmntMainActv;
    private SQLiteStatement maxPickerSqlStmntMainActv;
    private boolean can_write = true;
    private final String AMOUNTS_TABLE = "tb_amount_month";
    private final String AMOUNT_COLUMN = "amount";
    private final String DATE_COLUMN = "date";
    private final String CREATE_TB_MONTH = "CREATE TABLE tb_amount_month(cod INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                        "date TEXT," +
                                                                        "amount TEXT);";

    private DatabaseAccess(Context context) {
        dbOpenHelper = new SQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(CREATE_TB_MONTH);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

            @Override
            public void onOpen(SQLiteDatabase db) {}
        };
        //Opens the database to read/write.
        try {
            db = dbOpenHelper.getWritableDatabase();
        }catch (SQLiteException sqle) {
            can_write = false;
        }
    }

    public static DatabaseAccess getDBAcessInstance(Context context) {
        if(dbAccessInstance == null) {
            dbAccessInstance = new DatabaseAccess(context);
        }
        return dbAccessInstance;
    }

    //TODO: Move this method inside calls that needs to write on DB.
    public boolean databaseCanWrite() {
        return can_write;
    }

    public void closeDatabase() {
        if(minPickerSqlStmntMainActv != null) {
            minPickerSqlStmntMainActv.close();
        }
        if(maxPickerSqlStmntMainActv != null) {
            maxPickerSqlStmntMainActv.close();
        }
        db.close();
        dbAccessInstance = null;
    }

    //dateValue must be in format: YYYY-MM-DD HH:MM:SS. Return != -1 operation succeeded.
    public long saveAmount(String amountValue, String dateValue) {
        ContentValues mapValues = new ContentValues();
        mapValues.put(AMOUNT_COLUMN, amountValue);
        mapValues.put(DATE_COLUMN, dateValue);

        return db.insert(AMOUNTS_TABLE, null, mapValues);
    }

    //date must be in format: YYYY-MM-DD HH:MI:SS.
    public int removeAmount(String amountValue, String date) {
        String queryFilter = String.format("%s = '%s' AND %s = '%s'", DATE_COLUMN, date,
                AMOUNT_COLUMN, amountValue);
        String subQueryFilter = "SELECT cod FROM tb_amount_month WHERE " + queryFilter;
        String deleteFilter = "cod IN (" + subQueryFilter + ");";
        int returnValue = db.delete(AMOUNTS_TABLE, deleteFilter, null);

        return returnValue;
    }

    //actualFormattedDate must be in format: YYYY-MM-DD only.
    public List<String> getSpecificMonthlyAmounts(String actualFormattedDate) {
        String queryFilter = "STRFTIME('%Y-%m'," + DATE_COLUMN + ") = STRFTIME('%Y-%m','" + actualFormattedDate + "');";
        Cursor queryCursor = db.query(AMOUNTS_TABLE, new String[]{AMOUNT_COLUMN, DATE_COLUMN}, queryFilter,
                null, null, null, null, null);

        final int AMOUNT_COLUMN_INDEX = 0;
        final int DATE_COLUMN_INDEX = 1;
        List<String> amountsList = new ArrayList<>();

        if(queryCursor.moveToFirst()) {
            for (int i = 0; i < queryCursor.getCount(); i++) {
                amountsList.add(queryCursor.getString(AMOUNT_COLUMN_INDEX) + "&" + queryCursor.
                        getString(DATE_COLUMN_INDEX));
                queryCursor.moveToNext();
            }
        }
        queryCursor.close();
        return amountsList;
    }

    public long getDatePickerValue(int typeOfValue) {
        SQLiteStatement sqLiteStatement;
        if(typeOfValue == CalendarActivity.DATE_PICKER_MIN_VALUE) {
            if(minPickerSqlStmntMainActv == null) {
                minPickerSqlStmntMainActv = db.compileStatement("SELECT MIN(DISTINCT " + DATE_COLUMN + ") FROM " +
                        AMOUNTS_TABLE + ";");
            }
            sqLiteStatement = minPickerSqlStmntMainActv;
        } else {
            if(maxPickerSqlStmntMainActv == null) {
                maxPickerSqlStmntMainActv = db.compileStatement("SELECT MAX(DISTINCT " + DATE_COLUMN + ") FROM " +
                        AMOUNTS_TABLE+";");
            }
            sqLiteStatement = maxPickerSqlStmntMainActv;
        }
        String dbReturnedDate;
        try {
            dbReturnedDate = sqLiteStatement.simpleQueryForString();
            if(dbReturnedDate == null) {
                dbReturnedDate = Utilities.getNowDateWithoutTimeForDB();
            }
        } catch(SQLiteDoneException e) {
            dbReturnedDate = Utilities.getNowDateWithoutTimeForDB();
        }

        return Utilities.getLongValueFromDBDate(dbReturnedDate);
    }

    public SparseArray<Float> getGraphicAnalysisValues(int year) {
        SparseArray<Float> annualReportValues = new SparseArray<>();
        String sql = "SELECT %s, STRFTIME('%%m', %s) AS month FROM %s " +
                "WHERE STRFTIME('%%Y', %s) = ? ORDER BY month";
        sql = String.format(sql, AMOUNT_COLUMN, DATE_COLUMN, AMOUNTS_TABLE, DATE_COLUMN);

        for(int i = 1; i <= 12; i++) {
            annualReportValues.put(i, Float.NaN);
        }

        Cursor resultCur = db.rawQuery(sql, new String[]{String.valueOf(year)});
        //If cursor has result.
        if(resultCur.moveToFirst()) {
            int totalRows = resultCur.getCount();
            for(int i = 0; i < totalRows; i ++) {
                int actualMonth = Integer.parseInt(resultCur.getString(1));
                float actualAmount = resultCur.getFloat(0);
                if(!Float.isNaN(annualReportValues.get(actualMonth))) {
                    actualAmount += annualReportValues.get(actualMonth);
                }
                annualReportValues.put(actualMonth, actualAmount);
                resultCur.moveToNext();
            }
        }
        resultCur.close();
        return annualReportValues;
    }
}

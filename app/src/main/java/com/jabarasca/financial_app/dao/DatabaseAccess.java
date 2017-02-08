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
    private final String AMOUNTS_TABLE = "tb_amount";
    private final String AMOUNT_COLUMN = "amount";
    private final String DATE_COLUMN = "date";
    private final String TITLE_COLUMN = "title";
    private final String COD_COLUMN = "cod";
    private final String CREATE_TB_MONTH = "CREATE TABLE tb_amount(cod INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                        "date TEXT," +
                                                                        "amount TEXT," +
                                                                        "title TEXT);";

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

    public static DatabaseAccess getDBAccessInstance(Context context) {
        if(dbAccessInstance == null) {
            dbAccessInstance = new DatabaseAccess(context);
        }
        return dbAccessInstance;
    }

    //TODO: Move this method inside calls that needs to write on DB.
    public boolean databaseCanWrite() {
        return can_write;
    }

    public void closeSqlStatements() {
        if(minPickerSqlStmntMainActv != null) {
            minPickerSqlStmntMainActv.close();
            minPickerSqlStmntMainActv = null;
        }
        if(maxPickerSqlStmntMainActv != null) {
            maxPickerSqlStmntMainActv.close();
            maxPickerSqlStmntMainActv = null;
        }
    }

    public void closeDatabase() {
        db.close();
        dbAccessInstance = null;
    }

    //dateValue must be in format: YYYY-MM-DD HH:MM:SS. Return != -1 operation succeeded.
    public long saveAmount(String amountValue, String dateValue, String titleName) {
        ContentValues mapValues = new ContentValues();
        mapValues.put(AMOUNT_COLUMN, amountValue);
        mapValues.put(DATE_COLUMN, dateValue);
        if(titleName.length() > 0) {
            mapValues.put(TITLE_COLUMN, titleName);
        }

        return db.insert(AMOUNTS_TABLE, null, mapValues);
    }

    //date must be in format: YYYY-MM-DD HH:MI:SS.
    public int removeAmount(String amountValue, String date) {
        String queryFilter = String.format("%s = '%s' AND %s = '%s'", DATE_COLUMN, date,
                AMOUNT_COLUMN, amountValue);
        String subQueryFilter = "SELECT %s FROM %s WHERE " + queryFilter;
        subQueryFilter = String.format(subQueryFilter, COD_COLUMN, AMOUNTS_TABLE);
        String deleteFilter = "%s IN (" + subQueryFilter + ");";
        deleteFilter = String.format(deleteFilter, COD_COLUMN);
        return db.delete(AMOUNTS_TABLE, deleteFilter, null);
    }

    //actualFormattedDate must be in format: YYYY-MM-DD only.
    public List<String> getSpecificMonthlyAmounts(String actualFormattedDate) {
        String queryFilter = "WHERE STRFTIME('%Y-%m'," + DATE_COLUMN + ")" + " = " +
                "STRFTIME('%Y-%m','" + actualFormattedDate + "')";
        String orderBy = "ORDER BY %s DESC";
        orderBy = String.format(orderBy, DATE_COLUMN);
        String sql = "SELECT %s, %s, %s FROM %s";
        //OBS: DATE_COLUMN is used only to use ORDER BY by date.
        sql = String.format(sql, AMOUNT_COLUMN, COD_COLUMN, DATE_COLUMN, AMOUNTS_TABLE);
        sql = sql + " " + queryFilter + " " + orderBy;
        Cursor queryCursor = db.rawQuery(sql, null);

        final int AMOUNT_COLUMN_INDEX = 0, COD_COLUMN_INDEX = 1;
        List<String> amountsList = new ArrayList<>();

        if(queryCursor.moveToFirst()) {
            for (int i = 0; i < queryCursor.getCount(); i++) {
                amountsList.add(queryCursor.getString(AMOUNT_COLUMN_INDEX) + "&" + queryCursor
                        .getString(COD_COLUMN_INDEX));
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
                dbReturnedDate = Utilities.getNowDbDateWithoutTime();
            }
        } catch(SQLiteDoneException e) {
            dbReturnedDate = Utilities.getNowDbDateWithoutTime();
        }

        return Utilities.getLongValueFromDBDate(dbReturnedDate);
    }

    public String getTitleFromCod(int cod) {
        String sql = "SELECT %s FROM %s WHERE %s = ?";
        sql = String.format(sql, TITLE_COLUMN, AMOUNTS_TABLE, COD_COLUMN);
        Cursor resultCur = db.rawQuery(sql, new String[]{String.valueOf(cod)});
        resultCur.moveToFirst();
        String title = resultCur.getString(0);
        resultCur.close();
        return title;
    }

    public String getDateFromCod(int cod) {
        String sql = "SELECT %s FROM %s WHERE %s = ?";
        sql = String.format(sql, DATE_COLUMN, AMOUNTS_TABLE, COD_COLUMN);
        Cursor resultCur = db.rawQuery(sql, new String[]{String.valueOf(cod)});
        resultCur.moveToFirst();
        String date = resultCur.getString(0);
        resultCur.close();
        return date;
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

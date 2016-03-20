package com.jabarasca.financial_app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseAccess {

    private static DatabaseAccess dbAccessInstance;
    private final String DATABASE_FILE_NAME = "financial_app_db";
    private final int DATABASE_VERSION = 1;
    private SQLiteOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private SQLiteStatement minPickerSqlStmnt;
    private SQLiteStatement maxPickerSqlStmnt;
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
            minPickerSqlStmnt = db.compileStatement("SELECT MIN(DISTINCT " + DATE_COLUMN + ") FROM " +
                    AMOUNTS_TABLE + ";");
            maxPickerSqlStmnt = db.compileStatement("SELECT MAX(DISTINCT " + DATE_COLUMN + ") FROM " +
                    AMOUNTS_TABLE+";");
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
        db.close();
        dbAccessInstance = null;
    }

    //The amountValue needs to be string formatted. Return != -1 operation succeeded.
    //dateValue must be in format: YYYY-MM-DD
    public long saveAmount(String amountValue, String dateValue) {
        ContentValues mapValues = new ContentValues();
        mapValues.put(AMOUNT_COLUMN, amountValue);
        mapValues.put(DATE_COLUMN, dateValue);

        return db.insert(AMOUNTS_TABLE, null, mapValues);
    }

    //date must be in format: YYYY-MM-DD.
    public int removeAmount(String amountValue, String date) {
        String queryFilter = "STRFTIME('%Y-%m',"+DATE_COLUMN+") = STRFTIME('%Y-%m','"+date+"') AND '"+
                amountValue+"' = "+AMOUNT_COLUMN+" LIMIT 1";
        String selectFilter = "SELECT cod FROM tb_amount_month WHERE "+queryFilter;
        String deleteFilter = "cod IN ("+selectFilter+");";

        int returnValue = db.delete(AMOUNTS_TABLE, deleteFilter, null);

        return returnValue;
    }

    //actualFormattedDate must be in format: YYYY-MM-DD only.
    public List<String> getSpecificDateAmounts(String actualFormattedDate) {
        String queryFilter = "STRFTIME('%Y-%m'," + DATE_COLUMN + ") = STRFTIME('%Y-%m','" + actualFormattedDate + "');";
        Cursor queryCursor = db.query(AMOUNTS_TABLE, new String[]{AMOUNT_COLUMN}, queryFilter,
                null, null, null, null, null);

        return  getAmountsListFromCursor(queryCursor);
    }

    public SQLiteStatement getMinPickerSqlStmnt() {
        return minPickerSqlStmnt;
    }

    public SQLiteStatement getMaxPickerSqlStmnt() {
        return maxPickerSqlStmnt;
    }

    public long getDatePickerValue(SQLiteStatement sqlStatement, long defaultValue) {
        String datePickerValue;
        try {
            datePickerValue = sqlStatement.simpleQueryForString();
        } catch(SQLiteDoneException e) {
            return defaultValue;
        }

        if(datePickerValue != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d", Locale.getDefault());
            try {
                Date date = dateFormat.parse(datePickerValue);
                return date.getTime();
            } catch (ParseException e) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private List<String> getAmountsListFromCursor(Cursor cursor) {
        final int AMOUNT_COLUMN_INDEX = 0;
        List<String> amountsList = new ArrayList<>();

        if(!cursor.moveToFirst()) {
            return amountsList;
        } else {
            for (int i = 0; i < cursor.getCount(); i++) {
                amountsList.add(cursor.getString(AMOUNT_COLUMN_INDEX));
                cursor.moveToNext();
            }
            return amountsList;
        }
    }
}

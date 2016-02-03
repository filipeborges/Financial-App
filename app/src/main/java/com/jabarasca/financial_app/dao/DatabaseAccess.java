package com.jabarasca.financial_app.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {

    private final String DATABASE_FILE_NAME = "financial_app_db";
    private final int DATABASE_VERSION = 1;
    private SQLiteOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private final String AMOUNTS_TABLE = "tb_amount_month";
    private final String AMOUNT_COLUMN = "amount";
    private final String DATE_COLUMN = "date";
    private final String CREATE_TB_MONTH = "CREATE TABLE tb_amount_month(cod INTEGER PRIMARY KEY AUTOINCREMENT," +
                                                                        "date TEXT," +
                                                                        "amount TEXT);";

    public DatabaseAccess(Context context) {
        dbOpenHelper = new SQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION) {
            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(CREATE_TB_MONTH);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

            @Override
            public void onOpen(SQLiteDatabase db) {
                //TODO: Verify if the database is READ ONLY.
            }
        };
        //Opens the database to read/write.
        db = dbOpenHelper.getWritableDatabase();
    }

    public void closeDatabase() {
        db.close();
    }

    //The amountValue needs to be string formatted. Return != -1 operation succeeded.
    //dateValue must be in format: YYYY-MM-DD
    public long saveAmount(String amountValue, String dateValue) {
        //TODO: Verify if the database is opened.
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

    //actualFormattedDate must be in format: YYYY-MM-DD.
    public List<String> getSpecificDateAmounts(String actualFormattedDate) {
        String queryFilter = "STRFTIME('%Y-%m'," + DATE_COLUMN + ") = STRFTIME('%Y-%m','" + actualFormattedDate + "');";
        Cursor queryCursor = db.query(AMOUNTS_TABLE, new String[]{AMOUNT_COLUMN}, queryFilter,
                null, null, null, null, null);

        return  getAmountsListFromCursor(queryCursor);
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

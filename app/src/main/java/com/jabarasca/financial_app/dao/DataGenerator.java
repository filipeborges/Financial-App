package com.jabarasca.financial_app.dao;

import android.database.sqlite.SQLiteDatabase;

//Used only for UI Test.
public class DataGenerator {

    private String sqlJan = "INSERT INTO tb_amount(date, amount, title) " +
                            "VALUES('2016-01-10 14:55:32', '-235,59', 'Despeza 1');";
    private String sqlFev = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-02-10 15:55:32', '-20,35', 'Despeza 2');";
    private String sqlMar = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-03-10 16:55:32', '+200,00', 'Receita 3');";
    private String sqlAbr = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-04-10 17:55:32', '-800,00', 'Despeza 4');";
    private String sqlMai = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-05-10 18:55:32', '+400,00', 'Receita 5');";
    private String sqlJun = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-06-10 19:55:32', '-139,57', 'Despeza 6');";
    private String sqlJul = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-07-10 20:55:32', '-19,45', 'Despeza 7');";
    private String sqlAgo = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-08-10 21:55:32', '-0,75', 'Despeza 8');";
    private String sqlSet = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-09-10 22:55:32', '+355,00', 'Receita 9');";
    private String sqlOut = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-10-10 23:55:32', '+200,00', 'Receita 10');";
    private String sqlNov = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-11-10 00:55:32', '-1200,00', 'Despeza 11');";
    private String sqlDez = "INSERT INTO tb_amount(date, amount, title) " +
            "VALUES('2016-12-10 01:55:32', '-900,00', 'Despeza 12');";

    private SQLiteDatabase db;

    public DataGenerator(SQLiteDatabase db) {
        this.db = db;
    }

    public void generateData() {
        db.execSQL(sqlJan);
        db.execSQL(sqlFev);
        db.execSQL(sqlMar);
        db.execSQL(sqlAbr);
        db.execSQL(sqlMai);
        db.execSQL(sqlJun);
        db.execSQL(sqlJul);
        db.execSQL(sqlAgo);
        db.execSQL(sqlSet);
        db.execSQL(sqlOut);
        db.execSQL(sqlNov);
        db.execSQL(sqlDez);
    }

}
package by.unsofter.ussdbelarus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class USSDDB extends SQLiteOpenHelper {

    // Версия БД
    private static final int DATABASE_VERSION = 1;

    public USSDDB(Context context) {
        // конструктор суперкласса
        super(context, "USSDBelarusDB", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу с полями
        db.execSQL("create table favorites ("
                + "id integer primary key autoincrement,"
                + "code text,"
                + "info text,"
                + "type integer,"
                + "shablon text,"
                + "operator int"+ ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

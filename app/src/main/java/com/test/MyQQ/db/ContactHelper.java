package com.test.MyQQ.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ThinkPad on 2016/8/13.
 */
public class ContactHelper extends SQLiteOpenHelper {
    private String username;
    public ContactHelper(Context context,String username){
        super(context,"contact.db",null,1);
        this.username = username;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+username+"(_id integer primary key,contact_name varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

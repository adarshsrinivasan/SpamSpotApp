package com.example.adarsh.smstest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DbName = "SmsSpam";
    private static final int DbVersion = 1;
    String Query, tempst;
    Context context ;
    static int id = 0;
    public DbHelper(Context context, String query){
        super(context,DbName,null,DbVersion);
        this.context = context;
        this.Query = query;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Query);
        Log.e("DataBase", "Table Created...");
    }
    public void addRowSmsDataset(int mid ,String msg,String type,SQLiteDatabase db){
        ContentValues cr = new ContentValues();
        this.id = mid;
        cr.put("MID",mid);
        cr.put("MESSAGE",msg);
        cr.put("TYPE",type);
        db.insert("SMS_DATASET",null,cr);
        Log.e("DataBase","Row Inserted");
    }
    public void ValueChange(String table,String type,String msg,SQLiteDatabase db){
        msg = msg.trim();
        msg = msg.replaceAll("[^\\p{Alpha} ]+","");
        if(MainActivity.messages.get(msg) != null){Toast.makeText(context,"Exists",Toast.LENGTH_SHORT).show();}
        else{Toast.makeText(context,"Not Exists",Toast.LENGTH_SHORT).show();}
        MainActivity.messages.put(msg,type);
        db.execSQL("UPDATE " + table + " set TYPE = '" + type + "' where MESSAGE = '" + msg + "';");
    }
    public Cursor getData(SQLiteDatabase db,String table){
        Cursor c;
        String[] reqColInbox = {"MESSAGE","CONTACT","TYPE"}, reqColData = {"MESSAGE","TYPE"};
        if(table.equals("SMS_DATASET")){
            c = db.query("SMS_DATASET",reqColData,null,null,null,null,null);
        }
        else {
            c = db.query("SMS_INBOX_DATASET",reqColInbox,null,null,null,null,null);
        }
        return c;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
package com.matrixteknoloji.pratikfis.SQLiteData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.matrixteknoloji.pratikfis.WebService.Values;

public class Data extends SQLiteOpenHelper {

    private String Data_Version = "1.0";
    /* DB Values */
    private static final String Database_Name = "AccountInfo.db";
    private static final String Table_Name = "Info";
    private static final String Column_Id = "Id";
    private static final String Column_UserName = "UserName";
    private static final String Column_Password = "Password";
    private static final String Column_CoId = "CoId";
    private static final String Column_CoDesc = "CoDesc";
    private static final String Column_Domain = "Domain";
    /* end DB Values */

    public Data(@Nullable Context context) {
        super(context, Database_Name, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS " + Table_Name +
                "("
                + Column_Id + " INTEGER PRIMARY  KEY AUTOINCREMENT, "
                + Column_UserName + " TEXT, "
                + Column_Password + " TEXT, "
                + Column_CoId + " TEXT, "
                + Column_CoDesc + " TEXT, "
                + Column_Domain + " TEXT" +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }

    // Insert
    public boolean InsertData(String UserName, String Password, String CoId, String CoDesc, String Domain){
        // Check Data Version
        CheckDataVersion();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Column_UserName, UserName);
        contentValues.put(Column_Password, Password);
        contentValues.put(Column_CoId, CoId);
        contentValues.put(Column_CoDesc, CoDesc);
        contentValues.put(Column_Domain, Domain);
        long result = db.insert(Table_Name, null, contentValues);

        // Check result
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor GetData(){
        // Check Data Version
        CheckDataVersion();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + Table_Name, null);
        return res;
    }

    public boolean UpdateData(String Id, String CoDesc, String CoId){
        // Check Data Version
        CheckDataVersion();

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Column_CoId, CoId);
        contentValues.put(Column_CoDesc, CoDesc);

        db.update(Table_Name, contentValues, "Id = ?", new String[] {Id});
        return true;
    }

    public Integer DeleteData(String Id){
        // Check Data Version
        CheckDataVersion();

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(Table_Name, "Id = ?", new String[] {Id});
    }

    public void DeleteAllData(){
        // Check Data Version
        CheckDataVersion();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor delete = db.rawQuery("delete from " + Table_Name, null);
        delete.close();
    }

    // Check Data Version
    private void CheckDataVersion(){
        if(!Data_Version.equals(Values.Data_Version)){
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
            onCreate(db);
        }
    }
}
/*
======================================
© 2019 Copyright Matrix Teknoloji A.Ş
======================================
*/
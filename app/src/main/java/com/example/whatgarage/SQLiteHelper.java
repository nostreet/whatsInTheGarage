package com.example.whatgarage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    // 4 works on phone with no img
    public static final String TABLE_NAME = "stock_items8";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EXISTING_QUANTITY = "existingQuantity";
    public static final String COLUMN_RECOMMENDED_STOCK = "recommendedStock";
    public static final String COLUMN_STORED_TYPES = "StoredTypes";
    public static final String COLUMN_IMAGE = "Image";

    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sql){
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData(String name, int existingQuantity, int recommendedStock, String type, byte[] image){
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " VALUES (NULL, ?, ?, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString (2, Integer.toString(existingQuantity));
        statement.bindString (3, Integer.toString(recommendedStock));
        statement.bindString(4, type);
        statement.bindBlob(5, image);

        statement.executeInsert();
    }

    public void updateData(String name, int existingQuantity, int recStock, String storedType, byte[] image, int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "UPDATE " + TABLE_NAME + " SET " +
                COLUMN_NAME + " = ?, " +
                COLUMN_EXISTING_QUANTITY + " = ?, " +
                COLUMN_RECOMMENDED_STOCK + " = ?, " +
                COLUMN_STORED_TYPES + " = ?, " +
                COLUMN_IMAGE + " = ? " +
                "WHERE " + COLUMN_ID + " = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, name);
        statement.bindDouble(2, existingQuantity);
        statement.bindDouble(3, recStock);
        statement.bindString(4, storedType);
        statement.bindBlob(5, image);
        statement.bindDouble(6, id);

        statement.execute();
        database.close();
    }

    public  void deleteData(int id) {
        SQLiteDatabase database = getWritableDatabase();

        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + SQLiteHelper.COLUMN_ID + " = ?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, id);

        statement.execute();
        database.close();
    }

    public Cursor getData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        return database.rawQuery(sql, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

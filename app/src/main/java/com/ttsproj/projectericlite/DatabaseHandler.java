package com.ttsproj.projectericlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongbiaoyang on 10/10/15.
 */
public class DatabaseHandler extends SQLiteAssetHelper {

    // All Static variables
    // Database Version
    private static final String DATABASE_NAME = "projEric.db";
    private static final int DATABASE_VERSION = 1;

    // Contacts table name
    private static final String TABLE_PARA = "ParaTable";
    private static final String TABLE_FIXED = "FixedTable";
    private static final String TABLE_APPINFO = "appInfo";

    // Contacts Table Columns names
    private static final String KEY_TITLE = "title";
    private static final String KEY_TEXT = "text";
    private static final String KEY_TITULO = "titulo";
    private static final String KEY_TEXTO = "texto";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_IMAGEV = "imageV";
    private static final String KEY_COLOR = "color";
    private static final String KEY_MENU = "menu";
    private static final String KEY_CUSTOMIZE = "customize";

    private static final String KEY_FREQ_HEARING = "hearing";
    private static final String KEY_FREQ_NONENGLISH = "nonEnglish";
    private static final String KEY_FREQ_COGNITIVE = "cognitive";

    private static final String KEY_NAME = "fieldKey";
    private static final String KEY_VALUE = "fieldValue";


    public boolean firstTime = false;
    private String TAG = "DatabaseHandler";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    void addProp(String name, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name);
        values.put(KEY_VALUE, value);

        db.insert(TABLE_APPINFO, null, values);
        db.close();
    }

    String getProp(String name) {
        String selectQuery = "SELECT  * FROM " + TABLE_APPINFO + " where " + KEY_NAME + " = ?";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{name});

        String value = null;

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
              value = cursor.getString(2);
            } while (cursor.moveToNext());
        }

        return value;
    }

    // Adding new item
    void addItem(ItemStruct itemStruct, String menu, int transitType) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, itemStruct.getTitle(LANG.ENGLISH)); // English title
        values.put(KEY_TEXT, itemStruct.getText(LANG.ENGLISH)); // English Text
        values.put(KEY_TITULO, itemStruct.getTitle(LANG.SPANISH)); // Spanish title
        values.put(KEY_TEXTO, itemStruct.getText(LANG.SPANISH));    // Spanish Text
        values.put(KEY_IMAGE, itemStruct.getImageID());
        values.put(KEY_IMAGEV, itemStruct.getVImageID());
        values.put(KEY_COLOR, itemStruct.getColorString());
        values.put(KEY_CUSTOMIZE, itemStruct.getSpecialTag());
        values.put(KEY_FREQ_HEARING, itemStruct.getFreq("hearing"));    // 0 frequency at first for hearing
        values.put(KEY_FREQ_COGNITIVE, itemStruct.getFreq("cognitive"));    // 0 frequency at first for cognitive
        values.put(KEY_FREQ_NONENGLISH, itemStruct.getFreq("nonenglish"));    // 0 frequency at first for non English
        values.put(KEY_MENU, menu);

        // choose table based on transit type
        String table = transitType == CONSTANT.PARA ? TABLE_PARA : TABLE_FIXED;

        // Inserting Row
        db.insert(table, null, values);

        db.close(); // Closing database connection
    }

    // Getting All Contacts
    public List<ItemStruct> getAllItems(int transitType, String orderby, String... otherArgs) {
        List<ItemStruct> itemList = new ArrayList<ItemStruct>();

        Cursor cursor;

        // choose table based on transit type
        String table = transitType == CONSTANT.PARA ? TABLE_PARA : TABLE_FIXED;

        // Select All Query

        if (otherArgs.length == 0) {
            String selectQuery = "SELECT  * FROM " + table ;
            selectQuery += orderby;

            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
        } else {
            String selectQuery = "SELECT  * FROM " + table + " where ";
            List<String> valueList = new ArrayList<String>();

            for (int i = 0; i < otherArgs.length; i+= 2) {
                selectQuery += otherArgs[i] + "=? AND ";
                valueList.add(otherArgs[i+1]);
            }
            selectQuery += " 1 " + orderby;
            String[] values = new String[valueList.size()];
            valueList.toArray(values);

            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(selectQuery, values);
        }



        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemStruct aItem = new ItemStruct();

                aItem.setTitle(LANG.ENGLISH, cursor.getString(1));
                aItem.setText(LANG.ENGLISH, cursor.getString(2));
                aItem.setTitle(LANG.SPANISH, cursor.getString(3));
                aItem.setText(LANG.SPANISH, cursor.getString(4));
                aItem.setImageString(cursor.getString(5));
                aItem.setvImageString(cursor.getString(6));
//                aItem.setImageID(Integer.parseInt(cursor.getString(5)));
//                aItem.setVImageID(Integer.parseInt(cursor.getString(6)));
                aItem.setColor(Color.parseColor(cursor.getString(7)));
                // number 8 is the menu, not need here
                aItem.setSpecialTag(cursor.getString(9));
                aItem.setFreq("hearing", Integer.parseInt(cursor.getString(10)));
                aItem.setFreq("cognitive", Integer.parseInt(cursor.getString(11)));
                aItem.setFreq("nonenglish", Integer.parseInt(cursor.getString(12)));

                // Adding contact to list
                itemList.add(aItem);
            } while (cursor.moveToNext());
        }

        // return contact list
        return itemList;
    }


    public int deleteItem(int transitType, String title) {

        SQLiteDatabase db = getWritableDatabase();

        String table = transitType == CONSTANT.PARA ? TABLE_PARA : TABLE_FIXED;

        return db.delete(table, KEY_TITLE + "= '" + title.replace("'", "''") + "'", null);
    }

    // Updating single item
    public int updateItem(int transitType, String subMenu, ItemStruct item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(subMenu, item.getFreq(subMenu));

        // choose table based on transit type
        String table = transitType == CONSTANT.PARA ? TABLE_PARA : TABLE_FIXED;

        // updating row
        return db.update(table, values, KEY_TITLE + " = ?",
                new String[] { String.valueOf(item.getTitle())});
    }


    public int getMaxFreq(String menu, int transitType) {

        Cursor cursor;
        int count = 0;

        // choose table based on transit type
        String table = transitType == CONSTANT.PARA ? TABLE_PARA : TABLE_FIXED;

        String selectQuery = "SELECT MAX("+KEY_FREQ_HEARING+") FROM " + table + " where " + KEY_MENU + " = ?";
        SQLiteDatabase db = getWritableDatabase();
        cursor = db.rawQuery(selectQuery, new String[]{menu});
        if (cursor.moveToFirst()) {
            count = Integer.parseInt(cursor.getString(0));
        }

        return count;
    }


    public int updateProp(String propKey, String propValue) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_VALUE,propValue);

        // updating row
        return db.update(TABLE_APPINFO, values, KEY_NAME + " = ?",
                new String[] { String.valueOf(propKey)});
    }


}

package com.contacts.sundeep.readcontacts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sundeep on 3/5/16.
 */
public class SQLiteDatabase extends SQLiteOpenHelper implements ContactTable {

    private static final String CREATE_TABLE_STMT =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_RAW_ID + " INTEGER," +
                    COLUMN_NAME + " VARCHAR(50)," +
                    COLUMN_ACCOUNT_NAME + " VARCHAR(50)," +
                    COLUMN_ACCOUNT_TYPE + " VARCHAR(50)," +
                    COLUMN_PHONE_NO + " TEXT" +
                    ")";

    public SQLiteDatabase(Context context) {
        super(context, "SContacts.db", null, 1);
    }


    @Override
    public void onCreate(android.database.sqlite.SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_STMT);
    }

    @Override
    public void onUpgrade(android.database.sqlite.SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
    }

    public long insertContact(Contact contact) {
        android.database.sqlite.SQLiteDatabase wDatabase = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, contact.getId());
        values.put(COLUMN_RAW_ID, contact.getRawId());
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_ACCOUNT_NAME, contact.getEmail());
        values.put(COLUMN_ACCOUNT_TYPE, contact.getAccountType());
        values.put(COLUMN_PHONE_NO, contact.getPhoneNosAsString());
        long insert = -1;
        try {
            insert = wDatabase.insertOrThrow(TABLE_NAME, null, values);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        wDatabase.close();
        return insert;
    }

    public List<Contact> getContacts() {
        List<Contact> contacts = new ArrayList<Contact>();
        android.database.sqlite.SQLiteDatabase rDatabase = getReadableDatabase();
        Cursor query = rDatabase.query(TABLE_NAME, null, null, null, null, null, null);
        if (query == null)
            return contacts;

        if (query.moveToFirst()) {
            do {
                contacts.add(getContact(query));
            } while (query.moveToNext());
        }
        query.close();
        return contacts;
    }

    private Contact getContact(Cursor query) {
        Contact contact = new Contact();
        contact.setId(query.getInt(query.getColumnIndex(COLUMN_ID)));
        contact.setRawId(query.getInt(query.getColumnIndex(COLUMN_RAW_ID)));
        contact.setName(query.getString(query.getColumnIndex(COLUMN_NAME)));
        contact.setEmail(query.getString(query.getColumnIndex(COLUMN_ACCOUNT_NAME)));
        contact.setAccountType(query.getString(query.getColumnIndex(COLUMN_ACCOUNT_TYPE)));
        String[] strings = query.getString(query.getColumnIndex(COLUMN_PHONE_NO)).split(",");
        for (String phno : strings) {
            contact.addPhoneno(phno);
        }
        return contact;
    }
}

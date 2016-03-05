package com.contacts.sundeep.readcontacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Message;
import android.provider.ContactsContract;

import java.util.HashMap;
import java.util.List;

public class ReadContactsTask extends AsyncTask<Void, Void, Void> {

    private HashMap<Long, Contact> contacts = new HashMap<Long, Contact>();
    private MainActivity.StatusHandler handler;
    private Context context;

    public ReadContactsTask(Context context, MainActivity.StatusHandler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.HAS_PHONE_NUMBER}, null, null, null);
        if (cur == null) {
            return null;
        }
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                Contact contact = new Contact();
                long id = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contact.setId(id);
                contact.setName(name);
                contacts.put(id, contact);
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    getPhoneNos(cr, contact, id);
                    getContactAccount(id, cr, contact);
                }
            }

            Message msg = new Message();
            msg.obj = "Read " + contacts.size() + " contacts.";
            handler.sendMessage(msg);

            msg = new Message();
            msg.obj = "Saving contacts...";
            handler.sendMessage(msg);
            // Save contacts to sqlite
            saveContacts(contacts);


            List<Contact> contacts = new SQLiteDatabase(context).getContacts();
            msg = new Message();
            msg.obj = "Saved " + contacts.size() + " contacts in database.";
            handler.sendMessage(msg);

            msg = new Message();
            msg.arg1 = -1;
            msg.obj = "Done.";
            handler.sendMessage(msg);
        }
        cur.close();
        return null;
    }

    private void saveContacts(HashMap<Long, Contact> contacts) {
        SQLiteDatabase database = new SQLiteDatabase(context);
        for (Contact contact : contacts.values()) {
            database.insertContact(contact);
        }
    }

    private void getPhoneNos(ContentResolver cr, Contact contact, long id) {
        Cursor cursor = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(id)}, null);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            String phoneNo = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contact.addPhoneno(phoneNo);
        }
        cursor.close();
    }

    public void getContactAccount(long id, ContentResolver contentResolver, Contact contact) {
        Cursor cursor = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts.ACCOUNT_NAME, ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.RawContacts._ID},
                ContactsContract.RawContacts.CONTACT_ID + "=?",
                new String[]{String.valueOf(id)},
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String aNam = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME));
            String aTyp = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
            long rawId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
            contact.setEmail(aNam);
            contact.setAccountType(aTyp);
            contact.setRawId(rawId);
            cursor.close();
        }
    }

}
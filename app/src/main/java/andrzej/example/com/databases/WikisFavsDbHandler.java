package andrzej.example.com.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import andrzej.example.com.models.WikiFavItem;

/**
 * Created by andrzej on 02.07.15.
 */
public class WikisFavsDbHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "wikisFavs";

    // Contacts table name
    private static final String TABLE_HISTORY = "wikisFavsRecords";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE_URL = "img_url";
    String CREATE_CONTACTS_TABLE;


    public WikisFavsDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, " + KEY_URL + " TEXT, " + KEY_DESCRIPTION + " TEXT, " + KEY_IMAGE_URL + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        // Create tables again
        onCreate(db);
    }

    public void addItem(WikiFavItem item) {

        if (itemExsists(item.getUrl(), item.getTitle())) {
            SQLiteDatabase db = this.getWritableDatabase();

            deleteItemsWithUrl(item.getUrl());

            db.close(); // Closing database connection
        } else {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, item.getTitle());
            values.put(KEY_URL, item.getUrl());
            values.put(KEY_DESCRIPTION, item.getDescription());
            values.put(KEY_IMAGE_URL, item.getImageUrl());

            // Inserting Row
            db.insert(TABLE_HISTORY, null, values);
            db.close(); // Closing database connection
        }
    }


    public void deleteItemsWithUrl(String url) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_URL + " =?",
                new String[]{url});
        db.close();

    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_ID + " =?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void turncateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, " + KEY_URL + " TEXT, " + KEY_DESCRIPTION + " TEXT, " + KEY_IMAGE_URL + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.close();
    }

    public ArrayList<WikiFavItem> getAllFavs() {
        ArrayList<WikiFavItem> contactList = new ArrayList<WikiFavItem>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + KEY_ID + " DESC";


        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    int id = Integer.parseInt(cursor.getString(0));
                    String title = cursor.getString(1);
                    String url = cursor.getString(2);
                    String description = cursor.getString(3);
                    String imageUrl = cursor.getString(4);
                    // Adding contact to list
                    contactList.add(new WikiFavItem(id, title, url, description, imageUrl));
                } while (cursor.moveToNext());
            }
        } catch (IllegalStateException e) {
            Log.e(null, e.getMessage());
        }
        // return contact list
        return contactList;
    }

    public WikiFavItem getWikiFavItemByUrl(String url) {
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " WHERE " + KEY_URL + "='" + url + "' LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        try {
            if (cursor != null)
                cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String URI = cursor.getString(2);
                String description = cursor.getString(3);
                String img_url = cursor.getString(4);

                return new WikiFavItem(id, title, URI, description, img_url);
            }
        } catch (IllegalStateException e) {
            Log.e(null, e.getMessage());
        }

        return null;
    }

    public void editItem(int id, WikiFavItem item) {
        String url = item.getUrl();
        String label = item.getTitle();

        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, label);
        cv.put(KEY_URL, url);
        cv.put(KEY_DESCRIPTION, item.getDescription());
        cv.put(KEY_IMAGE_URL, item.getImageUrl());

        SQLiteDatabase db = this.getReadableDatabase();
        db.update(TABLE_HISTORY, cv, "id=" + id, null);
        db.close();
    }


    public boolean itemExsists(String url) {
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = db.query(TABLE_HISTORY, new String[]{
                            KEY_URL}, KEY_URL + "=?",
                    new String[]{url}, null, null, null, null);

            try {
                if (cursor != null)
                    cursor.moveToFirst();

                if (cursor.getCount() > 0) {
                    return true;
                } else
                    return false;
            } catch (IllegalStateException e) {
                Log.e(null, e.getMessage());
                return false;
            }

    }

    public boolean itemExsistsLabel(String label) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{
                        KEY_NAME}, KEY_NAME + "=?",
                new String[]{label}, null, null, null, null);

        try {
            if (cursor != null)
                cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                return true;
            } else
                return false;
        } catch (IllegalStateException e) {
            Log.e(null, e.getMessage());
            return false;
        }
    }

    public boolean itemExsists(String url, String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{KEY_NAME,
                        KEY_URL}, KEY_URL + "=? AND " + KEY_NAME + "=?",
                new String[]{url, name}, null, null, null, null);

        try {
            if (cursor != null)
                cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                return true;
            } else
                return false;
        } catch (IllegalStateException e) {
            Log.e(null, e.getMessage());
            return false;
        }
    }

    public WikiFavItem getItemByUrl(String url) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{KEY_ID, KEY_NAME,
                        KEY_URL, KEY_DESCRIPTION, KEY_IMAGE_URL}, KEY_URL + "=?",
                new String[]{url}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() > 0)
            return new WikiFavItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        else
            return null;
    }

}
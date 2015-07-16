package andrzej.example.com.databases;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.fragments.SavedArticlesFragment;
import andrzej.example.com.models.BookmarkedArticle;
import andrzej.example.com.models.SearchResult;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;

/**
 * Created by andrzej on 30.06.15.
 */
public class WikisHistoryDbHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "wikisHistory";

    // Contacts table name
    private static final String TABLE_HISTORY = "wikisHistoryRecords";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_URL = "url";
    String CREATE_CONTACTS_TABLE;


    private Context context;

    public WikisHistoryDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, " + KEY_URL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        // Create tables again
        onCreate(db);
    }

    public void editItem(int id, WikiPreviousListItem item) {
        String url = item.getUrl();
        String label = item.getTitle();

        SavedArticlesDbHandler saved_db = new SavedArticlesDbHandler(context);
        List<BookmarkedArticle> articles = saved_db.getAllItemsWithWiki(url);
        for (BookmarkedArticle article : articles) {
            saved_db.editRecord(article.getId(), label);
        }
        saved_db.close();

        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, label);
        cv.put(KEY_URL, url);

        SQLiteDatabase db = this.getReadableDatabase();
        db.update(TABLE_HISTORY, cv, "id=" + id, null);
        db.close();
    }

    public void addItem(WikiPreviousListItem item) {

        if (!itemExsists(item.getUrl())) {
            try {
                SQLiteDatabase db = this.getWritableDatabase();

                SavedArticlesDbHandler saved_db = new SavedArticlesDbHandler(context);
                List<BookmarkedArticle> articles = saved_db.getAllItemsWithWiki(item.getUrl());
                for (BookmarkedArticle article : articles) {
                    saved_db.editRecord(article.getId(), item.getTitle());
                }
                saved_db.close();

                ContentValues values = new ContentValues();
                values.put(KEY_NAME, item.getTitle());
                values.put(KEY_URL, item.getUrl());

                // Inserting Row
                db.insert(TABLE_HISTORY, null, values);
                db.close(); // Closing database connection
            }catch (IllegalStateException e){
                Log.e(null, e.getMessage());
            }
        }
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void turncateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, " + KEY_URL + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.close();
    }

    public ArrayList<WikiPreviousListItem> getAllItems() {
        ArrayList<WikiPreviousListItem> contactList = new ArrayList<WikiPreviousListItem>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + KEY_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                String title = cursor.getString(1);
                String url = cursor.getString(2);
                // Adding contact to list
                contactList.add(new WikiPreviousListItem(id, title, url));
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
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
        }catch (IllegalStateException e){
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
        }catch (IllegalStateException e){
            Log.e(null, e.getMessage());
            return false;
        }
    }

    private void deleteItemsWithUrl(String url) {
        if (itemExsists(url)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_HISTORY, KEY_URL + " =?",
                    new String[]{url});
            db.close();
        }
    }

    public WikiPreviousListItem getItemByUrl(String url) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{KEY_ID, KEY_NAME,
                        KEY_URL}, KEY_URL + "=?",
                new String[]{url}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() > 0)
            return new WikiPreviousListItem(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
        else
            return null;
    }

}

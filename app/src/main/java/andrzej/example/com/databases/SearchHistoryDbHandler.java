package andrzej.example.com.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.models.Article;
import andrzej.example.com.models.SearchResult;
import andrzej.example.com.prefs.BaseConfig;

/**
 * Created by andrzej on 04.06.15.
 */
public class SearchHistoryDbHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "searchHistory";

    // Contacts table name
    private static final String TABLE_HISTORY = "searchHistoryRecords";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_WIKI_ID = "wiki_id";


    public SearchHistoryDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, " + KEY_WIKI_ID + " INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        // Create tables again
        onCreate(db);
    }

    public void addItem(SearchResult item) {

        deleteItemsWithName(item.getTitle());
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getTitle()); // Contact Name
        values.put(KEY_WIKI_ID, item.getId());

        // Inserting Row
        db.insert(TABLE_HISTORY, null, values);
        db.close(); // Closing database connection


    }

    public SearchResult getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{KEY_ID,
                        KEY_NAME, KEY_WIKI_ID}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        SearchResult item = new SearchResult(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(2)),
                cursor.getString(1));
        // return contact
        return item;
    }

    public List<SearchResult> getAllItems() {
        List<SearchResult> contactList = new ArrayList<SearchResult>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + KEY_ID + " DESC LIMIT " + String.valueOf(BaseConfig.searchLimit);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(1);
                int wiki_id = Integer.parseInt(cursor.getString(2));
                // Adding contact to list
                contactList.add(new SearchResult(wiki_id, title));
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void deleteItem(SearchResult contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getDb_id())});
        db.close();
    }

    public boolean itemExsists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{
                        KEY_NAME}, KEY_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public void deleteItemsWithName(String name){
        if(itemExsists(name)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_HISTORY, KEY_NAME + " = ?",
                    new String[]{name});
            db.close();
        }
    }
}

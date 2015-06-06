package andrzej.example.com.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.models.SearchResult;
import andrzej.example.com.prefs.BaseConfig;

/**
 * Created by andrzej on 05.06.15.
 */
public class ArticleHistoryDbHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "articleHistory";

    // Contacts table name
    private static final String TABLE_HISTORY = "articleHistoryRecords";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_WIKI_ID = "wiki_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_THUMBNAIL_URL = "url";
    private static final String KEY_VISIT_DATE = "visited_at";

    private final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT, " + KEY_WIKI_ID + " INTEGER, " + KEY_THUMBNAIL_URL + " TEXT, " + KEY_VISIT_DATE + " TEXT)";


    public ArticleHistoryDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        // Create tables again
        onCreate(db);
    }


    public void addItem(ArticleHistoryItem item) {

        if (!item.getLabel().equals(getLastItem().getLabel())) {
            deleteSmart(item.getLabel(), item.getDateInString());

            ContentValues values = new ContentValues();
            values.put(KEY_NAME, item.getLabel()); // Contact Name
            values.put(KEY_WIKI_ID, item.getId());
            values.put(KEY_THUMBNAIL_URL, item.getThumbnail_url());
            values.put(KEY_VISIT_DATE, String.valueOf(item.getVisited_at()));

            // Inserting Row
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_HISTORY, null, values);
            db.close(); // Closing database connection
        }
    }

    public void deleteSmart(String name, String date) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, new String[]{KEY_ID,
                KEY_NAME, KEY_WIKI_ID, KEY_THUMBNAIL_URL, KEY_VISIT_DATE}, KEY_NAME + "=?", new String[]{name}, null, null, null, null);

        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    int db_id = Integer.parseInt(cursor.getString(0));
                    String title = cursor.getString(1);
                    long milisDate = Long.parseLong(cursor.getString(4));
                    // Adding contact to list
                    String dateString = getDateInString(milisDate);

                    if (dateString.contains(date)) {
                        deleteItem(db_id);
                    }
                } while (cursor.moveToNext());
            }
        }
    }

    public void deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteItemsWithName(String name) {
        if (itemExsists(name)) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_HISTORY, KEY_NAME + " = ?",
                    new String[]{name});
            db.close();
        }
    }

    public ArticleHistoryItem getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{KEY_ID,
                        KEY_NAME, KEY_WIKI_ID, KEY_THUMBNAIL_URL, KEY_VISIT_DATE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ArticleHistoryItem item = new ArticleHistoryItem(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(2)),
                Integer.parseInt(cursor.getString(4)), cursor.getString(1), cursor.getString(3));

        // return contact
        return item;
    }

    public List<ArticleHistoryItem> getAllItemsLike(String search) {
        List<ArticleHistoryItem> itemsList = new ArrayList<ArticleHistoryItem>();

        Cursor cursor = getSearch(KEY_NAME, search);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int db_id = Integer.parseInt(cursor.getString(0));
                    int wiki_id = Integer.parseInt(cursor.getString(2));
                    long time_milis = Long.parseLong(cursor.getString(4));
                    String title = cursor.getString(1);
                    String thumbnail_url = cursor.getString(3);
                    // Adding contact to list
                    itemsList.add(new ArticleHistoryItem(db_id, wiki_id, time_milis, title, thumbnail_url));
                } while (cursor.moveToNext());
            }

            return itemsList;
        }

        return null;
    }

    public Cursor getSearch(String field,
                            String search) {
        SQLiteDatabase sampleDB = this.getReadableDatabase();
        Cursor c = sampleDB.rawQuery("SELECT * FROM "
                + TABLE_HISTORY + " where " + field + " like '%" + search
                + "%' ORDER BY " + KEY_VISIT_DATE + " DESC", null);
        return c;
    }

    public List<ArticleHistoryItem> getAllItems() {
        List<ArticleHistoryItem> contactList = new ArrayList<ArticleHistoryItem>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + KEY_VISIT_DATE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int db_id = Integer.parseInt(cursor.getString(0));
                int wiki_id = Integer.parseInt(cursor.getString(2));
                long time_milis = Long.parseLong(cursor.getString(4));
                String title = cursor.getString(1);
                String thumbnail_url = cursor.getString(3);
                // Adding contact to list
                contactList.add(new ArticleHistoryItem(db_id, wiki_id, time_milis, title, thumbnail_url));
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public ArticleHistoryItem getLastItem() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{KEY_ID,
                KEY_NAME, KEY_WIKI_ID, KEY_THUMBNAIL_URL, KEY_VISIT_DATE}, null, null, null, null, null);

        if (cursor != null)
            cursor.moveToLast();

        if (cursor.getCount() > 0) {
            ArticleHistoryItem item = new ArticleHistoryItem(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(2)),
                    Long.parseLong(cursor.getString(4)), cursor.getString(1), cursor.getString(3));

            // return contact

            return item;
        }
        return new ArticleHistoryItem();
    }



    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void turncateTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL(CREATE_CONTACTS_TABLE);
        db.close();
    }


    public boolean itemExsists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{
                        KEY_NAME, KEY_VISIT_DATE}, KEY_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }


    public String getDateInString(long time) {
        Calendar c = Calendar.getInstance();

        //Set time in milliseconds
        c.setTimeInMillis(time);
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        return mDay + " " + ArticleHistoryItem.getMonthName(mMonth) + " " + mYear;
    }
}
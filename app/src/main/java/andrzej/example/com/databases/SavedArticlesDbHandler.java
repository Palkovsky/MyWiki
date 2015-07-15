package andrzej.example.com.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.models.BookmarkedArticle;
import andrzej.example.com.prefs.APIEndpoints;

/**
 * Created by andrzej on 15.07.15.
 */
public class SavedArticlesDbHandler  extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "savedArticles";

    // Contacts table name
    private static final String TABLE_HISTORY = "savedArticles";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_THUMBNAIL_URL = "url";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_WIKI_NAME = "wiki";
    private static final String KEY_WIKI_URL = "wiki_url";

    private final String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT, " + KEY_THUMBNAIL_URL + " TEXT, " + KEY_CONTENT + " TEXT, " + KEY_WIKI_NAME + " TEXT, "+ KEY_WIKI_URL +" TEXT)";

    public SavedArticlesDbHandler(Context context) {
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

    public void addItem(BookmarkedArticle item){
        if(!itemExsists(item.getTitle(), item.getWikiUrl())){
            ContentValues values = new ContentValues();
            values.put(KEY_TITLE, item.getTitle());
            values.put(KEY_THUMBNAIL_URL, item.getImgUrl());
            values.put(KEY_WIKI_NAME, item.getWikiName());
            values.put(KEY_CONTENT, item.getContent());
            values.put(KEY_WIKI_URL, item.getWikiUrl());

            // Inserting Row
            SQLiteDatabase db = this.getWritableDatabase();
            db.insert(TABLE_HISTORY, null, values);
            db.close(); // Closing database connection
        }
    }

    public List<BookmarkedArticle> getAllItems(){
        List<BookmarkedArticle> contactList = new ArrayList<BookmarkedArticle>();
        // Select All Query

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, new String[]{KEY_ID, KEY_TITLE,
                        KEY_THUMBNAIL_URL, KEY_CONTENT, KEY_WIKI_NAME, KEY_WIKI_URL},
                null, null, null, null, KEY_ID + " DESC, " + KEY_WIKI_NAME + " ASC");

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String imgUrl = cursor.getString(2);
                String content = cursor.getString(3);
                String wikiName = cursor.getString(4);
                String wikiUrl = cursor.getString(5);
                contactList.add(new BookmarkedArticle(id, title, imgUrl, content, wikiName, wikiUrl));
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public List<BookmarkedArticle> getAllItemsLike(String query){
        List<BookmarkedArticle> contactList = new ArrayList<BookmarkedArticle>();

        SQLiteDatabase sampleDB = this.getReadableDatabase();
        Cursor cursor = sampleDB.rawQuery("SELECT * FROM "
                + TABLE_HISTORY + " where " + KEY_TITLE + " like '%" + query
                + "%' ORDER BY " + KEY_ID + " DESC", new String[]{});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String imgUrl = cursor.getString(2);
                String content = cursor.getString(3);
                String wikiName = cursor.getString(4);
                String wikiUrl = cursor.getString(5);
                contactList.add(new BookmarkedArticle(id, title, imgUrl, content, wikiName, wikiUrl));
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }


    public boolean itemExsists(String name, String wikiUrl) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_HISTORY, new String[]{
                        KEY_TITLE, KEY_WIKI_NAME}, KEY_TITLE + "=? AND "+ KEY_WIKI_URL + "=?",
                new String[]{name, wikiUrl}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }
}
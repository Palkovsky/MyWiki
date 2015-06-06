package andrzej.example.com.models;

import android.content.Context;

import java.util.Calendar;

import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;

/**
 * Created by andrzej on 05.06.15.
 */
public class ArticleHistoryItem {
    private int db_id;
    private int id;
    private long visited_at;
    private String label;
    private String thumbnail_url;

    public ArticleHistoryItem() {
    }

    public ArticleHistoryItem(int id, long visited_at, String label, String thumbnail_url) {
        this.id = id;
        this.visited_at = visited_at;
        this.label = label;
        this.thumbnail_url = thumbnail_url;
    }

    public ArticleHistoryItem(int db_id, int id, long visited_at, String label, String thumbnail_url) {
        this.db_id = db_id;
        this.id = id;
        this.visited_at = visited_at;
        this.label = label;
        this.thumbnail_url = thumbnail_url;
    }

    public int getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getVisited_at() {
        return visited_at;
    }

    public void setVisited_at(long visited_at) {
        this.visited_at = visited_at;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getDateInString() {
        Calendar c = Calendar.getInstance();

        //Set time in milliseconds
        c.setTimeInMillis(getVisited_at());
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        return mDay + " " + getMonthName(mMonth) + " " + mYear;
    }

    private String getMonthName(int num) {

        Context c = MyApplication.getAppContext();

        switch (num) {
            case 1: //Jan
                return c.getResources().getString(R.string.january);
            case 2: //Feb
                return c.getResources().getString(R.string.feburary);
            case 3: //March
                return c.getResources().getString(R.string.march);
            case 4: //April
                return c.getResources().getString(R.string.april);
            case 5: //May
                return c.getResources().getString(R.string.may);
            case 6: //June
                return c.getResources().getString(R.string.june);
            case 7: //July
                return c.getResources().getString(R.string.july);
            case 8: //Aug
                return c.getResources().getString(R.string.august);
            case 9: //Sep
                return c.getResources().getString(R.string.september);
            case 10: //Oct
                return c.getResources().getString(R.string.october);
            case 11: //Nov
                return c.getResources().getString(R.string.november);
            case 12: //Dec
                return c.getResources().getString(R.string.december);
        }

        return null;
    }
}

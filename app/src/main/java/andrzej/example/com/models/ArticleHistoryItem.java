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

    public String getScaledDownImage() {
        String thumbnail_url = getThumbnail_url();

        if(thumbnail_url!=null) {
            String chunk_string_beg = "/window-crop/width/";
            String chunk_string_end = "/x-offset/";

            int index_beg = thumbnail_url.indexOf("/window-crop/width/") + chunk_string_beg.length();
            int index_end = thumbnail_url.indexOf(chunk_string_end);

            if(index_beg>0 && index_end>0 && index_beg<thumbnail_url.length() && index_end<thumbnail_url.length()) {
                String width_substring = thumbnail_url.substring(index_beg, index_end);
                return thumbnail_url.replaceFirst(width_substring, String.valueOf(64));
            }
            return getThumbnail_url();
        }

        return null;
    }

    public static String getMonthName(int num) {

        Context c = MyApplication.getAppContext();

        switch (num) {
            case 0: //Jan
                return c.getResources().getString(R.string.january);
            case 1: //Feb
                return c.getResources().getString(R.string.feburary);
            case 2: //March
                return c.getResources().getString(R.string.march);
            case 3: //April
                return c.getResources().getString(R.string.april);
            case 4: //May
                return c.getResources().getString(R.string.may);
            case 5: //June
                return c.getResources().getString(R.string.june);
            case 6: //July
                return c.getResources().getString(R.string.july);
            case 7: //Aug
                return c.getResources().getString(R.string.august);
            case 8: //Sep
                return c.getResources().getString(R.string.september);
            case 9: //Oct
                return c.getResources().getString(R.string.october);
            case 10: //Nov
                return c.getResources().getString(R.string.november);
            case 11: //Dec
                return c.getResources().getString(R.string.december);
        }

        return null;
    }
}

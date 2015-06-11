package andrzej.example.com.models;

import android.widget.TextView;

/**
 * Created by andrzej on 11.06.15.
 */
public class ArticleHeader {

    private String title;
    private TextView view;
    private int level;

    public ArticleHeader(int level, String title, TextView view) {
        this.level = level;
        this.title = title;
        this.view = view;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TextView getView() {
        return view;
    }

    public void setView(TextView view) {
        this.view = view;
    }
}

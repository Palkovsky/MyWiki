package andrzej.example.com.models;

/**
 * Created by andrzej on 27.07.15.
 */
public class MainPageArticle {
    public static final String KEY_ITEMS = "items";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";

    private int id;
    private String title;

    public MainPageArticle(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

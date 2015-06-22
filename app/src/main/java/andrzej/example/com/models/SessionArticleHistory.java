package andrzej.example.com.models;

/**
 * Created by andrzej on 22.06.15.
 */
public class SessionArticleHistory {
    private int id;
    private String title;

    public SessionArticleHistory(int id, String title) {
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

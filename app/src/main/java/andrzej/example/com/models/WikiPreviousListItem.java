package andrzej.example.com.models;

/**
 * Created by andrzej on 30.06.15.
 */
public class WikiPreviousListItem {
    private int id;
    private String title;
    private String url;

    public WikiPreviousListItem(){}

    public WikiPreviousListItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public WikiPreviousListItem(int id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

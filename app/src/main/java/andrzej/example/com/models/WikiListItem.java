package andrzej.example.com.models;

/**
 * Created by andrzej on 30.06.15.
 */
public class WikiListItem {
    private String title;
    private String url;

    public WikiListItem(){}

    public WikiListItem(String title, String url) {
        this.title = title;
        this.url = url;
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

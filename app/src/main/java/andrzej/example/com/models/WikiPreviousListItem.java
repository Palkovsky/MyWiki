package andrzej.example.com.models;

/**
 * Created by andrzej on 30.06.15.
 */
public class WikiPreviousListItem {
    private int id;
    private String title;
    private String url;
    private String description;
    private String imageUrl;

    public WikiPreviousListItem(){}

    public WikiPreviousListItem(String title, String url, String description, String imageUrl) {
        this.title = title;
        this.url = url;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public WikiPreviousListItem(int id, String title, String url, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.description = description;
        this.imageUrl = imageUrl;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

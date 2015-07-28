package andrzej.example.com.models;


/**
 * Created by andrzej on 10.07.15.
 */
public class SuggestedItem{

    public static final String ID_FIELD = "id";
    public static final String TITLE_FIELD = "title";
    public static final String URL_FIELD = "url";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String IMAGE_FIELD = "logo_url";
    public static final String PAGE_COUNT_FIELD = "page count";

    private int id;
    private String url;
    private String title;
    private String description;
    private String imageUrl;
    private int pages;


    public SuggestedItem(int id, String url, String title, String description, String imageUrl, int pages) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.pages = pages;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }
}

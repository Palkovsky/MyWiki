package andrzej.example.com.models;


/**
 * Created by andrzej on 10.07.15.
 */
public class SuggestedItem{
    private int id;
    private String url;
    private String title;
    private String description;
    private String imageUrl;



    public SuggestedItem(int id, String url, String title, String description, String imageUrl) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
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

}

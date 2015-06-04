package andrzej.example.com.models;

/**
 * Created by andrzej on 02.06.15.
 */
public class Article {

    public static final String KEY_ITEMS = "items";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ID = "id";
    public static final String KEY_IMAGE_URL = "thumbnail";
    public static final String KEY_ABSTRACT = "abstract";

    private int id;
    private int db_id;
    private String title;
    private String thumbnail_url;
    private String revision;

    public Article() {
    }

    public Article(int db_id, int id, String title) {
        this.db_id = db_id;
        this.id = id;
        this.title = title;
    }

    public Article(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public Article(int id, String title, String thumbnail_url, String revision) {
        this.id = id;
        this.title = title;
        this.thumbnail_url = thumbnail_url;
        this.revision = revision;
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

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public int getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
    }
}

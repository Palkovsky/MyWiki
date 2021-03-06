package andrzej.example.com.models;

/**
 * Created by andrzej on 15.07.15.
 */
public class BookmarkedArticle {
    private int id;
    private int article_id;
    private String wikiName;
    private String title;
    private String imgUrl;
    private String content;
    private String wikiUrl;

    public BookmarkedArticle(String title, String imgUrl, String content, String wikiName, String wikiUrl, int article_id) {
        this.wikiName = wikiName;
        this.title = title;
        this.imgUrl = imgUrl;
        this.content = content;
        this.wikiUrl = wikiUrl;
        this.article_id = article_id;
    }

    public BookmarkedArticle(int id, String title, String imgUrl, String content, String wikiName, String wikiUrl, int article_id) {
        this.id = id;
        this.wikiName = wikiName;
        this.title = title;
        this.imgUrl = imgUrl;
        this.content = content;
        this.wikiUrl = wikiUrl;
        this.article_id = article_id;
    }

    public int getArticle_id() {
        return article_id;
    }

    public void setArticle_id(int article_id) {
        this.article_id = article_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWikiName() {
        return wikiName;
    }

    public void setWikiName(String wikiName) {
        this.wikiName = wikiName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }
}

package andrzej.example.com.models;

import java.util.List;

/**
 * Created by andrzej on 07.06.15.
 */
public class ArticleSection {
    private int level;
    private String title;
    private String formatedContent;
    private List<ArticleImage> imgs;

    public ArticleSection() {
    }

    public ArticleSection(int level, String title, String formatedContent, List<ArticleImage> imgs) {
        this.level = level;
        this.title = title;
        this.formatedContent = formatedContent;
        this.imgs = imgs;
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

    public String getFormatedContent() {
        return formatedContent;
    }

    public void setFormatedContent(String formatedContent) {
        this.formatedContent = formatedContent;
    }

    public List<ArticleImage> getImgs() {
        return imgs;
    }

    public void setImgs(List<ArticleImage> imgs) {
        this.imgs = imgs;
    }
}

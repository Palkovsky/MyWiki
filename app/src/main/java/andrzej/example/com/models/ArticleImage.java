package andrzej.example.com.models;

/**
 * Created by andrzej on 05.06.15.
 */
public class ArticleImage {

    public static final String KEY_IMAGES = "images";
    public static final String KEY_CAPTION = "caption";
    public static final String KEY_SRC = "src";
    public static final String KEY_ORGINAL_DIMENS = "original_dimensions";
    public static final String KEY_WIDTH = "width";
    public static final String KEY_HEIGHT = "height";

    private String img_url;
    private String label;
    private int position;

    public ArticleImage(String img_url, int position) {
        this.img_url = img_url;
        this.position = position;
    }

    public ArticleImage(String img_url, String label, int position) {
        this.img_url = img_url;
        this.label = label;
        this.position = position;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}

package andrzej.example.com.models;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by andrzej on 21.06.15.
 */
public class Recommendation {

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMGURL = "imgUrl";


    private int id;
    private String title;
    private String imgUrl;
    private int positon;

    public Recommendation(int id, String title, String imgUrl, int positon) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
        this.positon = positon;
    }

    public String getSquaredImage(int size) {
        //http://vignette4.wikia.nocookie.net/mlp/images/b/b1/Cloudsdale.png/revision/latest/window-crop/width/200/x-offset/0/y-offset/108/window-width/1920/window-height/960?cb=20120612090025&path-prefix=pl
        String imgUrl = getImgUrl();
        String chunk_beg = "/latest/window-crop/width/";
        String chunk_end = "/x-offset/";

        if (imgUrl != null && imgUrl.trim().length() > 0 && imgUrl.contains(chunk_beg)) {
            int start_index = imgUrl.indexOf(chunk_beg) + chunk_beg.length();
            int enx_index = imgUrl.indexOf(chunk_end);

            String sizeSubstring = imgUrl.substring(start_index, enx_index);

            if (StringUtils.countMatches(imgUrl, '/'+sizeSubstring+'/') == 1)
                imgUrl = imgUrl.replaceFirst('/' + sizeSubstring + '/', '/' + String.valueOf(size) + '/');
        }

        return imgUrl;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getPositon() {
        return positon;
    }

    public void setPositon(int positon) {
        this.positon = positon;
    }
}

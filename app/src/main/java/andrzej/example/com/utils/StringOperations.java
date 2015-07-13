package andrzej.example.com.utils;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

/**
 * Created by andrzej on 07.06.15.
 */
public class StringOperations {

    private static String[] extensionList = {".jpg/", ".jpeg/", ".png/", ".bmp/", ".gif/"};

    public static String pumpUpSize(String url, int size) {
        String size_str = String.valueOf(size);

        if (url.contains("/scale-to-width/") && !url.contains("/scale-to-width-down/")) {
            String chunk_string_beg = "/scale-to-width/";
            String chunk_string_end = "?cb=";

            int index_beg = url.indexOf(chunk_string_beg) + chunk_string_beg.length();
            int index_end = url.indexOf(chunk_string_end);

            if (index_beg <= 0 || index_end <= 0 || index_beg >= url.length() || index_end > url.length())
                return url;
            else {
                try {
                    String width_substring = url.substring(index_beg, index_end);
                    String url_return;

                    if (StringUtils.countMatches(url, width_substring) > 1) {
                        URL uri = new URL(url.replaceFirst("/scale-to-width/" + width_substring, "/scale-to-width/" + size_str));
                        return uri.toString();
                    } else {
                        url_return = url.replaceFirst(width_substring, size_str);
                    }

                    URL url_check = new URL(url_return);
                    return url_return;
                } catch (MalformedURLException e) {
                    return url;
                }
            }

        } else if (url.contains("/scale-to-width-down/")) {

            String chunk_beg = "/scale-to-width-down/";
            String chunk_end = "?cb=";

            int index_start = url.indexOf(chunk_beg) + chunk_beg.length();
            int index_end = url.indexOf(chunk_end);

            String currentSize = url.substring(index_start, index_end);

            url = url.replaceFirst("/scale-to-width-down/" + currentSize, "/scale-to-width-down/" + size_str);

            Log.e(null, "upgraded url: " + url);
            return url;
        } else if (url.contains("/latest/window-crop/width")) {

            /*
            Za bardzo rozciąga
            String chunk_string_beg = "/latest/window-crop/width/";
            String chunk_string_end = "/x-offset/";
            int index_beg = url.indexOf(chunk_string_beg) + chunk_string_beg.length();
            int index_end = url.indexOf(chunk_string_end);
            String initialSize = url.substring(index_beg, index_end);

            if(StringUtils.countMatches(url, initialSize)==1)
                url = url.replaceFirst(initialSize, size_str);
            */

            return url;

        } else if(stringContainsItemFromList(url, extensionList) && url.contains("px-")) {
            String pxSuffix = "px-";
            int indexEnd = url.indexOf(pxSuffix);
            String firstContained = getFirstContainedItem(url, extensionList);
            int indexBeg = url.indexOf(firstContained) + firstContained.length();

            Log.e(null, "URL przed: " + url);
            Log.e(null, "indexEnd: " + indexEnd);
            Log.e(null, "indexBeg: " + indexBeg);

            if(indexBeg<url.length() && indexBeg>0 && indexEnd>0 && indexEnd<=url.length() && indexEnd>indexBeg) {
                String currentSize = url.substring(indexBeg, indexEnd);
                url = url.replaceFirst(firstContained + currentSize + pxSuffix, firstContained + size_str + pxSuffix);
            }

            Log.e(null, "URL PO:  " + url);

            return url;

        }else {
            //http://vignette4.wikia.nocookie.net/mlp/images/e/e9/Dreppy_x666.png/revision/latest?cb=20130715174906&path-prefix=pl
            String chunk_string_beg = "/latest";

            int index_beg = url.indexOf(chunk_string_beg) + chunk_string_beg.length();
            int index_end = index_beg + 1;

            if (index_beg <= 0 || index_end <= 0 || index_beg >= url.length() || index_end > url.length())
                return url;
            else {
                try {
                    String return_url = url.replaceFirst("\\/latest", "/latest/scale-to-width/" + size_str);
                    URL url_validation = new URL(return_url);
                    return return_url;
                } catch (MalformedURLException e) {
                    return url;
                }

            }
        }

    }

    public static String pumpUpResolution(int width, String thumbnail_url) {
        String chunk_string_beg = "/window-crop/width/";
        String chunk_string_end = "/x-offset/";

        int index_beg = thumbnail_url.indexOf("/window-crop/width/") + chunk_string_beg.length();
        int index_end = thumbnail_url.indexOf(chunk_string_end);

        Log.e(null, "Dad thumbnail URL: " + thumbnail_url);

        if(index_beg>0 && index_beg<thumbnail_url.length() && index_end>0 && index_end<thumbnail_url.length() && index_beg<index_end) {
            String width_substring = thumbnail_url.substring(index_beg, index_end);
            return thumbnail_url.replaceFirst(width_substring, String.valueOf(width));
        }
        return thumbnail_url;
    }

    public static String generateRandomString(int len) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static boolean stringContainsItemFromList(String inputString, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (inputString.contains(items[i])) {
                return true;
            }
        }
        return false;
    }

    private static String getFirstContainedItem(String inputString, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (inputString.contains(items[i])) {
                return items[i];
            }
        }
        return null;
    }

    public static String stripUpWikiUrl(String url){

        String httpSuffix = "http://";
        String wikiaSuffix = ".wikia.com";
        if(url.startsWith(httpSuffix))
            url = url.substring(httpSuffix.length(), url.length());

        if(url.contains(wikiaSuffix)){
            url = url.substring(0, url.indexOf(wikiaSuffix));
        }

        return url;
    }

}

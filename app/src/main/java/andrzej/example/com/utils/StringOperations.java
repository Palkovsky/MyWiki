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

    public static String pumpUpSize(String url, int size) {
        String size_str = String.valueOf(size);
        Log.e(null, "Oryginał: " + url);

        if (url.contains("/scale-to-width/")) {
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
                        return url;
                    } else {
                        url_return = url.replaceFirst(width_substring, size_str);
                    }

                    URL url_check = new URL(url_return);
                    Log.e(null, url_return);
                    return url_return;
                } catch (MalformedURLException e) {
                    return url;
                }
            }

        } else

        {
            //http://vignette4.wikia.nocookie.net/mlp/images/e/e9/Dreppy_x666.png/revision/latest?cb=20130715174906&path-prefix=pl
            String chunk_string_beg = "/latest";

            int index_beg = url.indexOf(chunk_string_beg) + chunk_string_beg.length();
            int index_end = index_beg + 1;

            if (index_beg <= 0 || index_end <= 0 || index_beg >= url.length() || index_end > url.length())
                return url;
            else {
                Log.e(null, url.replaceFirst("\\/latest", "/latest/scale-to-width/" + size_str));
                try {
                    String return_url = url.replaceFirst("\\/latest", "/latest/scale-to-width/" + size_str);
                    URL url_validation = new URL(return_url);
                    Log.e(null, return_url);
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

        String width_substring = thumbnail_url.substring(index_beg, index_end);

        return thumbnail_url.replaceFirst(width_substring, String.valueOf(width));
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
}

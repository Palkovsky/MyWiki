package andrzej.example.com.utils;

import android.util.Log;

/**
 * Created by andrzej on 07.06.15.
 */
public class StringOperations {

    public static String pumpUpSize(String url, int size) {
        String size_str = String.valueOf(size);

        if (url.contains("/scale-to-width/")) {
            String chunk_string_beg = "/scale-to-width/";
            String chunk_string_end = "?cb=";

            int index_beg = url.indexOf(chunk_string_beg) + chunk_string_beg.length();
            int index_end = url.indexOf(chunk_string_end);


            if (index_beg <= 0 || index_end <= 0 || index_beg >= url.length() || index_end > url.length())
                return url;
            else {
                String width_substring = url.substring(index_beg, index_end);

                return url.replaceFirst(width_substring, size_str);
            }
        } else {
            //http://vignette4.wikia.nocookie.net/mlp/images/e/e9/Dreppy_x666.png/revision/latest?cb=20130715174906&path-prefix=pl
            String chunk_string_beg = "/latest";

            int index_beg = url.indexOf(chunk_string_beg) + chunk_string_beg.length();
            int index_end = index_beg+1;

            if (index_beg <= 0 || index_end <= 0 || index_beg >= url.length() || index_end > url.length())
                return url;
            else {
                Log.e(null, url.replaceFirst("\\/latest", "/latest/scale-to-width/"+size_str));
                return url.replaceFirst("\\/latest", "/latest/scale-to-width/"+size_str);
            }
        }
    }
}

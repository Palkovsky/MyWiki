package andrzej.example.com.utils;

import android.content.Context;

import andrzej.example.com.databases.WikisFavsDbHandler;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;

/**
 * Created by andrzej on 02.07.15.
 */
public class WikiManagementHelper {

    Context c;

    WikisHistoryDbHandler previous_db;
    WikisFavsDbHandler favs_db;

    public WikiManagementHelper(Context c) {
        this.c = c;
        previous_db = new WikisHistoryDbHandler(getContext());
        favs_db = new WikisFavsDbHandler(getContext());
    }

    public void addWikiToPreviouslyUsed(String label, String url) {
        previous_db.addItem(new WikiPreviousListItem(label, cleanInputUrl(url)));
    }

    public void addWikiToFavs(String label, String url){
        favs_db.addItem(new WikiFavItem(label, url));
    }

    public void closeDbs(){
        previous_db.close();
        favs_db.close();
    }


    public String cleanInputUrl(String url) {
        url = url.toLowerCase();

        //In case of not needed slash at the end
        if (url.endsWith("/"))
            url = url.substring(0, url.length() - 1);

        // If someone pasts sth like that
        // http://pl.starwars.wikia.com/wiki/Strona_g%C5%82%C3%B3wna
        // Just throw not needed part of the link out
        String com_suffix = ".com";
        if (url.contains(com_suffix)) {
            int index_end = url.indexOf(com_suffix) + com_suffix.length();
            url = url.substring(0, index_end);
        } else {
            if (!url.contains("wikia" + com_suffix)) {
                if (url.endsWith("."))
                    url += "wikia" + com_suffix;
                else
                    url += ".wikia" + com_suffix;
            }
        }

        String http_suffix = "http://";
        if(!url.startsWith(http_suffix))
            url = http_suffix + url;

        return url;
    }

    public String stripUpWikiUrl(String url) {
        String httpSuffix = "http://";
        String wikiaSuffix = ".wikia.com";
        if (url.startsWith(httpSuffix))
            url = url.substring(httpSuffix.length(), url.length());

        if (url.contains(wikiaSuffix)) {
            url = url.substring(0, url.indexOf(wikiaSuffix));
        }

        return url;
    }

    private Context getContext() {
        return c;
    }
}

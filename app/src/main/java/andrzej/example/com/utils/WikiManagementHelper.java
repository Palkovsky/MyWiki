package andrzej.example.com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.databases.WikisFavsDbHandler;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.fragments.ManagementTabs.FavouriteWikisFragment;
import andrzej.example.com.fragments.ManagementTabs.PreviouslyUsedWikisFragment;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SuggestedItem;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.SharedPrefsKeys;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by andrzej on 02.07.15.
 */
public class WikiManagementHelper {

    Context c;

    WikisHistoryDbHandler previous_db;
    WikisFavsDbHandler favs_db;

    SharedPreferences prefs;

    public WikiManagementHelper(Context c) {
        this.c = c;
        previous_db = new WikisHistoryDbHandler(getContext());
        favs_db = new WikisFavsDbHandler(getContext());
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public void addWikiToPreviouslyUsed(WikiPreviousListItem item) {
        if (item.getTitle() != null && item.getTitle().trim().length() > 0)
            previous_db.addItem(new WikiPreviousListItem(item.getTitle(), cleanInputUrl(item.getUrl()), item.getDescription(), item.getImageUrl()));
        else
            previous_db.addItem(new WikiPreviousListItem(stripUpWikiUrl(item.getUrl()), cleanInputUrl(item.getUrl()), item.getDescription(), item.getImageUrl()));
    }

    public void addWikiToFavs(String label, String url, String description, String img_url) {
        favs_db.addItem(new WikiFavItem(label, url, description, img_url));
    }

    public WikiFavItem getItemByLabel(String url){
        return favs_db.getWikiFavItemByUrl(cleanInputUrl(url));
    }

    public void closeDbs() {
        previous_db.close();
        favs_db.close();
    }

    public void editFavItem(int id, WikiFavItem item){
        favs_db.editItem(id, item);
    }

    public void editPrevItem(int id, WikiPreviousListItem item){
        previous_db.editItem(id, item);
    }

    public void editPrevAndFavItem(int id, String label, String url, String oldUrl){

        if(label==null || label.trim().length()<=0)
            label = stripUpWikiUrl(url);

        if(doesFavItemExsistsUrl(oldUrl)) {
            WikiFavItem favItem = new WikiFavItem(label, url, null, null);
            editFavItem(id, favItem);
        }

        if (previous_db.itemExsists(oldUrl)) {
            WikiPreviousListItem previousListItem = previous_db.getItemByUrl(oldUrl);
            previousListItem.setTitle(label);
            previousListItem.setUrl(url);
            editPrevItem(previousListItem.getId(), previousListItem);
        }
    }

    public void editFavAndPrevItem(int id, String label, String url, String oldUrl){

        if(label==null || label.trim().length()<=0)
            label = stripUpWikiUrl(url);

        if(previous_db.itemExsists(oldUrl)) {
            WikiPreviousListItem favItem = new WikiPreviousListItem(label, url, null, null);
            editPrevItem(id, favItem);
        }

        if (favs_db.itemExsists(oldUrl)) {
            WikiFavItem wikiFavItem = favs_db.getItemByUrl(oldUrl);
            wikiFavItem.setTitle(label);
            wikiFavItem.setUrl(url);
            editFavItem(wikiFavItem.getId(), wikiFavItem);
        }
    }

    public ArrayList<WikiFavItem> getAllFavs() {
        return favs_db.getAllFavs();
    }

    public ArrayList<WikiPreviousListItem> getAllWikis(){
        return previous_db.getAllItems();
    }

    public void removeFav(int id) {
        favs_db.deleteItem(id);
    }

    public void removeWikiFromAll(int id){
        previous_db.deleteItem(id);
    }

    public void removeWiki(int id){
        favs_db.deleteItem(id);
        previous_db.deleteItem(id);
    }

    public void clearFavs() {
        favs_db.turncateTable();
    }

    public void setCurrentWiki(String label, String url) {
        //previous_db.addItem(new WikiPreviousListItem(label, url));
        PreviouslyUsedWikisFragment.updateRecords();

        APIEndpoints.WIKI_NAME = url;
        setUrlAsPreference(APIEndpoints.WIKI_NAME, label);
        APIEndpoints.reInitEndpoints();
        if (label == null)
            MainActivity.account.setSubTitle(stripUpWikiUrl(url));
        else
            MainActivity.account.setSubTitle(label);
        MainActivity.account.setTitle(APIEndpoints.WIKI_NAME);
        ((MaterialNavigationDrawer) getContext()).notifyAccountDataChanged();

        PreviouslyUsedWikisFragment.updateRecords();
        FavouriteWikisFragment.updateDataset();
    }

    public void setCurrentWikiWithoutUpdating(String label, String url){
        APIEndpoints.WIKI_NAME = url;
        setUrlAsPreference(APIEndpoints.WIKI_NAME, label);
        APIEndpoints.reInitEndpoints();
        if (label == null)
            MainActivity.account.setSubTitle(stripUpWikiUrl(url));
        else
            MainActivity.account.setSubTitle(label);
        MainActivity.account.setTitle(APIEndpoints.WIKI_NAME);
        ((MaterialNavigationDrawer) getContext()).notifyAccountDataChanged();
    }

    public void setUrlAsPreference(String url, String label) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedPrefsKeys.CURRENT_WIKI_URL, url);

        if (label != null && label.trim().length() > 0)
            editor.putString(SharedPrefsKeys.CURRENT_WIKI_LABEL, label);
        else
            editor.putString(SharedPrefsKeys.CURRENT_WIKI_LABEL, stripUpWikiUrl(url));

        editor.apply();
    }

    public void removeFavByUrl(String url){
        favs_db.deleteItemsWithUrl(url);
    }

    public boolean doesItemExsistsLabel(String label) {
        if (previous_db.itemExsistsLabel(label)) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.label_exsists), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public boolean doesItemExsistsUrl(String url) {
        if (previous_db.itemExsists(url)) {
            Toast.makeText(getContext(), getContext().getResources().getString(R.string.url_exsists), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public boolean itemPrevExsists(String label, String url) {
        url = cleanInputUrl(url);
        if (doesItemExsistsLabel(label) || doesItemExsistsUrl(url))
            return true;
        return false;
    }


    public boolean doesFavItemExsistsLabel(String label) {
        if (favs_db.itemExsistsLabel(label))
            return true;
        return false;
    }

    public boolean doesFavItemExsistsUrl(String url) {
        url = cleanInputUrl(url);
        if (favs_db.itemExsists(url))
            return true;
        return false;
    }

    public boolean doesItemFavExsists(String label, String url) {
        url = cleanInputUrl(url);
        if (doesFavItemExsistsUrl(url) || doesFavItemExsistsLabel(label))
            return true;
        return false;
    }

    public boolean universalItemExsistsEdit(String label, String url, String currentUrl){
        currentUrl = cleanInputUrl(currentUrl);
        url = cleanInputUrl(url);
        if(label==null || label.trim().length()<=0)
            label = stripUpWikiUrl(url);

        if(!currentUrl.equals(url)) {
            if (previous_db.itemExsists(url) || doesFavItemExsistsUrl(url)) {
                Toast.makeText(getContext(), getContext().getResources().getString(R.string.url_exsists), Toast.LENGTH_SHORT).show();
                return true;
            }
        }else{
            return false;
        }
        return false;
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
        if (!url.startsWith(http_suffix))
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

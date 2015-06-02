package andrzej.example.com.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.HistoryFragment;
import andrzej.example.com.fragments.MainFragment;
import andrzej.example.com.fragments.SearchResultsFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.DrawerImages;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MainActivity extends MaterialNavigationDrawer {

    private static Context context;

    //Sekcje, które muszą być globalne.
    MaterialSection section_main;
    MaterialSection section_random;
    MaterialSection section_history;
    MaterialSection section_settings;

    @Override
    public void init(Bundle savedInstanceState) {

        MainActivity.context = getApplicationContext();

        allowArrowAnimation();

        int[] images = DrawerImages.drawer_images;
        Random r = new Random();
        int random_int = r.nextInt(images.length);

        this.setDrawerHeaderImage(images[random_int]);


        section_main = newSection("Strona główna", new MainFragment());
        addSection(section_main);

        section_random = newSection("Losowa strona", new ArticleFragment());
        addSection(section_random);


        section_history = newSection("Historia", new HistoryFragment());
        addSection(section_history);


        section_settings = newSection("Ustawienia", new MainFragment());
        section_settings.select();
        addBottomSection(section_settings);

    }


    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                if(NetworkUtils.isNetworkAvailable(MyApplication.getAppContext())) {
                    SearchResultsFragment f = new SearchResultsFragment();
                    // Supply index input as an argument.
                    Bundle args = new Bundle();
                    args.putString("query", s);
                    f.setArguments(args);


                    ((MaterialNavigationDrawer) MainActivity.this).setFragment(f, "Szukaj: '" + s + "'");
                    ((MaterialNavigationDrawer) MainActivity.this).getCurrentSection().unSelect();
                    ((MaterialNavigationDrawer) MainActivity.this).setSection(newSection("", new android.app.Fragment()));
                    section_settings.select();

                    searchView.clearFocus();
                }else
                    Toast.makeText(MyApplication.getAppContext(), MainActivity.this.getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //Toast.makeText(getAppContext(), query, Toast.LENGTH_SHORT).show();
                return true;

            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }
}

package andrzej.example.com.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Random;

import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.HistoryFragment;
import andrzej.example.com.fragments.MainFragment;
import andrzej.example.com.mlpwiki.R;
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_search:
                Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(myIntent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

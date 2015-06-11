package andrzej.example.com.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.HistoryFragment;
import andrzej.example.com.fragments.MainFragment;
import andrzej.example.com.fragments.SavedArticlesFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.prefs.DrawerImages;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;

public class MainActivity extends MaterialNavigationDrawer {

    private static Context context;
    public static final int REQUEST_CODE_TEST = 0;

    //Sekcje, które muszą być globalne.
    MaterialSection section_main;
    MaterialSection section_random;
    MaterialSection section_saved;
    MaterialSection section_history;
    MaterialSection section_settings;
    public static MaterialSection section_article;

    @Override
    public void init(Bundle savedInstanceState) {

        MainActivity.context = getApplicationContext();

        allowArrowAnimation();

        int[] images = DrawerImages.drawer_images;
        Random r = new Random();
        int random_int = r.nextInt(images.length);

        this.setDrawerHeaderImage(images[random_int]);


        section_main = newSection("Dzisiaj", new MainFragment());
        addSection(section_main);

        section_history = newSection("Historia", new HistoryFragment());
        addSection(section_history);

        section_saved = newSection("Zapisany strony", new SavedArticlesFragment());
        addSection(section_saved);

        //Not really random
        Fragment fragment = new ArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("article_id", 1986);
        bundle.putString("article_title", "Spike");
        fragment.setArguments(bundle);

        section_random = newSection("Losowa strona", fragment);
        addSection(section_random);

        section_article = newSection("Artykuły", new ArticleFragment());
        addSection(section_article);
        section_article.getView().setVisibility(View.GONE);

        section_settings = newSection("Ustawienia", new Intent(this, SharedPreferenceActivity.class));
        addBottomSection(section_settings);

    }


    public static Context getAppContext() {
        return MainActivity.context;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_TEST): {
                if (resultCode == Activity.RESULT_OK) {
                    int article_id = data.getIntExtra("article_id", -1);
                    String article_title = data.getStringExtra("article_title");

                    Fragment fragment = new ArticleFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("article_id", article_id);
                    bundle.putString("article_title", article_title);
                    fragment.setArguments(bundle);

                    ((MaterialNavigationDrawer) MainActivity.this).setFragment(fragment, article_title);
                    ((MaterialNavigationDrawer) MainActivity.this).setSection(section_article);

                }
                break;
            }
        }
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

        switch (item.getItemId()) {
            case R.id.menu_search:
                Intent myIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivityForResult(myIntent, REQUEST_CODE_TEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        MaterialSection current_section = this.getCurrentSection();

        if (current_section == section_main) {
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
        } else {
            if (ArticleFragment.mDrawerLayout != null && ArticleFragment.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                ArticleFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                ((MaterialNavigationDrawer) MainActivity.this).setFragment(new MainFragment(), "Strona główna");
                ((MaterialNavigationDrawer) MainActivity.this).setSection(section_main);
            }
        }
    }
}

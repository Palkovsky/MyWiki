package andrzej.example.com.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Random;

import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.HistoryFragment;
import andrzej.example.com.fragments.MainFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.fragments.SavedArticlesFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.DrawerImages;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

public class MainActivity extends MaterialNavigationDrawer {

    private static Context context;
    public static final int REQUEST_CODE_TEST = 0;

    //Sekcje, które muszą być globalne.
    public static MaterialSection section_main;
    public static MaterialSection section_random;
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

        getSupportActionBar().setShowHideAnimationEnabled(true);

        section_main = newSection(getResources().getString(R.string.drawer_today), getResources().getDrawable(R.drawable.ic_white_balance_sunny_grey600_24dp), new MainFragment());
        addSection(section_main);

        section_history = newSection(getResources().getString(R.string.drawer_history), getResources().getDrawable(R.drawable.ic_history_grey600_24dp), new HistoryFragment());
        addSection(section_history);

        section_saved = newSection(getResources().getString(R.string.drawer_saved_articles), getResources().getDrawable(R.drawable.ic_content_save_all_grey600_24dp), new SavedArticlesFragment());
        addSection(section_saved);


        section_random = newSection(getResources().getString(R.string.drawer_random_article), getResources().getDrawable(R.drawable.ic_dice_5_grey600_24dp), new RandomArticleFragment());
        addSection(section_random);
        section_random.setOnClickListener(new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {
                if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext())) {
                    setFragment(new RandomArticleFragment(), getResources().getString(R.string.drawer_random_article));
                    setSection(section_article);
                    materialSection.select();
                } else {
                    Toast.makeText(MyApplication.getAppContext(), getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                    materialSection.unSelect();
                }
            }
        });

        section_article = newSection(getResources().getString(R.string.drawer_articles), new ArticleFragment());
        addSection(section_article);
        section_article.getView().setVisibility(View.GONE);

        section_settings = newSection(getResources().getString(R.string.drawer_settings), getResources().getDrawable(R.drawable.ic_settings_grey600_24dp), new Intent(this, SharedPreferenceActivity.class));
        addBottomSection(section_settings);

        getToolbar().setCollapsible(true);

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
            } else if (RandomArticleFragment.mDrawerLayout != null && RandomArticleFragment.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                RandomArticleFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                ((MaterialNavigationDrawer) MainActivity.this).setFragment(new MainFragment(), "Strona główna");
                ((MaterialNavigationDrawer) MainActivity.this).setSection(section_main);
            }
        }
    }

}

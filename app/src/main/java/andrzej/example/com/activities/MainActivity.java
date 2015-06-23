package andrzej.example.com.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.HistoryFragment;
import andrzej.example.com.fragments.MainFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.fragments.SavedArticlesFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.SessionArticleHistory;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.DrawerImages;
import andrzej.example.com.utils.OnBackPressedListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

public class MainActivity extends MaterialNavigationDrawer {

    private static Context context;
    public static final int REQUEST_CODE_TEST = 0;

    protected OnBackPressedListener onBackPressedListener;

    //Sekcje, które muszą być globalne.
    public static MaterialSection section_main;
    public static MaterialSection section_random;
    public static MaterialSection section_saved;
    public static MaterialSection section_history;
    public static MaterialSection section_settings;
    public static MaterialSection section_article;


    //Fragment
    //public static HistoryFragment historyFragment = new HistoryFragment();
    public static RandomArticleFragment randomArticleFragment = new RandomArticleFragment();
    public static ArticleFragment articleFragment = new ArticleFragment();
    //public static MainFragment mainFragment = new MainFragment();
    //public static SavedArticlesFragment savedArticlesFragment = new SavedArticlesFragment();


    public static List<SessionArticleHistory> sessionArticleHistory = new ArrayList();

    @Override
    public void init(Bundle savedInstanceState) {

        MainActivity.context = getApplicationContext();

        allowArrowAnimation();

        int[] images = DrawerImages.drawer_images;
        Random r = new Random();
        int random_int = r.nextInt(images.length);

        this.setDrawerHeaderImage(images[random_int]);

        getSupportActionBar().setShowHideAnimationEnabled(true);

        section_main = newSection(getResources().getString(R.string.drawer_today), ContextCompat.getDrawable(this, R.drawable.ic_white_balance_sunny_grey600_24dp), new MainFragment());
        addSection(section_main);

        section_history = newSection(getResources().getString(R.string.drawer_history), ContextCompat.getDrawable(this, R.drawable.ic_history_grey600_24dp), new HistoryFragment());
        addSection(section_history);

        section_saved = newSection(getResources().getString(R.string.drawer_saved_articles), ContextCompat.getDrawable(this, R.drawable.ic_content_save_all_grey600_24dp), new SavedArticlesFragment());
        addSection(section_saved);


        section_random = newSection(getResources().getString(R.string.drawer_random_article), ContextCompat.getDrawable(this, R.drawable.ic_dice_5_grey600_24dp), new RandomArticleFragment());
        addSection(section_random);
        section_random.setOnClickListener(new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {
                if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext())) {
                    setFragment(randomArticleFragment, getResources().getString(R.string.drawer_random_article));
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

        section_settings = newSection(getResources().getString(R.string.drawer_settings), ContextCompat.getDrawable(this, R.drawable.ic_settings_grey600_24dp), new Intent(this, SharedPreferenceActivity.class));
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

    public static void addToSessionArticleHistory(int id, String label) {
        SessionArticleHistory item = new SessionArticleHistory(id, label);


        if (sessionArticleHistory.size() > 0) {
            if (sessionArticleHistory.get(sessionArticleHistory.size() - 1).getId() != item.getId()) {


                for (int i = 0; i < sessionArticleHistory.size(); i++) {
                    if (sessionArticleHistory.get(i).getId() == item.getId()) {
                        sessionArticleHistory.remove(i);
                    }
                }

                sessionArticleHistory.add(item);
            }
        } else
            sessionArticleHistory.add(item);
    }

    @Override
    public void onBackPressed() {
        MaterialSection current_section = this.getCurrentSection();

        if (current_section != section_article)
            sessionArticleHistory.clear();

        if (current_section == section_main) {
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
        } else if (current_section == section_article) {
            if (ArticleFragment.mDrawerLayout != null && ArticleFragment.mDrawerLayout.getChildCount()>0 && ArticleFragment.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                ArticleFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else if (RandomArticleFragment.mDrawerLayout != null && RandomArticleFragment.mDrawerLayout.getChildCount()>0 && RandomArticleFragment.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                RandomArticleFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                if (sessionArticleHistory.size() > 1) {

                    onBackPressedListener.doBack();

                    /*
                    Fragment fragment = new ArticleFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("article_id", item.getId());
                    bundle.putString("article_title", item.getTitle());
                    fragment.setArguments(bundle);


                    ((MaterialNavigationDrawer) MainActivity.this).setFragment(fragment, item.getTitle());
                    ((MaterialNavigationDrawer) MainActivity.this).setSection(section_article);
                    */
                } else {

                    ((MaterialNavigationDrawer) MainActivity.this).setFragment(new MainFragment(), getResources().getString(R.string.drawer_today));
                    ((MaterialNavigationDrawer) MainActivity.this).setSection(section_main);
                    sessionArticleHistory.clear();
                }
            }

        } else {
            ((MaterialNavigationDrawer) MainActivity.this).setFragment(new MainFragment(), getResources().getString(R.string.drawer_today));
            ((MaterialNavigationDrawer) MainActivity.this).setSection(section_main);
            sessionArticleHistory.clear();
        }
    }


            /*
            if (ArticleFragment.mDrawerLayout != null && ArticleFragment.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                ArticleFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else if (RandomArticleFragment.mDrawerLayout != null && RandomArticleFragment.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                RandomArticleFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                ((MaterialNavigationDrawer) MainActivity.this).setFragment(new MainFragment(), getResources().getString(R.string.drawer_today));
                ((MaterialNavigationDrawer) MainActivity.this).setSection(section_main);
            }
            */

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }
}


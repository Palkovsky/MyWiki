package andrzej.example.com.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.HistoryFragment;
import andrzej.example.com.fragments.MainFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.fragments.SavedArticlesFragment;
import andrzej.example.com.fragments.WikisManagementFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SessionArticleHistory;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.DrawerImages;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnBackPressedListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.elements.MaterialAccount;
import it.neokree.materialnavigationdrawer.elements.MaterialSection;
import it.neokree.materialnavigationdrawer.elements.listeners.MaterialSectionListener;

public class MainActivity extends MaterialNavigationDrawer {

    private static Context context;
    public static final int REQUEST_CODE_TEST = 0;

    protected OnBackPressedListener onBackPressedListener;

    //Sekcje, które muszą być globalne.
    public static MaterialSection section_main;
    public static MaterialSection section_random;
    public static MaterialSection section_management;
    public static MaterialSection section_history;
    public static MaterialSection section_bookmarks;
    public static MaterialSection section_settings;
    public static MaterialSection section_article;
    public static MaterialSection section_offlineArticle;

    //Accounts
    public static MaterialAccount account;

    //Fragment
    //public static HistoryFragment historyFragment = new HistoryFragment();
    //public static RandomArticleFragment randomArticleFragment = new RandomArticleFragment();
    //public static ArticleFragment articleFragment = new ArticleFragment();
    //public static MainFragment mainFragment = new MainFragment();
    //public static WikisManagementFragment wikisManagementFragment = new WikisManagementFragment();

    SharedPreferences prefs;

    public static List<SessionArticleHistory> sessionArticleHistory = new ArrayList();

    @Override
    public void init(Bundle savedInstanceState) {

        MainActivity.context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        allowArrowAnimation();

        int[] images = DrawerImages.drawer_images;
        Random r = new Random();
        int random_int = r.nextInt(images.length);

        String account_subtitle = prefs.getString(SharedPrefsKeys.CURRENT_WIKI_URL, BaseConfig.DEFAULT_WIKI);
        String account_title = prefs.getString(SharedPrefsKeys.CURRENT_WIKI_LABEL, BaseConfig.DEFAULT_TITLE);

        APIEndpoints.WIKI_NAME = account_subtitle;
        APIEndpoints.reInitEndpoints();

        account = new MaterialAccount(this.getResources(), account_subtitle, account_title, null, images[random_int]);
        this.addAccount(account);

        section_main = newSection(getResources().getString(R.string.drawer_today), ContextCompat.getDrawable(this, R.drawable.ic_white_balance_sunny_grey600_24dp), new MainFragment());
        addSection(section_main);

        section_history = newSection(getResources().getString(R.string.drawer_history), ContextCompat.getDrawable(this, R.drawable.ic_history_grey600_24dp), new HistoryFragment());
        addSection(section_history);

        section_management = newSection(getResources().getString(R.string.drawer_saved_articles), ContextCompat.getDrawable(this, R.drawable.ic_elevator_grey600_24dp), new WikisManagementFragment());
        addSection(section_management);
        section_management.setOnClickListener(new MaterialSectionListener() {
            @Override
            public void onClick(MaterialSection materialSection) {
                setFragment(new WikisManagementFragment(), getResources().getString(R.string.drawer_saved_articles));
                setSection(section_management);
                materialSection.select();
            }
        });

        section_bookmarks = newSection(getResources().getString(R.string.drawer_bookmared_articles), ContextCompat.getDrawable(this, R.drawable.ic_bookmark_grey600_24dp), new SavedArticlesFragment());
        addSection(section_bookmarks);

        section_random = newSection(getResources().getString(R.string.drawer_random_article), ContextCompat.getDrawable(this, R.drawable.ic_dice_5_grey600_24dp), new RandomArticleFragment());
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

        section_offlineArticle = newSection(getResources().getString(R.string.drawer_articles), new ArticleFragment());
        addSection(section_offlineArticle);
        section_offlineArticle.getView().setVisibility(View.GONE);

        section_settings = newSection(getResources().getString(R.string.drawer_settings), ContextCompat.getDrawable(this, R.drawable.ic_settings_grey600_24dp), new Intent(this, SharedPreferenceActivity.class));
        addBottomSection(section_settings);

        getToolbar().setCollapsible(true);



    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String redirection = extras.getString(InitialActivity.KEY_REDIRECT);

            if (redirection.equals(WikisManagementFragment.TAG)) {
                setFragment(new WikisManagementFragment(), getResources().getString(R.string.drawer_saved_articles));
                setSection(section_management);
                section_management.select();
            }
        }
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

        if (isDrawerOpen())
            closeDrawer();
        else {

            if (current_section != section_article)
                sessionArticleHistory.clear();

            if (current_section == section_main) {
                Intent setIntent = new Intent(Intent.ACTION_MAIN);
                setIntent.addCategory(Intent.CATEGORY_HOME);
                setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(setIntent);
            } else if (current_section == section_article) {
                if (ArticleFragment.mDrawerLayout != null && ArticleFragment.mDrawerLayout.getChildCount() > 0 && ArticleFragment.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    ArticleFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else if (RandomArticleFragment.mDrawerLayout != null && RandomArticleFragment.mDrawerLayout.getChildCount() > 0 && RandomArticleFragment.mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    RandomArticleFragment.mDrawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    if (sessionArticleHistory.size() > 1) {
                        onBackPressedListener.doBack();
                    } else {

                        ((MaterialNavigationDrawer) MainActivity.this).setFragment(new MainFragment(), getResources().getString(R.string.drawer_today));
                        ((MaterialNavigationDrawer) MainActivity.this).setSection(section_main);
                        sessionArticleHistory.clear();
                    }
                }

            } else if (current_section == section_offlineArticle) {
                onBackPressedListener.doBack();
            } else {
                ((MaterialNavigationDrawer) MainActivity.this).setFragment(new MainFragment(), getResources().getString(R.string.drawer_today));
                ((MaterialNavigationDrawer) MainActivity.this).setSection(section_main);
                sessionArticleHistory.clear();
            }
        }
    }


    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }
}


package andrzej.example.com.activities;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import andrzej.example.com.adapters.SimpleRecyclerAdapter;
import andrzej.example.com.databases.WikisFavsDbHandler;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.fragments.ManagementTabs.FavouriteWikisFragment;
import andrzej.example.com.fragments.ManagementTabs.PreviouslyUsedWikisFragment;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SuggestedItem;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.PaletteTransformation;
import andrzej.example.com.utils.WikiManagementHelper;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;


public class WikiInfoActivity extends AppCompatActivity implements View.OnClickListener {

    //Finals
    public static final String WIKI_ID_INTENT_KEY = "WIKI_ID_INTENT_KEY";
    public static final String WIKI_TITLE_INTENT_KEY = "WIKI_TITLE_INTENT_KEY";
    public static final String WIKI_DESCRIPTION_INTENT_KEY = "WIKI_DESCRIPTION_INTENT_KEY";
    public static final String WIKI_IMG_URL_INTENT_KEY = "WIKI_IMG_URL_INTENT_KEY";
    public static final String WIKI_URL_INTENT_KEY = "WIKI_URL_INTENT_KEY";
    public static final String WIKI_PAGES_COUNT_KEY = "WIKI_PAGES_COUNT_KEY";

    //Few vital variables
    int wikiId = -1; //Default -1, in case of some issuses. To prevent from NullPointerException.

    //UI Elements
    private Toolbar mToolbar;
    private FloatingActionButton mButton;
    private CoordinatorLayout mRootView;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView headerIv;
    RecyclerView recyclerView;

    //Vitals
    int mutedColor = R.attr.colorPrimary;

    //Callbacs

    //Adapters
    SimpleRecyclerAdapter simpleRecyclerAdapter;

    //Utils
    private SharedPreferences prefs;
    private WikiManagementHelper mHelper;


    SuggestedItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_info);

        //Utils Init
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mHelper = new WikiManagementHelper(this);

        //UI Init
        mToolbar = (Toolbar) findViewById(R.id.anim_toolbar);
        mRootView = (CoordinatorLayout) findViewById(R.id.wikiInfo_root);
        mButton = (FloatingActionButton) findViewById(R.id.floatingBtn);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.scrollableview);
        headerIv = (ImageView) findViewById(R.id.header);
        headerIv.setBackgroundColor(Color.BLACK);

        //Listener
        mButton.setOnClickListener(this);


        //Toolbar Config
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Get bundle with wiki id
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            wikiId = extras.getInt(WIKI_ID_INTENT_KEY);
            String wikiTitle = extras.getString(WIKI_TITLE_INTENT_KEY);
            String wikiImgUrl = extras.getString(WIKI_IMG_URL_INTENT_KEY);
            String description = extras.getString(WIKI_DESCRIPTION_INTENT_KEY);
            String wikiUrl = extras.getString(WIKI_URL_INTENT_KEY);
            int pageCount = extras.getInt(WIKI_PAGES_COUNT_KEY);

            item = new SuggestedItem(wikiId, wikiUrl, wikiTitle, description, wikiImgUrl, pageCount);

            collapsingToolbar.setTitle(wikiTitle);
            collapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
            collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.collapsedToolbarTextColor));

            Drawable d = ContextCompat.getDrawable(this, R.drawable.logo);



            Picasso.with(this).load(wikiImgUrl).placeholder(d).error(d).into(headerIv, new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) headerIv.getDrawable()).getBitmap();

                    Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            mutedColor = palette.getMutedColor(R.attr.colorPrimary);
                            headerIv.setBackgroundColor(palette.getDarkVibrantColor(R.attr.color));
                            collapsingToolbar.setContentScrimColor(mutedColor);
                        }
                    });
                }

                @Override
                public void onError() {}
            });


        }


        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        String[] data = {item.getTitle(), item.getDescription(), String.valueOf(item.getPages()), item.getUrl()};
        List<String> listData = new ArrayList<String>();

        for (String item : data) {
            listData.add(item);
        }

        if (simpleRecyclerAdapter == null) {
            simpleRecyclerAdapter = new SimpleRecyclerAdapter(this, listData);
            recyclerView.setAdapter(simpleRecyclerAdapter);
        }

        //Few init methords
        setUpColorScheme();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wiki_info, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setUpNightMode() {
        mRootView.setBackgroundColor(getResources().getColor(R.color.nightBackground));
    }

    private void setUpNormalMode() {
        mRootView.setBackgroundColor(getResources().getColor(R.color.background));
    }

    private void setUpColorScheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //If specific wiki is currently setted don't show adding fab.
        if (APIEndpoints.WIKI_NAME.equals(item.getUrl()))
            mRootView.removeView(mButton);

        setUpColorScheme();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingBtn:
                final String url = item.getUrl();
                if (!APIEndpoints.WIKI_NAME.equals(url)) {
                    WikisHistoryDbHandler db = new WikisHistoryDbHandler(this);
                    if (db.itemExsists(url)) {
                        WikiPreviousListItem item1 = db.getItemByUrl(url);
                        mHelper.removeWiki(item1.getId());
                    }
                    db.close();

                    WikisFavsDbHandler favs_db = new WikisFavsDbHandler(this);
                    if (favs_db.itemExsists(url)) {
                        int id = favs_db.getItemByUrl(url).getId();
                        favs_db.editItem(id, new WikiFavItem(id, item.getTitle(), url, item.getDescription(), item.getImageUrl()));
                    }
                    favs_db.close();

                    mHelper.addWikiToPreviouslyUsed(new WikiPreviousListItem(item.getTitle(), url, item.getDescription(), item.getImageUrl()));

                    APIEndpoints.WIKI_NAME = mHelper.cleanInputUrl(url);
                    mHelper.setUrlAsPreference(APIEndpoints.WIKI_NAME, item.getTitle());
                    APIEndpoints.reInitEndpoints();

                    MainActivity.account.setTitle(APIEndpoints.WIKI_NAME);
                    MainActivity.account.setSubTitle(item.getTitle());


                    FavouriteWikisFragment.updateDataset();
                    PreviouslyUsedWikisFragment.updateRecords();

                    Toast.makeText(this, getResources().getString(R.string.wiki_succesfully_changed), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this, getResources().getString(R.string.already_setted), Toast.LENGTH_SHORT).show();

                mRootView.removeView(mButton);

                break;
        }
    }


}


package andrzej.example.com.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.views.AlphaForegroundColorSpan;
import andrzej.example.com.views.KenBurnsView;

import android.graphics.RectF;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;



public class WikiInfoActivity extends AppCompatActivity {

    //Finals
    public static final String WIKI_ID_INTENT_KEY = "WIKI_ID_INTENT_KEY";

    //Few vital variables
    int wikiId = -1; //Default -1, in case of some issuses. To prevent from NullPointerException.

    //UI Elements
    private RelativeLayout rootView;
    private ListView mListView;
    private ImageView mHeaderImageView;
    private Toolbar mToolbar;
    private TextView testTextView;

    //Utils
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_info);

        //Utils Init
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //UI Init
        rootView = (RelativeLayout) findViewById(R.id.wikiInfoRootView);
        mListView = (ListView) findViewById(R.id.wikiInfoListview);
        mToolbar = (Toolbar) findViewById(R.id.wikiInfoToolbar);
        mHeaderImageView = (ImageView) findViewById(R.id.wikiInfoHeader_logo);
        testTextView = (TextView) findViewById(R.id.wikiInfoTestMsg);

        //Toolbar Config
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);
        getSupportActionBar().setTitle(this.getResources().getString(R.string.wiki_info_activity));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get bundle with wiki id
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            wikiId = extras.getInt(WIKI_ID_INTENT_KEY);
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

    private void setUpNormalMode() {
        rootView.setBackgroundColor(this.getResources().getColor(R.color.background));
        testTextView.setTextColor(this.getResources().getColor(R.color.font_color));
    }

    private void setUpNightMode() {
        rootView.setBackgroundColor(this.getResources().getColor(R.color.nightBackground));
        testTextView.setTextColor(this.getResources().getColor(R.color.nightFontColor));
    }

    private void setUpColorScheme() {
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpColorScheme();

        Toast.makeText(this, "Wiki ID: " + wikiId, Toast.LENGTH_SHORT).show();
    }
}


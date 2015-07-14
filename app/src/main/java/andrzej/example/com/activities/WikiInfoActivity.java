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
import android.widget.LinearLayout;
import android.widget.ListView;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.ArticleHeader;
import andrzej.example.com.models.Recommendation;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.researchapi.APIStatisticalEndpoints;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    private TextView wikiInfoErrorMessage;
    private LinearLayout mContentView;
    private LinearLayout mNoIntenretLayout;
    private LinearLayout mLoadingLayout;

    //Utils
    private SharedPreferences prefs;

    //Networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wiki_info);

        //Utils Init
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        //UI Init
        rootView = (RelativeLayout) findViewById(R.id.wikiInfoRootView);
        mListView = (ListView) findViewById(R.id.wikiInfoListview);
        mToolbar = (Toolbar) findViewById(R.id.wikiInfoToolbar);
        mHeaderImageView = (ImageView) findViewById(R.id.wikiInfoHeader_logo);
        testTextView = (TextView) findViewById(R.id.wikiInfoTestMsg);
        wikiInfoErrorMessage = (TextView) findViewById(R.id.wikiInfoErrorMessage);
        mContentView = (LinearLayout) findViewById(R.id.wikiInfoContentView);
        mNoIntenretLayout = (LinearLayout) findViewById(R.id.wikiInfoNoInternetLayout);
        mLoadingLayout = (LinearLayout) findViewById(R.id.wikiInfoLoadingLayout);

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

        setNoInternetLayout();

        //Few init methords
        setUpColorScheme();
    }


    private void parseWikiInfo(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIStatisticalEndpoints.getWikiData(wikiId), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Token token=" + APIStatisticalEndpoints.getAPIkey());
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
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

    private void setNoInternetLayout(){
        setErrorMessage();
        mContentView.setVisibility(View.GONE);
        mNoIntenretLayout.setVisibility(View.VISIBLE);
        mLoadingLayout.setVisibility(View.GONE);
    }

    private void setInternetPresentLayout(){
        mContentView.setVisibility(View.VISIBLE);
        mNoIntenretLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
    }

    private void setLoadingLayout(){
        mContentView.setVisibility(View.GONE);
        mNoIntenretLayout.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    private void setErrorMessage(){
        if(NetworkUtils.isNetworkAvailable(this))
            wikiInfoErrorMessage.setText(getResources().getString(R.string.wiki_info_fetching_error));
        else
            wikiInfoErrorMessage.setText(getResources().getString(R.string.no_internet_conn));
    }

    private void setUpNormalMode() {
        rootView.setBackgroundColor(this.getResources().getColor(R.color.background));
        testTextView.setTextColor(this.getResources().getColor(R.color.font_color));
        wikiInfoErrorMessage.setTextColor(this.getResources().getColor(R.color.font_color));
    }

    private void setUpNightMode() {
        rootView.setBackgroundColor(this.getResources().getColor(R.color.nightBackground));
        testTextView.setTextColor(this.getResources().getColor(R.color.nightFontColor));
        wikiInfoErrorMessage.setTextColor(this.getResources().getColor(R.color.nightFontColor));
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


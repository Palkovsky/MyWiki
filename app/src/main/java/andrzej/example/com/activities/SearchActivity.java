package andrzej.example.com.activities;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import andrzej.example.com.adapters.ResultListAdapter;
import andrzej.example.com.databases.SearchHistoryDbHandler;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.models.Article;


public class SearchActivity extends AppCompatActivity {

    // UI Elements
    private ListView results_listview;
    private LinearLayout noInternetLl;
    private BootstrapButton noInternetButton;
    private ProgressBar fetchingProgressBar;
    private Toolbar toolbar;
    private SearchView searchView;
    private TextView noRecordsTv;

    //Adapter
    BaseAdapter mListAdapter;

    //db
    SearchHistoryDbHandler db;

    String query_url, query;

    //Lists
    private ArrayList<Article> results;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.searching));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        results = new ArrayList<Article>();
        mListAdapter = new ResultListAdapter(this, results);

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        fetchingProgressBar = (ProgressBar) findViewById(R.id.loadingPb);
        results_listview = (ListView) findViewById(R.id.result_listview);
        noInternetLl = (LinearLayout) findViewById(R.id.noInternetLl);
        noInternetButton = (BootstrapButton) findViewById(R.id.noInternetBtn);
        noRecordsTv = (TextView) findViewById(R.id.noRecordsTv);


        results_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Article article = results.get(position);
                db.addItem(new Article(article.getId(), article.getTitle()));


                Intent resultIntent = new Intent();
                resultIntent.putExtra("article_id", article.getId());
                resultIntent.putExtra("article_title", article.getTitle());
                setResult(Activity.RESULT_OK, resultIntent);
                finish();

            }
        });

        results_listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext()
                            .getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            searchView.getWindowToken(), 0);

                }

                return false;
            }
        });

        results_listview.setAdapter(mListAdapter);

        db = new SearchHistoryDbHandler(MyApplication.getAppContext());
        results.addAll(db.getAllItems());
        mListAdapter.notifyDataSetChanged();

        noInternetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext())) {

                    toolbar.setTitle("Szukaj: '" + query + "'");
                    results.clear();
                    mListAdapter.notifyDataSetChanged();

                    results_listview.setVisibility(View.VISIBLE);
                    fetchingProgressBar.setVisibility(View.VISIBLE);
                    noRecordsTv.setVisibility(View.GONE);
                    noInternetLl.setVisibility(View.GONE);

                    if (searchView != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }

                    sendJsonSearchRequest(true);
                } else
                    Toast.makeText(MyApplication.getAppContext(), getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
            }
        });

        if (!NetworkUtils.isNetworkAvailable(this))
            fetchingProgressBar.setVisibility(View.GONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.search_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                query = query;
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext())) {

                    query_url = APIEndpoints.getUrlSearch(query, BaseConfig.searchLimit);
                    query_url = query_url.replaceAll(" ", "%20");
                    try {
                        URL url = new URL(query_url);
                        URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                        query_url = uri.toASCIIString();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }

                    toolbar.setTitle("Szukaj: '" + query + "'");
                    results.clear();
                    mListAdapter.notifyDataSetChanged();
                    results_listview.setVisibility(View.VISIBLE);
                    fetchingProgressBar.setVisibility(View.VISIBLE);
                    noRecordsTv.setVisibility(View.GONE);
                    noInternetLl.setVisibility(View.GONE);


                    sendJsonSearchRequest(true);

                } else {
                    fetchingProgressBar.setVisibility(View.GONE);
                    results_listview.setVisibility(View.GONE);

                    Toast.makeText(MyApplication.getAppContext(), getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                query_url = APIEndpoints.getUrlSearch(newText, BaseConfig.searchLimit);
                query_url = query_url.replaceAll(" ", "%20");
                try {
                    URL url = new URL(query_url);
                    URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                    query_url = uri.toASCIIString();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

                query = newText;

                if (newText.trim().length() > 0) {

                    results_listview.setVisibility(View.VISIBLE);
                    noRecordsTv.setVisibility(View.GONE);

                    sendJsonSearchRequest(false);
                } else {
                    results.clear();
                    db = new SearchHistoryDbHandler(MyApplication.getAppContext());
                    results.addAll(db.getAllItems());
                    mListAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        searchView.setIconified(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void parseJsonResponse(JSONObject response, boolean submit) {

        if (response == null || response.length() == 0) {
            return;
        }

        try {
            JSONArray items = response.getJSONArray(Article.KEY_ITEMS);

            int count = 0;
            for (int i = 0; i < items.length(); i++) {
                if (stringContainsItemFromList(items.getJSONObject(i).getString(Article.KEY_TITLE), APIEndpoints.STOP_WORDS))
                    count++;
            }

            if (!submit && results.size() > 0 && count < items.length())
                results.clear();
            if (submit)
                results.clear();
            mListAdapter.notifyDataSetChanged();

            for (int i = 0; i < items.length(); i++) {

                JSONObject searchItem = items.getJSONObject(i);

                int id = searchItem.getInt(Article.KEY_ID);
                String title = searchItem.getString(Article.KEY_TITLE);

                if (!stringContainsItemFromList(title, APIEndpoints.STOP_WORDS)) {

                    results.add(new Article(id, title));
                    mListAdapter.notifyDataSetChanged();
                }

            }

            if (submit) {
                fetchingProgressBar.setVisibility(View.GONE);
                if (results.size() <= 0) {
                    noRecordsTv.setVisibility(View.VISIBLE);
                    results_listview.setVisibility(View.GONE);
                }
            } else {
                fetchingProgressBar.setVisibility(View.GONE);
            }

            if(results.size()>0)
                noInternetLl.setVisibility(View.GONE);


        } catch (JSONException e) {
            Toast.makeText(MyApplication.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void sendJsonSearchRequest(final boolean submit) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, query_url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseJsonResponse(response, submit);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {

                    if(results.size()>0)
                        noInternetLl.setVisibility(View.GONE);

                    if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext())) {
                        if(error!=null && error.networkResponse !=null && error.networkResponse.data != null) {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            JSONObject jsonObject = new JSONObject(responseBody);

                            if (submit) {
                                results.clear();
                                mListAdapter.notifyDataSetChanged();
                                fetchingProgressBar.setVisibility(View.GONE);
                                noRecordsTv.setVisibility(View.VISIBLE);
                                results_listview.setVisibility(View.GONE);
                            } else {
                                fetchingProgressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                } catch (JSONException e) {
                    //Handle a malformed json response
                } catch (UnsupportedEncodingException volley_error) {

                }
            }
        });

        requestQueue.add(request);
    }

    public static boolean stringContainsItemFromList(String inputString, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (inputString.contains(items[i])) {
                return true;
            }
        }
        return false;
    }

}

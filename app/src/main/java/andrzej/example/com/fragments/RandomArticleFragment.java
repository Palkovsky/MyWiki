package andrzej.example.com.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.adapters.ArticleStructureListAdapter;
import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.fab.FloatingActionButton;
import andrzej.example.com.fab.ObservableScrollView;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.ArticleHeader;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.models.ArticleImage;
import andrzej.example.com.models.ArticleSection;
import andrzej.example.com.models.SearchResult;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.utils.ArrayHelpers;
import andrzej.example.com.utils.ArticleViewsManager;
import andrzej.example.com.utils.StringOperations;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class RandomArticleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //UI
    ImageView parallaxIv;
    TextView titleTv;
    ObservableScrollView parallaxSv;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout noInternetLl;
    LinearLayout rootArticleLl;
    LinearLayout loadingLl;
    BootstrapButton retryBtn;
    ArticleViewsManager viewsManager;
    andrzej.example.com.fab.FloatingActionButton fab;
    public static DrawerLayout mDrawerLayout;
    ListView mDrawerListView;
    ActionBarDrawerToggle drawerToggle;

    private int article_id;
    String article_title;

    // Lists
    private List<ArticleImage> imgs = new ArrayList<ArticleImage>();
    private List<ArticleSection> sections = new ArrayList<>();
    private List<ArticleHeader> headers = new ArrayList<>();
    ArticleStructureListAdapter mStructureAdapter;

    //Networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    ArticleHistoryDbHandler db;

    //Flasg
    public static boolean scrollingWithDrawer = false;

    public RandomArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
        Bundle bundle = this.getArguments();
        article_id = bundle.getInt("article_id", -1);
        article_title = bundle.getString("article_title");
*/

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        db = new ArticleHistoryDbHandler(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_article, container, false);

        setHasOptionsMenu(true);

        parallaxSv = (ObservableScrollView) v.findViewById(R.id.parallaxSv);
        parallaxIv = (ImageView) v.findViewById(R.id.parallaxIv);
        titleTv = (TextView) v.findViewById(R.id.titleTv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.article_swipe_refresh_layout);
        noInternetLl = (LinearLayout) v.findViewById(R.id.noInternetLl);
        rootArticleLl = (LinearLayout) v.findViewById(R.id.rootArticle);
        loadingLl = (LinearLayout) v.findViewById(R.id.loadingLl);
        retryBtn = (BootstrapButton) v.findViewById(R.id.noInternetBtn);
        fab = (andrzej.example.com.fab.FloatingActionButton) v.findViewById(R.id.fab);
        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) v.findViewById(R.id.right_drawer);

        fab.hide(false);
        fab.attachToScrollView(parallaxSv);

        imgs.clear();
        sections.clear();


        //((MaterialNavigationDrawer) this.getActivity()).getToolbar().setTitle(getResources().getString(R.string.drawer_random_article));

        viewsManager = new ArticleViewsManager(MyApplication.getAppContext());
        viewsManager.setLayout(rootArticleLl);


        setLoadingLayout();

        mStructureAdapter = new ArticleStructureListAdapter(getActivity(), R.layout.article_structure_list_item, headers);
        mDrawerListView.setAdapter(mStructureAdapter);
        refreshHeaders();


        //mDrawerListView.addHeaderView(drawerHeader);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        if (headers.get(position).getView() != null) {
                            parallaxSv.smoothScrollTo(0, headers.get(position).getView().getBottom());
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                            fab.hide();
                        }
                    }
                });
            }
        });

        parallaxSv.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int posY = parallaxSv.getScrollY();
                if (posY <= 800)
                    fab.hide(true);

            }
        });


        ((MaterialNavigationDrawer) this.getActivity()).setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        parallaxSv.smoothScrollTo(0, 0);
                    }
                });
            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    mSwipeRefreshLayout.setEnabled(true);
                    refreshHeaders();
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(true);
                            fetchRandomArticle();
                        }
                    });
                } else
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);

        setImageViewBackground(parallaxIv, getResources().getDrawable(R.drawable.logo));

        if (NetworkUtils.isNetworkAvailable(getActivity()))
            fetchRandomArticle();
        else
            setNoInternetLayout();

        return v;
    }


    private void fetchArticleContent(int id) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemContent(id), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                setInternetPresentLayout();
                            else
                                setNoInternetLayout();
                            JSONArray sections = response.getJSONArray(Article.KEY_SECTIONS);

                            for (int i = 0; i < sections.length(); i++) {
                                JSONObject section = sections.getJSONObject(i);

                                //Parsowanie zawartości
                                JSONArray content = section.getJSONArray(Article.KEY_CONTENT);

                                int level = section.getInt(Article.KEY_LEVEL);
                                String title = section.getString(Article.KEY_TITLE);
                                headers.add(new ArticleHeader(level, title, viewsManager.addHeader(level, title)));


                                if (content.length() > 0) {
                                    for (int j = 0; j < content.length(); j++) {
                                        JSONObject content_section = content.getJSONObject(j);
                                        String type = content_section.getString(Article.KEY_TYPE);

                                        if (type.equals(Article.KEY_PARAGRAPH)) {
                                            String text = content_section.getString(Article.KEY_TEXT);
                                            if (text != null && text.trim().length() > 0)
                                                viewsManager.addTextViewToLayout(text, level);
                                        } else if (type.equals(Article.KEY_LIST)) {
                                            JSONArray elements = content_section.getJSONArray(Article.KEY_ELEMENTS);
                                            if (elements != null && elements.length() > 0)
                                                fetchList(elements, 0, level);
                                        }
                                    }
                                }

                                JSONArray images_section = section.getJSONArray(ArticleImage.KEY_IMAGES);

                                for (int j = 0; j < images_section.length(); j++) {
                                    JSONObject image = images_section.getJSONObject(j);

                                    String img_url = image.getString(ArticleImage.KEY_SRC);
                                    String caption = null;
                                    if (image.has(ArticleImage.KEY_CAPTION))
                                        caption = image.getString(ArticleImage.KEY_CAPTION);

                                    if (img_url != null && img_url.trim().length() > 0) {
                                        imgs.add(new ArticleImage(img_url, caption));
                                        viewsManager.addImageViewToLayout(StringOperations.pumpUpSize(img_url, 600), caption);
                                    }
                                }

                            }

                            if (imgs.size() > 0) {
                                Picasso.with(MyApplication.getAppContext()).load(imgs.get(0).getImg_url()).into(parallaxIv, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        parallaxIv.setBackgroundColor(Color.WHITE);

                                        ArticleHistoryItem item = new ArticleHistoryItem(article_id, System.currentTimeMillis(), article_title, imgs.get(0).getImg_url());
                                        db.addItem(item);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            } else {
                                ArticleHistoryItem item = new ArticleHistoryItem(article_id, System.currentTimeMillis(), article_title, null);
                                db.addItem(item);
                            }

                            mSwipeRefreshLayout.setRefreshing(false);

                            headers = ArrayHelpers.headersRemoveLevels(headers);
                            mStructureAdapter.notifyDataSetChanged();

                            setInternetPresentLayout();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (!NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                    setNoInternetLayout();
            }
        });

        requestQueue.add(request);
    }

    private void fetchList(JSONArray elements, int level, int layout_level) {

        int tree_level;

        if (level <= 0)
            tree_level = 1;
        else
            tree_level = level;

        try {
            for (int i = 0; i < elements.length(); i++) {

                JSONObject element = elements.getJSONObject(i);
                String text = element.getString(Article.KEY_TEXT);
                viewsManager.addListItemToLayout(text, tree_level, layout_level);
                JSONArray nested_elements = element.getJSONArray(Article.KEY_ELEMENTS);

                if (nested_elements != null && nested_elements.length() > 0) {
                    fetchList(nested_elements, tree_level + 1, layout_level);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }

    private void fetchArticleInfo(final int id) {
        int[] array = {id};
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemDetalis(array), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                setInternetPresentLayout();
                            else
                                setNoInternetLayout();
                            JSONObject item = response.getJSONObject(SearchResult.KEY_ITEMS).getJSONObject(String.valueOf(id));
                            String thumbnail_url = item.getString(Article.KEY_THUMBNAIL);

                            if (thumbnail_url != null && thumbnail_url.trim().length() > 0 && !thumbnail_url.isEmpty()) {
                                JSONObject orginal_dimens = item.getJSONObject(ArticleImage.KEY_ORGINAL_DIMENS);
                                int orginal_width = orginal_dimens.getInt(ArticleImage.KEY_WIDTH);

                                thumbnail_url = StringOperations.pumpUpResolution(orginal_width, thumbnail_url);

                                imgs.add(new ArticleImage(thumbnail_url));
                            }

                            fetchArticleContent(id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                fetchArticleContent(id);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (!NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                    setNoInternetLayout();
                else
                    fetchArticleContent(id);
            }
        });

        requestQueue.add(request);
    }

    private void fetchRandomArticle() {
        final int listSize = 10;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlRandom(listSize), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                setInternetPresentLayout();
                            else
                                setNoInternetLayout();

                            JSONArray items = response.getJSONArray(Article.KEY_ITEMS);
                            Random r = new Random();
                            int random_int = r.nextInt(listSize);

                            boolean found = false;
                            JSONObject item = null;

                            while (!found) {
                                item = items.getJSONObject(random_int);

                                if (!StringOperations.stringContainsItemFromList(item.getString(Article.KEY_TITLE), APIEndpoints.STOP_WORDS)) {
                                    found = true;
                                } else {
                                    item = null;
                                    random_int = r.nextInt(listSize);
                                }
                            }

                            article_id = item.getInt(Article.KEY_ID);
                            article_title = item.getString(Article.KEY_TITLE);
                            titleTv.setText(article_title);


                            fetchArticleInfo(article_id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            setNoInternetLayout();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                setNoInternetLayout();
            }
        });

        requestQueue.add(request);
    }


    private void setNoInternetLayout() {
        parallaxSv.setVisibility(View.GONE);
        noInternetLl.setVisibility(View.VISIBLE);
        loadingLl.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(false);
    }

    private void setInternetPresentLayout() {
        parallaxSv.setVisibility(View.VISIBLE);
        noInternetLl.setVisibility(View.GONE);
        loadingLl.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(true);
    }

    private void setLoadingLayout() {
        parallaxSv.setVisibility(View.GONE);
        noInternetLl.setVisibility(View.GONE);
        loadingLl.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setEnabled(false);
    }

    private void refreshHeaders() {
        headers.clear();
        mStructureAdapter.notifyDataSetChanged();
    }

    private void setImageViewBackground(ImageView imageView, Drawable drawable) {

        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(drawable);
        } else {
            imageView.setBackgroundDrawable(drawable);
        }
    }

    @Override
    public void onRefresh() {
        rootArticleLl.removeAllViews();
        refreshHeaders();
        imgs.clear();
        setImageViewBackground(parallaxIv, getResources().getDrawable(R.drawable.logo));
        fetchRandomArticle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.article_menu, menu);
        // Associate searchable configuration with the SearchView
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_structure:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                else
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

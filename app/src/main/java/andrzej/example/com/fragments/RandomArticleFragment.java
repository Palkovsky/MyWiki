package andrzej.example.com.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

import andrzej.example.com.activities.GalleryActivity;
import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.adapters.ArticleStructureListAdapter;
import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.databases.OnDatabaseSaved;
import andrzej.example.com.databases.SavedArticlesDbHandler;
import andrzej.example.com.libraries.refreshlayout.BGANormalRefreshViewHolder;
import andrzej.example.com.libraries.refreshlayout.BGARefreshLayout;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.ArticleHeader;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.models.ArticleImage;
import andrzej.example.com.models.ArticleSection;
import andrzej.example.com.models.BookmarkedArticle;
import andrzej.example.com.models.Recommendation;
import andrzej.example.com.models.SearchResult;
import andrzej.example.com.models.SessionArticleHistory;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.observablescrollview.ObservableScrollView;
import andrzej.example.com.observablescrollview.ObservableScrollViewCallbacks;
import andrzej.example.com.observablescrollview.ScrollState;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.ArrayHelpers;
import andrzej.example.com.utils.ArticleViewsManager;
import andrzej.example.com.utils.OnBackPressedListener;
import andrzej.example.com.utils.StringOperations;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class RandomArticleFragment extends Fragment implements ObservableScrollViewCallbacks, BGARefreshLayout.BGARefreshLayoutDelegate {

    public static final String TAG = "randomArticleFragment";

    //UI
    ImageView parallaxIv;
    TextView titleTv;
    ObservableScrollView parallaxSv;
    BGARefreshLayout mSwipeRefreshLayout;
    LinearLayout noInternetLl;
    LinearLayout rootArticleLl;
    LinearLayout loadingLl;
    LinearLayout parallaxPart;
    BootstrapButton retryBtn;
    ArticleViewsManager viewsManager;
    public static DrawerLayout mDrawerLayout;
    TextView errorMessage;
    ListView mDrawerListView;
    ProgressBar contentProgressBar;

    private int article_id;
    String article_title;

    // Lists
    public static List<ArticleImage> imgs = new ArrayList<ArticleImage>();
    private List<ArticleSection> sections = new ArrayList<>();
    private List<ArticleHeader> headers = new ArrayList<>();
    private List<Recommendation> recommendations = new ArrayList<>();
    public static List<TextView> textViews = new ArrayList<>();
    public static List<ActionMode> mActionModes = new ArrayList<>();
    ArticleStructureListAdapter mStructureAdapter;

    //Networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    ArticleHistoryDbHandler db;

    //Flasg
    public static boolean scrollingWithDrawer = false;
    private boolean isSignificantDelta = false;
    private boolean loading = false;


    //stuff
    private int mLastScrollY;
    private int mScrollThreshold = 15;
    Display display;
    Point size = new Point();
    SharedPreferences prefs;


    private String article_image = null;
    private String article_content = null;

    private boolean initialSwipe = true;

    public RandomArticleFragment() {
        // Required empty public constructor
    }

    private void reInitVars() {
        article_id = -1;
        article_title = null;
        imgs.clear();
        headers.clear();
        recommendations.clear();
        textViews.clear();
    }

    private void nullifyAllVars() {
        if (mDrawerLayout.getChildCount() > 0)
            mDrawerLayout.removeAllViews();
        textViews.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        reInitVars();

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        db = new ArticleHistoryDbHandler(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_article, container, false);

        ((MaterialNavigationDrawer) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.drawer_random_article));

        setHasOptionsMenu(true);

        parallaxSv = (ObservableScrollView) v.findViewById(R.id.parallaxSv);
        parallaxIv = (ImageView) v.findViewById(R.id.parallaxIv);
        titleTv = (TextView) v.findViewById(R.id.titleTv);
        mSwipeRefreshLayout = (BGARefreshLayout) v.findViewById(R.id.article_swipe_refresh_layout);
        noInternetLl = (LinearLayout) v.findViewById(R.id.noInternetLl);
        rootArticleLl = (LinearLayout) v.findViewById(R.id.rootArticle);
        loadingLl = (LinearLayout) v.findViewById(R.id.loadingLl);
        retryBtn = (BootstrapButton) v.findViewById(R.id.noInternetBtn);
        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) v.findViewById(R.id.right_drawer);
        errorMessage = (TextView) v.findViewById(R.id.articleErrorMessage);
        parallaxPart = (LinearLayout) v.findViewById(R.id.rootOfRootsArticle);
        contentProgressBar = (ProgressBar) v.findViewById(R.id.content_progressBar);

        imgs.clear();
        sections.clear();

        textViews.add(errorMessage);
        //((MaterialNavigationDrawer) this.getActivity()).getToolbar().setTitle(getResources().getString(R.string.drawer_random_article));

        viewsManager = new ArticleViewsManager(MyApplication.getAppContext());
        viewsManager.setLayout(rootArticleLl);
        viewsManager.destroyAllViews();

        display = getActivity().getWindowManager().getDefaultDisplay();
        display.getSize(size);


        mStructureAdapter = new ArticleStructureListAdapter(getActivity(), R.layout.article_structure_list_item, headers);
        mDrawerListView.setAdapter(mStructureAdapter);
        refreshHeaders();

        parallaxSv.setScrollViewCallbacks(this);


        setUpColorScheme();
        setLoadingLayout();

        ((MainActivity) getActivity()).setOnBackPressedListener(new OnBackPressedListener() {
            @Override
            public void doBack() {
                SessionArticleHistory item = MainActivity.sessionArticleHistory.get(MainActivity.sessionArticleHistory.size() - 2);
                MainActivity.sessionArticleHistory.remove(MainActivity.sessionArticleHistory.size() - 1);


                ArticleFragment fragment = new ArticleFragment();
                Bundle bundle = new Bundle();

                bundle.putInt("article_id", item.getId());
                bundle.putString("article_title", item.getTitle());
                fragment.setArguments(bundle);


                ((MaterialNavigationDrawer) getActivity()).setFragment(fragment, item.getTitle());
                ((MaterialNavigationDrawer) getActivity()).setSection(MainActivity.section_article);
            }
        });

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                finishActionMode();
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

        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        if (!((MaterialNavigationDrawer) getActivity()).getSupportActionBar().isShowing()) {
                            ((MaterialNavigationDrawer) getActivity()).getSupportActionBar().show();
                        }

                        if (headers.get(position).getView() != null) {
                            if (position == 0)
                                parallaxSv.smoothScrollTo(0, parallaxIv.getTop());
                            else
                                parallaxSv.smoothScrollTo(0, headers.get(position).getView().getTop() + parallaxIv.getHeight());
                            mDrawerLayout.closeDrawer(Gravity.RIGHT);
                        }
                    }
                });
            }
        });


        ((MaterialNavigationDrawer) this.getActivity()).setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                if (mDrawerLayout.getChildCount() > 0 && drawerView != null && mDrawerLayout != null && mStructureAdapter != null)
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);

                finishActionMode();
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


        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    if (article_id <= 0) {
                        setRandomPage();
                    } else {
                        rootArticleLl.removeAllViews();
                        imgs.clear();
                        recommendations.clear();
                        finishActionMode();
                        refreshHeaders();
                        fetchArticleInfo(article_id);
                    }
                } else
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
            }
        });

        mSwipeRefreshLayout.setDelegate(this);

        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), true);
        mSwipeRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        refreshViewHolder.setLoadingMoreText(getActivity().getResources().getString(R.string.load_more));
        refreshViewHolder.setRefreshingText(getActivity().getResources().getString(R.string.loading));
        refreshViewHolder.setPullDownRefreshText(getActivity().getResources().getString(R.string.pull_to_refresh));
        refreshViewHolder.setReleaseRefreshText(getActivity().getResources().getString(R.string.relase));
        refreshViewHolder.setRefreshViewBackgroundDrawableRes(R.mipmap.bga_refresh_loading02);

        mSwipeRefreshLayout.beginRefreshing();
        mSwipeRefreshLayout.endRefreshing();


        if (getActivity().getResources().getConfiguration().orientation == 2) {
            parallaxIv.setVisibility(View.GONE);
        }

        if (savedInstanceState != null) {
            article_id = savedInstanceState.getInt("article_id");
            article_title = savedInstanceState.getString("article_title");
        }


        if (article_id > 0) {
            rootArticleLl.removeAllViews();
            imgs.clear();
            recommendations.clear();
            finishActionMode();
            refreshHeaders();
            fetchArticleInfo(article_id);
        } else
            fetchRandomArticle();

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (article_id > 0) {
            outState.putInt("article_id", article_id);
            outState.putString("article_title", article_title);
        }
    }


    public static void finishActionMode() {
        if (mActionModes != null && mActionModes.size() > 0) {
            for (ActionMode item : mActionModes) {
                item.finish();
            }
            mActionModes.clear();
        }
    }

    private void fetchArticleContent(int id) {

        setContentLoadingLayout();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemContent(id), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        article_content = response.toString();
                        if (getActivity() != null) {
                            try {

                                JSONArray sections = response.getJSONArray(Article.KEY_SECTIONS);

                                for (int i = 0; i < sections.length(); i++) {
                                    JSONObject section = sections.getJSONObject(i);

                                    //Parsowanie zawartości
                                    JSONArray content = section.getJSONArray(Article.KEY_CONTENT);

                                    int level = section.getInt(Article.KEY_LEVEL);
                                    String title = section.getString(Article.KEY_TITLE);
                                    TextView headerView = viewsManager.addHeader(level, title);
                                    textViews.add(headerView);
                                    headers.add(new ArticleHeader(level, title, headerView));


                                    if (content.length() > 0) {
                                        for (int j = 0; j < content.length(); j++) {
                                            JSONObject content_section = content.getJSONObject(j);
                                            String type = content_section.getString(Article.KEY_TYPE);

                                            if (type.equals(Article.KEY_PARAGRAPH)) {
                                                String text = content_section.getString(Article.KEY_TEXT);
                                                if (text != null && text.trim().length() > 0)
                                                    textViews.add(viewsManager.addTextViewToLayout(text, level));
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
                                            int scaleTo = prefs.getInt(SharedPrefsKeys.ARTICLE_IMAGES_SIZE_PREF, BaseConfig.imageSize);
                                            ImageView iv = viewsManager.addImageViewToLayout(StringOperations.pumpUpSize(img_url, scaleTo), caption);
                                            imgs.add(new ArticleImage(img_url, caption, imgs.size()));

                                            final ArticleImage imageItem = imgs.get(imgs.size() - 1);

                                            iv.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getActivity(), GalleryActivity.class);
                                                    intent.putExtra(GalleryActivity.KEY_TYPE, GalleryActivity.KEY_RANDOM);
                                                    intent.putExtra(GalleryActivity.KEY_POSITON, imageItem.getPosition());
                                                    startActivity(intent);
                                                }
                                            });

                                        }
                                    }

                                }

                                mSwipeRefreshLayout.endRefreshing();

                                headers = ArrayHelpers.headersRemoveLevels(headers);
                                mStructureAdapter.notifyDataSetChanged();

                                fetchSimilarArticles();

                                if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                    setInternetPresentLayout();
                                else
                                    setNoInternetLayout();


                            } catch (JSONException e) {
                                e.printStackTrace();
                                mSwipeRefreshLayout.endRefreshing();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.endRefreshing();
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

                if (i == 0) //First list item, no padding
                    textViews.add(viewsManager.addListItemToLayout(text, tree_level, layout_level, true));
                else
                    textViews.add(viewsManager.addListItemToLayout(text, tree_level, layout_level, false));

                JSONArray nested_elements = element.getJSONArray(Article.KEY_ELEMENTS);

                if (nested_elements != null && nested_elements.length() > 0) {
                    fetchList(nested_elements, tree_level + 1, layout_level);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            mSwipeRefreshLayout.endRefreshing();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        finishActionMode();
        if (!((MaterialNavigationDrawer) getActivity()).getSupportActionBar().isShowing()) {
            ((MaterialNavigationDrawer) getActivity()).getSupportActionBar().show();
        }
        db.close();
    }

    private void fetchArticleInfo(final int id) {
        int[] array = {id};
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemDetalis(array), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (getActivity() != null) {
                            MainActivity.addToSessionArticleHistory(article_id, article_title);

                            try {


                                JSONObject item = response.getJSONObject(SearchResult.KEY_ITEMS).getJSONObject(String.valueOf(id));
                                String thumbnail_url = item.getString(Article.KEY_THUMBNAIL);

                                parallaxSv.scrollTo(0, parallaxIv.getTop());

                                if (thumbnail_url != null && thumbnail_url.trim().length() > 0 && !thumbnail_url.isEmpty()) {

                                    article_image = thumbnail_url;

                                    try {
                                        JSONObject orginal_dimens = item.getJSONObject(ArticleImage.KEY_ORGINAL_DIMENS);


                                        if (orginal_dimens != null) {
                                            int orginal_width = orginal_dimens.getInt(ArticleImage.KEY_WIDTH);

                                            if (orginal_width > BaseConfig.MAX_IMAGE_SIZE)
                                                orginal_width = BaseConfig.MAX_IMAGE_SIZE;


                                            thumbnail_url = StringOperations.pumpUpResolution(orginal_width, thumbnail_url);
                                        }

                                        imgs.add(new ArticleImage(thumbnail_url, imgs.size()));
                                    } catch (JSONException e) {
                                        ArticleHistoryItem iItem = new ArticleHistoryItem(article_id, System.currentTimeMillis(), article_title, null);
                                        db.addItem(iItem);
                                    }
                                }

                                if (imgs.size() > 0) {
                                    final ArticleImage image = imgs.get(0);

                                    ArticleHistoryItem iItem = new ArticleHistoryItem(article_id, System.currentTimeMillis(), article_title, image.getImg_url());
                                    db.addItem(iItem);

                                    Picasso.with(MyApplication.getAppContext()).load(image.getImg_url()).placeholder(ContextCompat.getDrawable(getActivity(), R.drawable.logo)).error(ContextCompat.getDrawable(getActivity(), R.drawable.logo)).into(parallaxIv, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            parallaxIv.setBackgroundColor(Color.WHITE);
                                            parallaxIv.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getActivity(), GalleryActivity.class);
                                                    intent.putExtra(GalleryActivity.KEY_TYPE, GalleryActivity.KEY_RANDOM);
                                                    intent.putExtra(GalleryActivity.KEY_POSITON, image.getPosition());
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onError() {

                                        }
                                    });
                                } else {
                                    setImageViewBackground(parallaxIv, ContextCompat.getDrawable(getActivity(), R.drawable.logo));
                                    parallaxIv.setOnClickListener(null);
                                    parallaxIv.setBackgroundColor(Color.TRANSPARENT);
                                    parallaxIv.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.logo));
                                    ArticleHistoryItem historyItem = new ArticleHistoryItem(article_id, System.currentTimeMillis(), article_title, null);
                                    db.addItem(historyItem);
                                }

                                fetchArticleContent(id);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                mSwipeRefreshLayout.endRefreshing();
                                if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                    fetchArticleContent(id);
                                else
                                    setNoInternetLayout();
                            }

                        }
                    }
                }

                , new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.endRefreshing();
                if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                    fetchArticleContent(id);
                else
                    setNoInternetLayout();
            }
        }

        );

        request.setRetryPolicy(new

                        DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );

        requestQueue.add(request);
    }

    private void fetchRandomArticle() {
        int listLimit = 100;
        loading = true;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlRandom(listLimit), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {


                            if (!NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                setNoInternetLayout();


                            JSONArray items = response.getJSONArray(Article.KEY_ITEMS);
                            int listSize = items.length();
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
                            //setImageViewBackground(parallaxIv, getResources().getDrawable(R.drawable.logo));

                            if (article_id > 0 && article_title != null && article_title.trim().length() > 0)
                                fetchArticleInfo(article_id);
                            else
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.random_article_fetching_error), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.endRefreshing();
                            setNoInternetLayout();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.endRefreshing();
                setNoInternetLayout();
            }
        });


        requestQueue.add(request);
    }

    private void fetchSimilarArticles() {
        final int id = article_id;
        int[] ids = {id};

        int loadingLimit = prefs.getInt(SharedPrefsKeys.RECOMMENDATIONS_LIMIT_PREF, BaseConfig.MAX_RELATED_PAGES);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlRelatedPages(ids, loadingLimit), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading = false;
                        try {
                            JSONObject items_object = response.getJSONObject(Article.KEY_ITEMS);

                            //Teoretycznie ten endpoint mógłby zwracać wiele takich, ale my chcemy tylko dla danego artykułu
                            JSONArray items = items_object.getJSONArray(String.valueOf(id));

                            if (items.length() > 0 && viewsManager != null && getActivity() != null) {
                                TextView tv = viewsManager.addHeader(2, getActivity().getResources().getString(R.string.relatedHeader));
                                textViews.add(tv);
                                headers.add(new ArticleHeader(2, getActivity().getResources().getString(R.string.relatedHeader), tv));
                                mStructureAdapter.notifyDataSetChanged();
                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject item = items.getJSONObject(i);

                                    int articleId = item.getInt(Recommendation.KEY_ID);
                                    String articleTitle = item.getString(Recommendation.KEY_TITLE);
                                    String imgUrl = item.getString(Recommendation.KEY_IMGURL);

                                    if (articleId >= 0 && articleTitle != null && articleTitle.trim().length() > 0) {

                                        final Recommendation recommendation = new Recommendation(articleId, articleTitle, imgUrl, recommendations.size());

                                        recommendations.add(recommendation);

                                        LinearLayout ll = viewsManager.addRecommendationButtonToLayout(recommendation);
                                        ll.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                                                    ArticleFragment fragment = new ArticleFragment();
                                                    Bundle bundle = new Bundle();
                                                    bundle.putInt("article_id", recommendation.getId());
                                                    bundle.putString("article_title", recommendation.getTitle());
                                                    fragment.setArguments(bundle);

                                                    ((MaterialNavigationDrawer) getActivity()).setFragment(fragment, article_title);
                                                    ((MaterialNavigationDrawer) getActivity()).setSection(MainActivity.section_article);
                                                } else
                                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            loading = false;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading = false;
            }
        });

        requestQueue.add(request);
    }


    private void setErrorMessage() {
        setUpColorScheme();
        if (NetworkUtils.isNetworkAvailable(getActivity()))
            errorMessage.setText(getActivity().getResources().getString(R.string.loading_error));
        else
            errorMessage.setText(getActivity().getResources().getString(R.string.no_internet_conn));
    }

    private void setNoInternetLayout() {
        loading = false;
        refreshHeaders();
        setErrorMessage();
        parallaxSv.setVisibility(View.GONE);
        noInternetLl.setVisibility(View.VISIBLE);
        loadingLl.setVisibility(View.GONE);
        contentProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(false);
    }

    private void setContentLoadingLayout() {
        loading = true;
        parallaxSv.setVisibility(View.GONE);
        noInternetLl.setVisibility(View.GONE);
        loadingLl.setVisibility(View.GONE);
        contentProgressBar.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setEnabled(false);
    }


    private void setInternetPresentLayout() {
        setErrorMessage();
        parallaxSv.setVisibility(View.VISIBLE);
        noInternetLl.setVisibility(View.GONE);
        loadingLl.setVisibility(View.GONE);
        contentProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(true);
    }

    private void setLoadingLayout() {
        parallaxSv.setVisibility(View.GONE);
        noInternetLl.setVisibility(View.GONE);
        loadingLl.setVisibility(View.VISIBLE);
        contentProgressBar.setVisibility(View.GONE);
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
    public void onDestroy() {
        super.onDestroy();
        viewsManager.destroyAllViews();
        nullifyAllVars();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewsManager.destroyAllViews();
        nullifyAllVars();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.article_menu, menu);

        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);
        MenuItem item = menu.findItem(R.id.menu_nightMode);
        item.setChecked(nightMode);
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

            case R.id.menu_nightMode:
                boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

                SharedPreferences.Editor editor = prefs.edit();


                if (nightMode) {
                    setUpNormalMode();
                    item.setChecked(false);
                    editor.putBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, false);
                } else {
                    setUpNightMode();
                    item.setChecked(true);
                    editor.putBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, true);
                }

                editor.commit();
                break;

            case R.id.menu_saveArticle:
                if (article_content != null) {
                    SavedArticlesDbHandler db = new SavedArticlesDbHandler(getActivity(), new OnDatabaseSaved() {
                        @Override
                        public void onSucess() {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.article_succesfully_saved), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRecordAlreadyExsists() {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.saved_article_already_exsists), Toast.LENGTH_SHORT).show();
                        }
                    });
                    String wikiName = prefs.getString(SharedPrefsKeys.CURRENT_WIKI_LABEL, BaseConfig.DEFAULT_TITLE);
                    db.addItem(new BookmarkedArticle(article_title, article_image, article_content, wikiName, APIEndpoints.WIKI_NAME, article_id));
                    db.close();
                } else
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.article_not_loaded_yey), Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        isSignificantDelta = Math.abs(scrollY - mLastScrollY) > mScrollThreshold;
        mLastScrollY = scrollY;
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = ((MaterialNavigationDrawer) getActivity()).getSupportActionBar();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean showToolbar = settings.getBoolean("TOOLBAR_HIDING_PREF", true);

        if (showToolbar) {
            if (scrollState == ScrollState.UP) {
                if (ab.isShowing() && mLastScrollY > size.y * 1.6 && isSignificantDelta) {
                    ab.hide();
                }
            } else if (scrollState == ScrollState.DOWN) {
                if (!ab.isShowing()) {
                    if (mLastScrollY > size.y * 1.6) {
                        if (isSignificantDelta)
                            ab.show();
                    } else
                        ab.show();
                }
            }
        } else {
            if (!ab.isShowing())
                ab.show();
        }

        isSignificantDelta = false;
    }

    private void setRandomPage() {
        if (!loading) {
            //setLoadingLayout();
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
            setImageViewBackground(parallaxIv, ContextCompat.getDrawable(getActivity(), R.drawable.logo));
            parallaxIv.setBackgroundColor(Color.TRANSPARENT);
            parallaxIv.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.logo));
            rootArticleLl.removeAllViews();
            imgs.clear();
            recommendations.clear();
            finishActionMode();
            refreshHeaders();
            textViews.clear();
            setUpColorScheme();
            fetchRandomArticle();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpColorScheme();
    }

    private void setUpColorScheme() {
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    private void setUpNightMode() {
        errorMessage.setTextColor(getActivity().getResources().getColor(R.color.nightFontColor));
        parallaxPart.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        noInternetLl.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mDrawerLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        parallaxSv.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mDrawerListView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mSwipeRefreshLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));


        mStructureAdapter.notifyDataSetChanged();

        for (TextView item : textViews) {
            item.setTextColor(getActivity().getResources().getColor(R.color.nightFontColor));
        }
    }

    private void setUpNormalMode() {
        errorMessage.setTextColor(getActivity().getResources().getColor(R.color.font_color));
        parallaxPart.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        noInternetLl.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mDrawerLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        parallaxSv.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mDrawerListView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mSwipeRefreshLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.background));


        mStructureAdapter.notifyDataSetChanged();

        for (TextView item : textViews) {
            item.setTextColor(getActivity().getResources().getColor(R.color.font_color));
        }
    }


    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {

        if (!initialSwipe) {
            if (!loading) {
                setLoadingLayout();

                boolean fetchNew = prefs.getBoolean(SharedPrefsKeys.RANDOM_ARTICLE_FETCHING_PREF, false);

                if (fetchNew) {
                    if (NetworkUtils.isNetworkAvailable(getActivity()))
                        setRandomPage();
                    else
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                } else {
                    rootArticleLl.removeAllViews();
                    imgs.clear();
                    recommendations.clear();
                    finishActionMode();
                    refreshHeaders();
                    fetchArticleInfo(article_id);
                }
            } else
                refreshLayout.endRefreshing();
        } else
            initialSwipe = false;
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}

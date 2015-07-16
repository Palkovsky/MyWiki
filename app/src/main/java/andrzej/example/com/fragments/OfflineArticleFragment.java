package andrzej.example.com.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.GalleryActivity;
import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.adapters.ArticleStructureListAdapter;
import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.ArticleHeader;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.models.ArticleImage;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.observablescrollview.ObservableScrollView;
import andrzej.example.com.observablescrollview.ObservableScrollViewCallbacks;
import andrzej.example.com.observablescrollview.ScrollState;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.ArrayHelpers;
import andrzej.example.com.utils.ArticleViewsManager;
import andrzej.example.com.utils.OnBackPressedListener;
import andrzej.example.com.utils.StringOperations;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class OfflineArticleFragment extends Fragment implements OnBackPressedListener, AdapterView.OnItemClickListener, DrawerLayout.DrawerListener,  ObservableScrollViewCallbacks, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "offlinearticlefragmet";

    //Ui
    private RelativeLayout mRootView;
    private ListView mDrawerListView;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mMainArticleContent;
    private ObservableScrollView mMainScrollView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    //Vitals
    private int articleId = -1;
    private String articleTitle = "";
    private String articleContent = "";
    private String articleImage = "";
    private String wikiUrl = null;

    //Finals
    public static final String BUNDLE_KEY_ID = "article_id";
    public static final String BUNDLE_KEY_TITLE = "article_title";
    public static final String BUNDLE_KEY_CONTENT = "article_content";
    public static final String BUNDLE_KEY_WIKI_IMAGE = "article_image";
    public static final String BUNDLE_KEY_WIKI_URL = "wiki_url";


    //Lists
    private static List<ArticleImage> imgs = new ArrayList<ArticleImage>();
    private List<TextView> textViews = new ArrayList<>();
    private List<ArticleHeader> headers = new ArrayList<>();

    //Adapters
    ArticleStructureListAdapter mStructureAdapter;

    //Utils
    SharedPreferences prefs;
    ArticleViewsManager viewsManager;

    //Flags
    private boolean isSignificantDelta = false;

    //stuff
    private int mLastScrollY;
    private int mScrollThreshold = 15;
    Display display;
    Point size = new Point();

    public OfflineArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        viewsManager = new ArticleViewsManager(getActivity());

        Bundle bundle = this.getArguments();
        articleId = bundle.getInt(BUNDLE_KEY_ID);
        articleTitle = bundle.getString(BUNDLE_KEY_TITLE);
        articleContent = bundle.getString(BUNDLE_KEY_CONTENT);
        articleImage = bundle.getString(BUNDLE_KEY_WIKI_IMAGE);
        wikiUrl = bundle.getString(BUNDLE_KEY_WIKI_URL);

        display = getActivity().getWindowManager().getDefaultDisplay();
        display.getSize(size);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_offline_article, container, false);

        setHasOptionsMenu(true);

        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout);
        mRootView = (RelativeLayout) v.findViewById(R.id.offlineArticleRootView);
        mMainScrollView = (ObservableScrollView) v.findViewById(R.id.offlineArticleScrollView);
        mDrawerListView = (ListView) v.findViewById(R.id.right_drawer);
        mMainArticleContent = (LinearLayout) v.findViewById(R.id.mainArticleContent);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.offline_article_swipe_refresh_layout);

        //Adapters
        mStructureAdapter = new ArticleStructureListAdapter(getActivity(), R.layout.article_structure_list_item, headers);
        mDrawerListView.setAdapter(mStructureAdapter);

        //Listeners
        ((MainActivity) getActivity()).setOnBackPressedListener(this);
        ((MaterialNavigationDrawer) this.getActivity()).setDrawerListener(this);
        mDrawerListView.setOnItemClickListener(this);
        mMainScrollView.setScrollViewCallbacks(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);


        //Starting methods
        viewsManager.setLayout(mMainArticleContent);
        addToArticleHistory();
        refreshArticle();
        setUpColorScheme();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MaterialNavigationDrawer) getActivity()).getSupportActionBar().show();
    }

    private void refreshArticle() {
        headers.clear();
        textViews.clear();
        mStructureAdapter.notifyDataSetChanged();
        headers.add(new ArticleHeader(1, articleTitle, null));
        viewsManager.destroyAllViews();
        parseJsonObject(articleContent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();

        //Check if night mode enabled, if it is - check box
        inflater.inflate(R.menu.menu_offline_article, menu);
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);
        MenuItem item = menu.findItem(R.id.menu_nightMode);
        item.setChecked(nightMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

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

            case R.id.menu_structure:
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                else
                    mDrawerLayout.openDrawer(Gravity.RIGHT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseJsonObject(String articleJSONString) {
        try {

            JSONObject response = new JSONObject(articleJSONString);
            JSONArray sections = response.getJSONArray(Article.KEY_SECTIONS);

            for (int i = 0; i < sections.length(); i++) {
                JSONObject section = sections.getJSONObject(i);

                //Parsowanie zawartości
                JSONArray content = section.getJSONArray(Article.KEY_CONTENT);

                final int level = section.getInt(Article.KEY_LEVEL);
                String title = section.getString(Article.KEY_TITLE);


                TextView headerView = viewsManager.addHeader(level, title);
                textViews.add(headerView);
                if (level != 1)
                    headers.add(new ArticleHeader(level, title, headerView));
                else
                    headers.get(0).setView(headerView);


                if (content.length() > 0) {
                    for (int j = 0; j < content.length(); j++) {
                        JSONObject content_section = content.getJSONObject(j);
                        String type = content_section.getString(Article.KEY_TYPE);

                        if (type.equals(Article.KEY_PARAGRAPH)) {
                            final String text = content_section.getString(Article.KEY_TEXT);
                            if (text != null && text.trim().length() > 0) {
                                textViews.add(viewsManager.addTextViewToLayout(text, level));
                            }
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
                                intent.putExtra(GalleryActivity.KEY_TYPE, GalleryActivity.KEY_OFFLINE);
                                intent.putExtra(GalleryActivity.KEY_POSITON, imageItem.getPosition());
                                startActivity(intent);
                                //Toast.makeText(getActivity(), imageItem.getPosition()+"", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }

                mSwipeRefreshLayout.setRefreshing(false);

            }


            headers = ArrayHelpers.headersRemoveLevels(headers);
            mStructureAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        }
    }

    private void addToArticleHistory() {
        ArticleHistoryItem historyItem = new ArticleHistoryItem(articleId, System.currentTimeMillis(), articleTitle, articleImage, wikiUrl);
        ArticleHistoryDbHandler historyDbHandler = new ArticleHistoryDbHandler(getActivity());
        historyDbHandler.addItem(historyItem);
        historyDbHandler.close();
    }


    private void setUpNightMode() {
        mRootView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mDrawerLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mDrawerListView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mStructureAdapter.notifyDataSetChanged();

        for (TextView item : textViews) {
            item.setTextColor(getActivity().getResources().getColor(R.color.nightFontColor));
        }
    }

    private void setUpNormalMode() {
        mRootView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mDrawerLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mDrawerListView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mStructureAdapter.notifyDataSetChanged();

        for (TextView item : textViews) {
            item.setTextColor(getActivity().getResources().getColor(R.color.font_color));
        }
    }

    private void setUpColorScheme() {
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    @Override
    public void doBack() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            Fragment savedArticlesFragment = new SavedArticlesFragment();
            ((MaterialNavigationDrawer) getActivity()).setFragment(savedArticlesFragment, getActivity().getResources().getString(R.string.drawer_bookmared_articles));
            ((MaterialNavigationDrawer) getActivity()).setSection(MainActivity.section_bookmarks);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {

                if (headers.get(position).getView() != null) {
                    if (!((MaterialNavigationDrawer) getActivity()).getSupportActionBar().isShowing()) {
                        ((MaterialNavigationDrawer) getActivity()).getSupportActionBar().show();
                    }
                    mMainScrollView.smoothScrollTo(0, headers.get(position).getView().getTop());
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
                }
            }
        });
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        }
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

    public static List<ArticleImage> getImgs() {
        return imgs;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewsManager.destroyAllViews();
    }

    @Override
    public void onRefresh() {
        refreshArticle();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}

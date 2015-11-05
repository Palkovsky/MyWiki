package andrzej.example.com.fragments.MainTabs;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.MainTabs.adapters.MainRecyclerAdapter;
import andrzej.example.com.libraries.refreshlayout.BGANormalRefreshViewHolder;
import andrzej.example.com.libraries.refreshlayout.BGARefreshLayout;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.MainPageArticle;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnItemClickListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class TrendingArticlesFragment extends Fragment implements OnItemClickListener, View.OnClickListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private static RelativeLayout mRootView;
    private static RecyclerView mRecyclerView;
    private static LinearLayout noInternetLayout;
    private static BootstrapButton mRetryBtn;
    private static TextView mErrorMsg;
    private static BGARefreshLayout mRefreshLayout;
    private static TextView mNoRecordsTextView;
    private static BootstrapButton mNoRecordsBtn;
    private LinearLayout mNoRecordsLayout;
    private LinearLayout mLoadingLayout;
    private static RelativeLayout listWrapper;

    //List
    private List<MainPageArticle> mArticles = new ArrayList<>();


    //Networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    //Adapter
    private static MainRecyclerAdapter mAdapter;

    //Flags
    private boolean initialSwipe = true;

    public TrendingArticlesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.tab_trending_articles, container, false);

        mRootView = (RelativeLayout) v.findViewById(R.id.rootView);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.trendingArticles_recyclerView);
        noInternetLayout = (LinearLayout) v.findViewById(R.id.trending_no_internet_layout);
        mErrorMsg = (TextView) v.findViewById(R.id.trending_error_msg);
        mRetryBtn = (BootstrapButton) v.findViewById(R.id.noInternetBtn);
        mRefreshLayout = (BGARefreshLayout) v.findViewById(R.id.main_swipe_refresh_layout);
        mNoRecordsBtn = (BootstrapButton) v.findViewById(R.id.retryBtn);
        mNoRecordsTextView = (TextView) v.findViewById(R.id.no_articles_tv);
        mNoRecordsLayout = (LinearLayout) v.findViewById(R.id.no_records_layout);
        mLoadingLayout = (LinearLayout) v.findViewById(R.id.loadingLl);
        listWrapper = (RelativeLayout) v.findViewById(R.id.listWrapper);

        //Set up recycler view
        mAdapter = new MainRecyclerAdapter(getActivity(), mArticles);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getActivity().getResources().getColor(R.color.divider))
                .sizeResId(R.dimen.divider)
                .showLastDivider()
                .build());
        mRecyclerView.setAdapter(mAdapter);

        //Listeners
        mAdapter.setOnItemClickListener(this);
        mRetryBtn.setOnClickListener(this);
        mNoRecordsBtn.setOnClickListener(this);


        mRefreshLayout.setDelegate(this);
        BGANormalRefreshViewHolder refreshViewHolder = new BGANormalRefreshViewHolder(getActivity(), true);
        mRefreshLayout.setRefreshViewHolder(refreshViewHolder);

        refreshViewHolder.setLoadingMoreText(getActivity().getResources().getString(R.string.load_more));
        refreshViewHolder.setRefreshingText(getActivity().getResources().getString(R.string.loading));
        refreshViewHolder.setPullDownRefreshText(getActivity().getResources().getString(R.string.pull_to_refresh));
        refreshViewHolder.setReleaseRefreshText(getActivity().getResources().getString(R.string.relase));
        refreshViewHolder.setRefreshViewBackgroundDrawableRes(R.mipmap.bga_refresh_loading02);

        mRefreshLayout.beginRefreshing();
        mRefreshLayout.endRefreshing();

        setUpLoadingLayout();
        setUpColorScheme();
        fetchPopularArticles();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        LinearLayoutManager layoutManager = ((LinearLayoutManager)mRecyclerView.getLayoutManager());
        MainTabsPrefs.LAST_TRENDING_POS = layoutManager.findFirstVisibleItemPosition();
    }

    private void setUpColorScheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    public static void setUpNightMode() {
        mRootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.nightBackground));
        mErrorMsg.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
        mNoRecordsTextView.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
        mAdapter.notifyDataSetChanged();
    }

    public static void setUpNormalMode() {
        mRootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.background));
        mErrorMsg.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
        mNoRecordsTextView.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
        mAdapter.notifyDataSetChanged();
    }

    public void setUpLoadingLayout() {
        noInternetLayout.setVisibility(View.GONE);
        listWrapper.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
        mNoRecordsLayout.setVisibility(View.GONE);
    }

    public void setUpNoInternetLayout() {
        noInternetLayout.setVisibility(View.VISIBLE);
        listWrapper.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mNoRecordsLayout.setVisibility(View.GONE);
    }

    public void setUpInternetPresentLayout() {
        noInternetLayout.setVisibility(View.GONE);
        listWrapper.setVisibility(View.VISIBLE);
        mLoadingLayout.setVisibility(View.GONE);
        mNoRecordsLayout.setVisibility(View.GONE);
    }

    public void setUpNoRecordsLayout() {
        noInternetLayout.setVisibility(View.GONE);
        listWrapper.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        mNoRecordsLayout.setVisibility(View.VISIBLE);
    }

    private void fetchPopularArticles() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlArticlesTop(BaseConfig.MAIN_PAGE_ARTICLES_LIMIT), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (getActivity() != null) {
                            try {
                                JSONArray items = response.getJSONArray(MainPageArticle.KEY_ITEMS);

                                for (int i = 0; i < items.length(); i++) {
                                    JSONObject item = items.getJSONObject(i);

                                    int id = item.getInt(MainPageArticle.KEY_ID);
                                    String title = item.getString(MainPageArticle.KEY_TITLE);

                                    mArticles.add(new MainPageArticle(id, title));
                                    mAdapter.notifyItemInserted(mArticles.size() - 1);
                                }

                                if (mArticles.size() > 0) {
                                    setUpInternetPresentLayout();
                                    if(mArticles.size() > MainTabsPrefs.LAST_TRENDING_POS)
                                        mRecyclerView.scrollToPosition(MainTabsPrefs.LAST_TRENDING_POS);
                                }else {
                                    if (NetworkUtils.isNetworkAvailable(getActivity()))
                                        setUpNoRecordsLayout();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                setUpNoInternetLayout();
                            } finally {
                                mRefreshLayout.endRefreshing();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefreshLayout.endRefreshing();
                setUpNoInternetLayout();
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    @Override
    public void onItemClick(View view, int position) {

        MainTabsPrefs.LAST_TRENDING_POS = position;

        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            MainPageArticle item = mArticles.get(position);
            ArticleFragment fragment = new ArticleFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("article_id", item.getId());
            bundle.putString("article_title", item.getTitle());
            fragment.setArguments(bundle);

            ((MaterialNavigationDrawer) getActivity()).setFragment(fragment, item.getTitle());
            ((MaterialNavigationDrawer) getActivity()).setSection(MainActivity.section_article);
        } else
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
    }

    private void reloadArticles() {
        setUpLoadingLayout();
        mArticles.clear();
        mAdapter.notifyDataSetChanged();
        fetchPopularArticles();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.noInternetBtn:
                if (NetworkUtils.isNetworkAvailable(getActivity()))
                    reloadArticles();
                else
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                break;
            case R.id.retryBtn:
                if (NetworkUtils.isNetworkAvailable(getActivity()))
                    reloadArticles();
                else
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (!initialSwipe)
            reloadArticles();
        else
            initialSwipe = false;
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }
}

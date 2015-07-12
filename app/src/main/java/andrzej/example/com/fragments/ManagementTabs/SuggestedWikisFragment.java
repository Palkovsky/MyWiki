package andrzej.example.com.fragments.ManagementTabs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.activities.WikiInfoActivity;
import andrzej.example.com.databases.WikisFavsDbHandler;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.fragments.ManagementTabs.adapters.SuggestedListViewAdapter;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SuggestedItem;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.researchapi.APIStatisticalEndpoints;
import andrzej.example.com.researchapi.RequestHandler;
import andrzej.example.com.utils.WikiManagementHelper;
import andrzej.example.com.views.MaterialEditText;
import andrzej.example.com.views.StaggeredGridView;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by andrzej on 24.06.15.
 */
public class SuggestedWikisFragment extends Fragment implements StaggeredGridView.OnItemClickListener, View.OnClickListener, StaggeredGridView.OnItemLongClickListener {

    //UI Elements
    private static RelativeLayout rootView;
    private static RelativeLayout contentView;
    private static LinearLayout errorLayout;
    private static TextView errorMessage;
    private static StaggeredGridView mStaggeredGridView;
    private static BootstrapButton mRetryBtn;
    private static LinearLayout mLoadingLayout;

    private int page = 1;

    //List
    private static List<SuggestedItem> mSuggestedItems = new ArrayList<>();

    //Adapters
    private static SuggestedListViewAdapter mAdapter;

    //Utils
    private WikiManagementHelper mHelper;
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;
    private RequestHandler mRequestHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new WikiManagementHelper(getActivity());
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();
        mRequestHandler = new RequestHandler(getActivity());
    }


    /*
    Tutaj nie będziemy używać zwykłej listy tylko ładnych CardView w
    RecyclerView. Obecnie mamy rozwiązanie tymczasowe.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_suggested_wikis, container, false);


        rootView = (RelativeLayout) v.findViewById(R.id.suggested_rootView);
        contentView = (RelativeLayout) v.findViewById(R.id.suggested_contentView);
        errorLayout = (LinearLayout) v.findViewById(R.id.suggested_errorLayout);
        errorMessage = (TextView) v.findViewById(R.id.suggested_errorMsg);
        mStaggeredGridView = (StaggeredGridView) v.findViewById(R.id.staggeredGridView);
        mRetryBtn = (BootstrapButton) v.findViewById(R.id.suggested_retryBtn);
        mLoadingLayout = (LinearLayout) v.findViewById(R.id.suggested_loadingLayout);


        //Adapters
        mAdapter = new SuggestedListViewAdapter(getActivity(), mSuggestedItems);

        //Listeners
        mRetryBtn.setOnClickListener(this);
        mStaggeredGridView.setOnItemClickListener(this);
        mStaggeredGridView.setOnItemLongClickListener(this);

        //Grid View Config
        int margin = getResources().getDimensionPixelSize(R.dimen.margin);
        mStaggeredGridView.setSelector(ContextCompat.getDrawable(getActivity(), R.drawable.suggested_item_selector));
        mStaggeredGridView.setItemMargin(margin); // set the GridView margin
        mStaggeredGridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well
        mStaggeredGridView.setAdapter(mAdapter);

        setLoadingLayout();



        /*
        //Try this when running server
        restartArticles();
        fetchArticles();
*/


        //Else try it(Hardcoded wiki set.).
        populateDataset();
        setNormalLayout();
        updateViews();


        return v;
    }


    private void fetchArticles() {
        getPopularArticlesList();
    }


    private void restartArticles() {
        mSuggestedItems.clear();
        updateViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpColorScheme();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHelper.closeDbs();
    }

    public static void updateViews() {
        mAdapter.notifyDataSetChanged();
        reInitViews();
    }

    public static void reInitViews() {
        if (mSuggestedItems == null || mSuggestedItems.size() <= 0) {
            contentView.setVisibility(View.GONE);
            setErrorMessage();
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            contentView.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
    }

    private static void setErrorLayout() {
        contentView.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.GONE);
        setErrorMessage();
        errorLayout.setVisibility(View.VISIBLE);
    }

    private static void setNormalLayout() {
        contentView.setVisibility(View.VISIBLE);
        mLoadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
    }

    private static void setLoadingLayout() {
        errorLayout.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        mLoadingLayout.setVisibility(View.VISIBLE);
    }

    public static void setUpNightMode() {
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.nightBackground));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
        updateViews();
    }

    public static void setUpNormalMode() {
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.background));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
        updateViews();
    }

    private static void setErrorMessage() {
        if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
            errorMessage.setText(MyApplication.getAppContext().getResources().getString(R.string.no_suggested_wikis));
        else
            errorMessage.setText(MyApplication.getAppContext().getResources().getString(R.string.no_internet_conn));
    }

    public static void setUpColorScheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }


    @Override
    public void onItemClick(StaggeredGridView parent, View view, int position, long id) {
        int wikiId = mSuggestedItems.get(position).getId();

        Intent intent = new Intent(getActivity(), WikiInfoActivity.class);
        intent.putExtra(WikiInfoActivity.WIKI_ID_INTENT_KEY, wikiId);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(StaggeredGridView parent, View view, int position, long id) {
        SuggestedItem item = mSuggestedItems.get(position);
        final String title = item.getTitle();
        final String url = item.getUrl();

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(title)
                .items(R.array.sugested_dialog_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int which, CharSequence charSequence) {
                        switch (which) {
                            case 0: //Ustaw
                                if (!APIEndpoints.WIKI_NAME.equals(url)) {
                                    WikisHistoryDbHandler db = new WikisHistoryDbHandler(getActivity());
                                    if (db.itemExsists(url)) {
                                        WikiPreviousListItem item1 = db.getItemByUrl(url);
                                        mHelper.removeWiki(item1.getId());
                                    }
                                    db.close();

                                    WikisFavsDbHandler favs_db = new WikisFavsDbHandler(getActivity());
                                    if (favs_db.itemExsists(url)) {
                                        int id = favs_db.getItemByUrl(url).getId();
                                        favs_db.editItem(id, new WikiFavItem(id, title, url));
                                    }
                                    favs_db.close();

                                    mHelper.addWikiToPreviouslyUsed(title, url);

                                    APIEndpoints.WIKI_NAME = mHelper.cleanInputUrl(url);
                                    mHelper.setUrlAsPreference(APIEndpoints.WIKI_NAME, title);
                                    APIEndpoints.reInitEndpoints();

                                    MainActivity.account.setTitle(APIEndpoints.WIKI_NAME);
                                    MainActivity.account.setSubTitle(title);
                                    ((MaterialNavigationDrawer) getActivity()).notifyAccountDataChanged();


                                    FavouriteWikisFragment.updateDataset();
                                    PreviouslyUsedWikisFragment.updateRecords();

                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.wiki_succesfully_changed), Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.already_setted), Toast.LENGTH_SHORT).show();
                                break;

                            case 1: //Dodaj do ulubionych

                                WikisHistoryDbHandler db = new WikisHistoryDbHandler(getActivity());
                                WikisFavsDbHandler favs_db = new WikisFavsDbHandler(getActivity());
                                if (favs_db.itemExsists(url)) {
                                    int fav_id = favs_db.getItemByUrl(url).getId();
                                    favs_db.editItem(fav_id, new WikiFavItem(fav_id, title, url));
                                    int id = db.getItemByUrl(url).getId();
                                    db.editItem(id, new WikiPreviousListItem(id, title, url));

                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.alredy_existed_but_update), Toast.LENGTH_SHORT).show();
                                } else {
                                    if (db.itemExsists(url)) {
                                        int id = db.getItemByUrl(url).getId();
                                        db.editItem(id, new WikiPreviousListItem(id, title, url));
                                    } else
                                        mHelper.addWikiToPreviouslyUsed(title, url);
                                    mHelper.addWikiToFavs(title, url);
                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.wiki_succesfull_added_to_favs), Toast.LENGTH_SHORT).show();
                                }

                                db.close();
                                favs_db.close();

                                FavouriteWikisFragment.updateDataset();
                                PreviouslyUsedWikisFragment.updateRecords();
                                break;
                        }
                    }
                }).negativeText(R.string.back)
                .build();

        dialog.show();
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.suggested_retryBtn:
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    setLoadingLayout();
                    fetchArticles();
                } else
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void getPopularArticlesList() {

        JsonArrayRequest request = new JsonArrayRequest(APIStatisticalEndpoints.listGetEndpoint(page, APIStatisticalEndpoints.POPULARITY_FILTER), new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    if (NetworkUtils.isNetworkAvailable(getActivity())) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            int id = item.getInt(SuggestedItem.ID_FIELD);
                            String url = item.getString(SuggestedItem.URL_FIELD);
                            String title = item.getString(SuggestedItem.TITLE_FIELD);
                            String description = item.getString(SuggestedItem.DESCRIPTION_FIELD);
                            String logoUrl = item.getString(SuggestedItem.IMAGE_FIELD);

                            mSuggestedItems.add(new SuggestedItem(id, url, title, description, logoUrl));
                            updateViews();
                            setNormalLayout();
                        }
                    } else
                        reInitViews();
                } catch (JSONException e) {
                    Log.e(null, e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setErrorLayout();
            }
        }) {
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


    private void populateDataset() {
        String[] urls = {"harrypotter", "pl.harrypotter", "pl.leagueoflegends", "nonsensopedia", "godofwar"};
        String[] titles = {"Harry Potter Wiki", "Harry Potter Wiki PL", "League of Legends Wiki", "Nonsensopedia", "God of War Wiki"};
        String[] descriptions = {"The Harry Potter Wiki reveals plot details about the series. Read at your own risk!",
                "Polska encyklopedia o świecie Magii.", "", "", ""};
        String[] imageUrls = {null,
                "http://img4.wikia.nocookie.net/__cb68/harrypotter/pl/images/8/89/Wiki-wordmark.png",
                "http://img2.wikia.nocookie.net/__cb5/leagueoflegends/pl/images/8/89/Wiki-wordmark.png",
                "http://vignette1.wikia.nocookie.net/nonsensopedia/images/b/bc/Wiki.png/revision/latest?cb=20150101225319",
                "http://img2.wikia.nocookie.net/__cb15/godofwar/images/8/89/Wiki-wordmark.png"};

        for (int i = 0; i < urls.length; i++) {
            mSuggestedItems.add(new SuggestedItem(i, mHelper.cleanInputUrl(urls[i]), titles[i], descriptions[i], imageUrls[i]));
        }
    }

}
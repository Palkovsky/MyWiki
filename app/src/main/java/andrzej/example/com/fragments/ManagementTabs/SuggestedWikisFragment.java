package andrzej.example.com.fragments.ManagementTabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.fragments.ManagementTabs.adapters.SuggestedListViewAdapter;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SuggestedItem;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.WikiManagementHelper;
import andrzej.example.com.views.StaggeredGridView;

/**
 * Created by andrzej on 24.06.15.
 */
public class SuggestedWikisFragment extends Fragment implements StaggeredGridView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    //UI Elements
    private static RelativeLayout rootView;
    private static RelativeLayout contentView;
    private static LinearLayout errorLayout;
    private static TextView errorMessage;
    private static StaggeredGridView mStaggeredGridView;

    //List
    private static List<SuggestedItem> mSuggestedItems = new ArrayList<>();

    //Adapters
    private static SuggestedListViewAdapter mAdapter;

    //Utils
    private WikiManagementHelper mHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new WikiManagementHelper(getActivity());
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

        populateDataset();

        //Adapters
        mAdapter = new SuggestedListViewAdapter(getActivity(), mSuggestedItems);

        //Grid View Config
        int margin = getResources().getDimensionPixelSize(R.dimen.margin);
        mStaggeredGridView.setSelector(ContextCompat.getDrawable(getActivity(), R.drawable.suggested_item_selector));
        mStaggeredGridView.setItemMargin(margin); // set the GridView margin
        mStaggeredGridView.setPadding(margin, 0, margin, 0); // have the margin on the sides as well
        mStaggeredGridView.setAdapter(mAdapter);

        mStaggeredGridView.setOnItemClickListener(this);

        updateViews();

        return v;
    }


    private void populateDataset() {
        String[] urls = {"harrypotter", "pl.harrypotter", "pl.leagueoflegend", "nonsensopedia", "godofwar"};
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
        Toast.makeText(getActivity(), "Click POS: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        populateDataset();
        updateViews();
    }

}
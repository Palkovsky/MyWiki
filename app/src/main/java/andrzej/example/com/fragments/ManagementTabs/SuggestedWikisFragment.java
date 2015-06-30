package andrzej.example.com.fragments.ManagementTabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;

/**
 * Created by andrzej on 24.06.15.
 */
public class SuggestedWikisFragment extends Fragment {

    //UI Elements
    private static RelativeLayout rootView;
    private static ListView suggestedWikisList;
    private static PreviouslyUsedListAdapter mAdapter;
    private static LinearLayout errorLayout;
    private static TextView errorMessage;

    //List
    private static ArrayList<WikiPreviousListItem> mWikisList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /*
    Tutaj nie będziemy używać zwykłej listy tylko ładnych CardView w
    RecyclerView. Obecnie mamy rozwiązanie tymczasowe.
     */

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_suggested_wikis, container, false);

        populateList();
        mAdapter = new PreviouslyUsedListAdapter(getActivity(), mWikisList);

        rootView = (RelativeLayout) v.findViewById(R.id.suggested_rootView);
        errorLayout = (LinearLayout) v.findViewById(R.id.suggested_errorLayout);
        errorMessage = (TextView) v.findViewById(R.id.suggested_errorMsg);
        suggestedWikisList = (ListView) v.findViewById(R.id.suggested_list);

        suggestedWikisList.setAdapter(mAdapter);

        return v;
    }


    private static void populateList() {
        String[] titles = {"GTA Wikia", "MLP Wikia", "Equestria Girls Wikia"};
        String[] urls = {"gta", "pl.mlp", "pl.mlpeg"};

        for (int i = 0; i < titles.length; i++) {
            mWikisList.add(new WikiPreviousListItem(titles[i], urls[i]));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpColorScheme();
    }


    private static void reInitViews() {
        if (mWikisList == null || mWikisList.size() <= 0) {
            suggestedWikisList.setVisibility(View.GONE);
            setErrorMessage();
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            suggestedWikisList.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
    }

    public static void setUpNightMode() {
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.nightBackground));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));

        mWikisList.clear();
        populateList();
        reInitViews();
        mAdapter.notifyDataSetChanged();
    }

    public static void setUpNormalMode() {
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.background));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));

        mWikisList.clear();
        populateList();
        reInitViews();
        mAdapter.notifyDataSetChanged();
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
}
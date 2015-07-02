package andrzej.example.com.fragments.ManagementTabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.fragments.ManagementTabs.adapters.FavoritesAdapter;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.WikiManagementHelper;
import andrzej.example.com.views.DividerItemDecoration;

/**
 * Created by andrzej on 02.07.15.
 */
public class FavouriteWikisFragment extends Fragment {

    //UI Elements
    private static RelativeLayout rootView;
    private RelativeLayout contentView;
    private static LinearLayout errorLayout;
    private RecyclerView recyclerView;
    private static TextView errorMessage;

    //Utils
    private WikiManagementHelper mHelper;

    //List
    private List<WikiFavItem> mFavs = new ArrayList<>();

    //Adapter
    static FavoritesAdapter mAdapter;

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
        View v = inflater.inflate(R.layout.fragment_favourites, container, false);


        rootView = (RelativeLayout) v.findViewById(R.id.favs_rootView);
        contentView = (RelativeLayout) v.findViewById(R.id.favs_contentView);
        recyclerView = (RecyclerView) v.findViewById(R.id.favs_recyclerView);
        errorLayout = (LinearLayout) v.findViewById(R.id.favs_errorLayout);
        errorMessage = (TextView) v.findViewById(R.id.favs_errorMsg);


        //Set up recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mAdapter = new FavoritesAdapter(mFavs);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        populateDataset();

        return v;
    }

    private void populateDataset(){
        String[] labels = {"GTA Wikia PL", "FF Wikia PL", "OGame Wikia"};
        String[] urls = {"pl.gta", "pl.finalfantasy", "ogame"};

        for(int i=0; i<labels.length; i++){
            mFavs.add(new WikiFavItem(labels[i], mHelper.cleanInputUrl(urls[i])));
            mAdapter.notifyItemInserted(mFavs.size()-1);
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

    public static void setUpNightMode() {
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.nightBackground));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
        mAdapter.notifyDataSetChanged();
    }

    public static void setUpNormalMode() {
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.background));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
        mAdapter.notifyDataSetChanged();
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

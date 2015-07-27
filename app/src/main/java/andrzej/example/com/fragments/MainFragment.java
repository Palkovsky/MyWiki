package andrzej.example.com.fragments;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.fragments.MainTabs.MainTabsPrefs;
import andrzej.example.com.fragments.MainTabs.NewestArticlesFragment;
import andrzej.example.com.fragments.MainTabs.TrendingArticlesFragment;
import andrzej.example.com.fragments.MainTabs.adapters.MainTabsAdapter;
import andrzej.example.com.libraries.refreshlayout.BGAMoocStyleRefreshViewHolder;
import andrzej.example.com.libraries.refreshlayout.BGANormalRefreshViewHolder;
import andrzej.example.com.libraries.refreshlayout.BGARefreshLayout;
import andrzej.example.com.libraries.refreshlayout.BGARefreshViewHolder;
import andrzej.example.com.libraries.refreshlayout.BGAStickinessRefreshView;
import andrzej.example.com.libraries.refreshlayout.BGAStickinessRefreshViewHolder;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SuggestedItem;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.WikiManagementHelper;
import andrzej.example.com.views.SlidingTabLayout;


public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener {


    //UI Elements
    LinearLayout rootView;
    ViewPager mPager;
    SlidingTabLayout mTabs;
    private BGARefreshLayout mRefreshLayout;


    //Adapters
    MainTabsAdapter mTabsAdapter;

    //Networking
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;

    SharedPreferences prefs;

    private boolean initialSwipe = true;

    public MainFragment() {
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
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //Vitals initialization
        mTabsAdapter = new MainTabsAdapter(getActivity().getSupportFragmentManager(), MainTabsPrefs.tabIconsResIds, MainTabsPrefs.mTabsNum);

        //UI Initialization
        rootView = (LinearLayout) v.findViewById(R.id.main_rootView);
        mPager = (ViewPager) v.findViewById(R.id.mainPager);
        mTabs = (SlidingTabLayout) v.findViewById(R.id.mainTabs);


        //Tabs Config
        mPager.setOffscreenPageLimit(MainTabsPrefs.mTabsNum);
        mPager.setAdapter(mTabsAdapter);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
        mTabs.setCustomTabView(R.layout.custom_tab, 0);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });
        mTabs.setOnPageChangeListener(this);

        mTabs.setViewPager(mPager);

        return v;
    }


    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpColorScheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_fragment_menu, menu);

        MenuItem item = menu.findItem(R.id.menu_nightMode);
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);
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
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpNightMode() {
        rootView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        TrendingArticlesFragment.setUpNightMode();
        NewestArticlesFragment.setUpNightMode();
    }

    private void setUpNormalMode() {
        rootView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        TrendingArticlesFragment.setUpNormalMode();
        NewestArticlesFragment.setUpNormalMode();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

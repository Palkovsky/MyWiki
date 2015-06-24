package andrzej.example.com.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.fragments.ManagementTabs.TabsAdapter;
import andrzej.example.com.fragments.ManagementTabs.TabsPrefs;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.views.SlidingTabLayout;


public class WikisManagementFragment extends Fragment {

    FrameLayout rootView;
    TabsAdapter mAdapter;
    ViewPager mPager;
    SlidingTabLayout mTabs;

    public WikisManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved_articles, container, false);
        MainActivity.sessionArticleHistory.clear();

        mAdapter = new TabsAdapter(getActivity().getSupportFragmentManager(), TabsPrefs.mTitles, TabsPrefs.mTabsNum);

        rootView = (FrameLayout) v.findViewById(R.id.managementRootView);

        mPager = (ViewPager) v.findViewById(R.id.managementPager);
        mPager.setAdapter(mAdapter);

        // Assiging the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) v.findViewById(R.id.managementTabs);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mPager);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView.getChildCount() > 0)
            rootView.removeAllViews();
    }
}

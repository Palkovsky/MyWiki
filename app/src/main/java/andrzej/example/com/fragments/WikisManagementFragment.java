package andrzej.example.com.fragments;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;


import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.fragments.ManagementTabs.FavouriteWikisFragment;
import andrzej.example.com.fragments.ManagementTabs.PreviouslyUsedWikisFragment;
import andrzej.example.com.fragments.ManagementTabs.SuggestedWikisFragment;
import andrzej.example.com.fragments.ManagementTabs.adapters.TabsAdapter;
import andrzej.example.com.fragments.ManagementTabs.TabsPrefs;
import andrzej.example.com.utils.WikiManagementHelper;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.views.MaterialEditText;
import andrzej.example.com.views.SlidingTabLayout;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class WikisManagementFragment extends Fragment implements ViewPager.OnPageChangeListener, DrawerLayout.DrawerListener {

    LinearLayout rootView;
    TabsAdapter mAdapter;
    ViewPager mPager;
    SlidingTabLayout mTabs;
    SharedPreferences prefs;

    private WikiManagementHelper mHelper;

    public WikisManagementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new WikiManagementHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved_articles, container, false);
        setHasOptionsMenu(true);
        MainActivity.sessionArticleHistory.clear();

        mAdapter = new TabsAdapter(getActivity().getSupportFragmentManager(), TabsPrefs.mTitles, TabsPrefs.mTabsNum);

        rootView = (LinearLayout) v.findViewById(R.id.managementRootView);

        mPager = (ViewPager) v.findViewById(R.id.managementPager);
        mPager.setOffscreenPageLimit(TabsPrefs.mTabsNum);
        mPager.setAdapter(mAdapter);

        // Assiging the Sliding Tab Layout View
        mTabs = (SlidingTabLayout) v.findViewById(R.id.managementTabs);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });


        mTabs.setOnPageChangeListener(this);

        ((MaterialNavigationDrawer) this.getActivity()).setDrawerListener(this);

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mPager);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());




        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView.getChildCount() > 0)
            rootView.removeAllViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHelper.closeDbs();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.management_menu, menu);

        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);
        MenuItem item = menu.findItem(R.id.menu_nightMode);
        item.setChecked(nightMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_addNewWiki:
                boolean wrapInScrollView = true;

                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(getActivity().getResources().getString(R.string.wiki_man_input_title))
                        .customView(R.layout.dialog_new_wiki, wrapInScrollView)
                        .autoDismiss(false)
                        .negativeText(getActivity().getResources().getString(R.string.cancel))
                        .positiveText(getActivity().getResources().getString(R.string.ok)).build();

                View view = dialog.getCustomView();

                final MaterialEditText labelInput = (MaterialEditText) view.findViewById(R.id.mangementInputLabelET);
                final MaterialEditText urlInput = (MaterialEditText) view.findViewById(R.id.mangementInputUrlET);

                dialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        String url_input = urlInput.getText().toString().trim();
                        String label_input = labelInput.getText().toString().trim();

                        if (url_input.length() <= 0)
                            Toast.makeText(getActivity(), "Musisz podać URL", Toast.LENGTH_SHORT).show();
                        else {
                            if (!APIEndpoints.WIKI_NAME.equals(mHelper.cleanInputUrl(url_input))) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.wiki_succesfully_changed), Toast.LENGTH_SHORT).show();


                                mHelper.addWikiToPreviouslyUsed(label_input, url_input);
                                PreviouslyUsedWikisFragment.updateRecords();

                                APIEndpoints.WIKI_NAME = mHelper.cleanInputUrl(url_input);
                                setUrlAsPreference(APIEndpoints.WIKI_NAME, label_input);
                                APIEndpoints.reInitEndpoints();
                                if (label_input != null && label_input.trim().length() > 0)
                                    MainActivity.account.setTitle(label_input);
                                else
                                    MainActivity.account.setTitle(mHelper.stripUpWikiUrl(APIEndpoints.WIKI_NAME));
                                MainActivity.account.setSubTitle(APIEndpoints.WIKI_NAME);
                                ((MaterialNavigationDrawer) getActivity()).notifyAccountDataChanged();
                            } else
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.already_setted), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                });

                dialog.show();

                break;

            case R.id.menu_nightMode:
                boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

                SharedPreferences.Editor editor = prefs.edit();


                if (nightMode) {
                    PreviouslyUsedWikisFragment.setUpNormalMode();
                    SuggestedWikisFragment.setUpNormalMode();
                    FavouriteWikisFragment.setUpNormalMode();
                    item.setChecked(false);
                    editor.putBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, false);
                } else {
                    PreviouslyUsedWikisFragment.setUpNightMode();
                    SuggestedWikisFragment.setUpNightMode();
                    FavouriteWikisFragment.setUpNightMode();
                    item.setChecked(true);
                    editor.putBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, true);
                }

                editor.apply();
                break;

            case R.id.menu_deleteHistory:
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.menu_deleteHistory)
                        .content(R.string.removeWikisHistory)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.no)
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                WikisHistoryDbHandler db = new WikisHistoryDbHandler(getActivity());
                                db.turncateTable();
                                PreviouslyUsedWikisFragment.updateRecords();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);

                            }
                        })
                        .show();
                break;
        }

        return true;
    }

    private void setUrlAsPreference(String url, String label) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SharedPrefsKeys.CURRENT_WIKI_URL, url);

        if (label != null && label.trim().length() > 0)
            editor.putString(SharedPrefsKeys.CURRENT_WIKI_LABEL, label);
        else
            editor.putString(SharedPrefsKeys.CURRENT_WIKI_LABEL, mHelper.stripUpWikiUrl(url));

        editor.apply();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(PreviouslyUsedWikisFragment.mActionMode!=null)
            PreviouslyUsedWikisFragment.mActionMode.finish();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if(PreviouslyUsedWikisFragment.mActionMode!=null)
            PreviouslyUsedWikisFragment.mActionMode.finish();
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
}

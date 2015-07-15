package andrzej.example.com.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.adapters.SavedArticlesListAdapter;
import andrzej.example.com.databases.SavedArticlesDbHandler;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.BookmarkedArticle;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.views.MaterialEditText;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class SavedArticlesFragment extends Fragment implements DrawerLayout.DrawerListener, TextWatcher, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    // UI
    private FrameLayout mRootView;
    private ListView mListView;
    private MaterialEditText mFilterEditText;
    private TextView mErrorTextView;

    //Utils
    private SharedPreferences prefs;

    //Adapters
    private SavedArticlesListAdapter mAdapter;

    //lists
    private List<BookmarkedArticle> mSavedArticles = new ArrayList<>();

    //Action Mode
    public static ActionMode mActionMode;

    //Db
    SavedArticlesDbHandler savedArticlesDb;

    public SavedArticlesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mAdapter = new SavedArticlesListAdapter(getActivity(), mSavedArticles);
        savedArticlesDb = new SavedArticlesDbHandler(getActivity());
        MainActivity.sessionArticleHistory.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_saved_articles2, container, false);

        setHasOptionsMenu(true);

        //UI Initializationj
        mRootView = (FrameLayout) v.findViewById(R.id.savedArticlesRootView);
        mListView = (ListView) v.findViewById(R.id.savedArticlesList);
        mFilterEditText = (MaterialEditText) v.findViewById(R.id.savedEditText);
        mErrorTextView = (TextView) v.findViewById(R.id.noRecordsTv);

        //Listeners
        ((MaterialNavigationDrawer) this.getActivity()).setDrawerListener(this);
        mFilterEditText.addTextChangedListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if (checked) {
                    nr++;
                    mAdapter.setNewSelection(position, checked);
                } else {
                    nr--;
                    mAdapter.removeSelection(position);
                }
                mode.setTitle("Zaznaczone: " + nr);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                nr = 0;
                mActionMode = mode;
                MenuInflater inflater = getActivity().getMenuInflater();
                inflater.inflate(R.menu.delete_history_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_delete:
                        List<BookmarkedArticle> mSelected = mAdapter.getSelectedItems();
                        for (BookmarkedArticle savedArticle : mSelected) {
                            savedArticlesDb.deleteItem(savedArticle.getId());
                        }
                        savedArticlesDb.close();
                        mSavedArticles.removeAll(mSelected);
                        nr = 0;
                        mAdapter.clearSelection();
                        mode.finish();

                        updateList();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mAdapter.clearSelection();
                mActionMode = null;
            }
        });

        //Init
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setAdapter(mAdapter);
        mSavedArticles.clear();
        mSavedArticles.addAll(savedArticlesDb.getAllItems());
        updateList();
        setUpColorScheme();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFilterEditText.setText("");
        updateList();
    }

    private void reInitViews() {
        if (mSavedArticles.size() > 0)
            setRecordsPresentLayout();
        else
            setNoRecordsLayout();
    }

    private void clearList(){
        mSavedArticles.clear();
        mAdapter.notifyDataSetChanged();
        reInitViews();
    }

    private void updateList(){
        mAdapter.notifyDataSetChanged();
        reInitViews();
    }

    private void setNoRecordsLayout() {
        mErrorTextView.setText(getActivity().getResources().getString(R.string.no_records_found_savedArticles));
        mErrorTextView.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mFilterEditText.setVisibility(View.GONE);
    }

    private void setRecordsPresentLayout() {
        mErrorTextView.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        mFilterEditText.setVisibility(View.VISIBLE);
    }

    private void setUpNightMode() {
        mRootView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mListView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mErrorTextView.setTextColor(getActivity().getResources().getColor(R.color.nightFontColor));
        mFilterEditText.setHintTextColor(getActivity().getResources().getColor(R.color.nightBackgroundFontLight));
        mFilterEditText.setBaseColor(getActivity().getResources().getColor(R.color.nightBackgroundFontLight));
        mFilterEditText.setTextColor(getActivity().getResources().getColor(R.color.nightBackgroundFontLight));
        mFilterEditText.setBackgroundColor(getActivity().getResources().getColor(R.color.ColorPrimaryDark));
        mAdapter.notifyDataSetChanged();
    }

    private void setUpNormalMode() {
        mRootView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mListView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mErrorTextView.setTextColor(getActivity().getResources().getColor(R.color.font_color));
        mFilterEditText.setHintTextColor(getActivity().getResources().getColor(R.color.font_color));
        mFilterEditText.setBaseColor(getActivity().getResources().getColor(R.color.font_color));
        mFilterEditText.setTextColor(getActivity().getResources().getColor(R.color.font_color));
        mFilterEditText.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mAdapter.notifyDataSetChanged();
    }

    private void setUpColorScheme() {
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

        //Check if night mode enabled, if it is - check box
        inflater.inflate(R.menu.saved_articles_menu, menu);
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
        }

        InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext()
                .getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                mFilterEditText.getWindowToken(), 0);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (mFilterEditText != null)
            mFilterEditText.clearFocus();
        if(mActionMode != null && getActivity()!=null){
            mActionMode.finish();
        }
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        if (mFilterEditText != null && getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mFilterEditText.getWindowToken(), 0);
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String query = s.toString().trim();

        mSavedArticles.clear();

        if (query.trim().length() > 0)
            mSavedArticles.addAll(savedArticlesDb.getAllItemsLike(query));
        else
            mSavedArticles.addAll(savedArticlesDb.getAllItems());

        mAdapter.notifyDataSetChanged();
        if(mSavedArticles.size()>0){
            mErrorTextView.setText(getActivity().getResources().getString(R.string.no_records_found_savedArticles));
            mErrorTextView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }else{
            mErrorTextView.setText(getActivity().getResources().getString(R.string.no_record_matches_query));
            mErrorTextView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mListView.setItemChecked(position, !mAdapter.isPositionChecked(position));
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BookmarkedArticle article = mSavedArticles.get(position);

        Fragment fragment = new OfflineArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(OfflineArticleFragment.BUNDLE_KEY_CONTENT, article.getArticle_id());
        bundle.putString(OfflineArticleFragment.BUNDLE_KEY_TITLE, article.getTitle());
        bundle.putString(OfflineArticleFragment.BUNDLE_KEY_CONTENT, article.getContent());
        bundle.putString(OfflineArticleFragment.BUNDLE_KEY_WIKI_IMAGE, article.getImgUrl());
        bundle.putString(OfflineArticleFragment.BUNDLE_KEY_WIKI_URL, article.getWikiUrl());
        fragment.setArguments(bundle);

        ((MaterialNavigationDrawer) getActivity()).setFragment(fragment, article.getTitle());
        ((MaterialNavigationDrawer) getActivity()).setSection(MainActivity.section_offlineArticle);
    }
}

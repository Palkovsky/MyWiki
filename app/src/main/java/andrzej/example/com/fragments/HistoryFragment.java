package andrzej.example.com.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.adapters.HistoryListAdapter;

import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.views.MaterialEditText;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class HistoryFragment extends Fragment {

    public static final String TAG = "historyFragment";

    //UI
    TextView noRecordsTv;
    MaterialEditText filterEt;
    ListView listHistory;
    FrameLayout rootView;

    //ADapter
    public static HistoryListAdapter mAdapter;
    public static ActionMode mActionMode;

    //List
    List<ArticleHistoryItem> items;

    //Keys
    private static final String KEY_EDIT_TEXT_DATA = "editTextData";
    private static final String KEY_EDIT_TEXT_FOCUS = "editTextFocus";

    //Prefs
    SharedPreferences prefs;


    public HistoryFragment() {
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
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        MainActivity.sessionArticleHistory.clear();

        setHasOptionsMenu(true);

        ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());

        items = db.getAllItems();
        db.close();

        rootView = (FrameLayout) v.findViewById(R.id.history_rootView);
        noRecordsTv = (TextView) v.findViewById(R.id.noRecordsTv);
        filterEt = (MaterialEditText) v.findViewById(R.id.historyEditText);
        listHistory = (ListView) v.findViewById(R.id.historyList);


        mAdapter = new HistoryListAdapter(getActivity(), items);

        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (NetworkUtils.isNetworkAvailable(getActivity())) {

                    InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext()
                            .getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(
                            filterEt.getWindowToken(), 0);

                    ArticleHistoryItem item = items.get(position);

                    Fragment fragment = new ArticleFragment();
                    Bundle bundle = new Bundle();

                    bundle.putInt("article_id", item.getId());
                    bundle.putString("article_title", item.getLabel());
                    fragment.setArguments(bundle);

                    ((MaterialNavigationDrawer) getActivity()).setFragment(fragment, item.getLabel());
                    ((MaterialNavigationDrawer) getActivity()).setSection(MainActivity.section_article);
                } else
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
            }
        });

        listHistory.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                listHistory.setItemChecked(position, !mAdapter.isPositionChecked(position));
                return false;
            }
        });


        listHistory.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

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
                        ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());
                        List<ArticleHistoryItem> mSelected = mAdapter.getSelectedItems();
                        for (ArticleHistoryItem historyItem : mSelected) {
                            db.deleteItem(historyItem.getDb_id());
                        }
                        db.close();
                        items.removeAll(mSelected);
                        nr = 0;
                        mAdapter.clearSelection();
                        mode.finish();

                        reInitViews(items.size());
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mAdapter.clearSelection();
                mActionMode = null;
            }
        });

        ((MaterialNavigationDrawer) this.getActivity()).setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (filterEt != null)
                    filterEt.clearFocus();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (mAdapter != null && mActionMode != null)
                    mActionMode.finish();

                if (filterEt != null && getActivity() != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(filterEt.getWindowToken(), 0);
                }
            }


            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        listHistory.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listHistory.scrollTo(0, 0);

        filterEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = s.toString().trim();
                ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());

                items.clear();

                if (query.trim().length() > 0)
                    items.addAll(db.getAllItemsLike(query));
                else
                    items.addAll(db.getAllItems());


                db.close();
                mAdapter.notifyDataSetChanged();
                reInitViews(items.size());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        listHistory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext()
                        .getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        filterEt.getWindowToken(), 0);

                return false;
            }
        });

        listHistory.setAdapter(mAdapter);

        if (items.size() <= 0) {
            noRecordsTv.setVisibility(View.VISIBLE);
            listHistory.setVisibility(View.GONE);
            filterEt.setVisibility(View.GONE);
        }

        return v;
    }



    private void reInitViews(int size) {
        if (size <= 0) { // nie ma
            noRecordsTv.setVisibility(View.VISIBLE);
            listHistory.setVisibility(View.GONE);
        } else { // som
            noRecordsTv.setVisibility(View.GONE);
            listHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView.getChildCount() > 0)
            rootView.removeAllViews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.history_menu, menu);
        // Associate searchable configuration with the SearchView
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);
        MenuItem item = menu.findItem(R.id.menu_nightMode);
        item.setChecked(nightMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_removeAll:

                new MaterialDialog.Builder(getActivity()).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());
                        db.turncateTable();
                        db.close();

                        items.clear();
                        items = new ArrayList<ArticleHistoryItem>();
                        mAdapter.notifyDataSetChanged();

                        noRecordsTv.setVisibility(View.VISIBLE);
                        listHistory.setVisibility(View.GONE);

                        reInitViews(items.size());

                    }

                }).content(getActivity().getResources().getString(R.string.removeAllQuestion))
                        .positiveText(getActivity().getResources().getString(R.string.yes))
                        .negativeText(getActivity().getResources().getString(R.string.no)).show();


                break;

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
                filterEt.getWindowToken(), 0);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext()
                .getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                filterEt.getWindowToken(), 0);

        filterEt.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpColorScheme();
    }


    private void setUpNightMode() {
        rootView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        noRecordsTv.setTextColor(getActivity().getResources().getColor(R.color.nightFontColor));
        filterEt.setHintTextColor(getActivity().getResources().getColor(R.color.nightBackgroundFontLight));
        filterEt.setBaseColor(getActivity().getResources().getColor(R.color.nightBackgroundFontLight));
        filterEt.setTextColor(getActivity().getResources().getColor(R.color.nightBackgroundFontLight));
        filterEt.setBackgroundColor(getActivity().getResources().getColor(R.color.ColorPrimaryDark));

        ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());
        items.clear();
        items.addAll(db.getAllItems());
        db.close();
        mAdapter.notifyDataSetChanged();
        reInitViews(items.size());
    }

    private void setUpNormalMode() {
        rootView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        noRecordsTv.setTextColor(getActivity().getResources().getColor(R.color.font_color));
        filterEt.setHintTextColor(getActivity().getResources().getColor(R.color.font_color));
        filterEt.setBaseColor(getActivity().getResources().getColor(R.color.font_color));
        filterEt.setTextColor(getActivity().getResources().getColor(R.color.font_color));
        filterEt.setBackgroundColor(getActivity().getResources().getColor(R.color.background));

        ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());
        items.clear();
        items.addAll(db.getAllItems());
        db.close();
        mAdapter.notifyDataSetChanged();
        reInitViews(items.size());
    }

    private void setUpColorScheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }
}

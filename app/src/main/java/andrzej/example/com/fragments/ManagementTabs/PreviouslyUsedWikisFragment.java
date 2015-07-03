package andrzej.example.com.fragments.ManagementTabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.fragments.ManagementTabs.adapters.PreviouslyUsedListAdapter;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.WikiManagementHelper;
import andrzej.example.com.views.MaterialEditText;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by andrzej on 24.06.15.
 */
public class PreviouslyUsedWikisFragment extends Fragment implements View.OnClickListener {

    //UI Elements
    private static RelativeLayout rootView;
    private static ListView previouslyUsedList;
    private static PreviouslyUsedListAdapter mAdapter;
    private static LinearLayout errorLayout;
    private static TextView errorMessage;
    private BootstrapButton addWikiButton;

    public static ActionMode mActionMode;


    //Utils
    private WikiManagementHelper mHelper;

    //List
    private static ArrayList<WikiPreviousListItem> mWikisList = new ArrayList<>();

    //Prefs
    SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHelper = new WikiManagementHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_previously_used_wikis, container, false);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        mAdapter = new PreviouslyUsedListAdapter(getActivity(), mWikisList);

        rootView = (RelativeLayout) v.findViewById(R.id.previuslyUsed_rootView);
        errorLayout = (LinearLayout) v.findViewById(R.id.previuslyUsed_errorLayout);
        errorMessage = (TextView) v.findViewById(R.id.previuslyUsed_errorMsg);
        previouslyUsedList = (ListView) v.findViewById(R.id.previuslyUsed_list);
        addWikiButton = (BootstrapButton) v.findViewById(R.id.previuslyUsed_addWikiBtn);

        //Listeners
        addWikiButton.setOnClickListener(this);


        previouslyUsedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WikiPreviousListItem item = mWikisList.get(position);
                String url = item.getUrl();
                String label = item.getTitle();

                if (!APIEndpoints.WIKI_NAME.equals(url) && parent.isClickable()) {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.wiki_succesfully_changed), Toast.LENGTH_SHORT).show();

                    WikisHistoryDbHandler db = new WikisHistoryDbHandler(getActivity());
                    db.addItem(new WikiPreviousListItem(label, url));
                    updateRecords();

                    APIEndpoints.WIKI_NAME = url;
                    setUrlAsPreference(APIEndpoints.WIKI_NAME, label);
                    APIEndpoints.reInitEndpoints();
                    MainActivity.account.setTitle(mHelper.stripUpWikiUrl(url));
                    MainActivity.account.setSubTitle(APIEndpoints.WIKI_NAME);
                    ((MaterialNavigationDrawer) getActivity()).notifyAccountDataChanged();

                    updateRecords();
                }
            }
        });


        previouslyUsedList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                String url = mWikisList.get(position).getUrl().toLowerCase().trim();

                if (position < mWikisList.size() && !url.equals(APIEndpoints.WIKI_NAME.toLowerCase().trim()))
                    previouslyUsedList.setItemChecked(position, !mAdapter.isPositionChecked(position));

                return true;
            }
        });

        previouslyUsedList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            private int nr = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                String url = mWikisList.get(position).getUrl().toLowerCase().trim();
                if (mWikisList != null && position < mWikisList.size() && !url.equals(APIEndpoints.WIKI_NAME.toLowerCase().trim())) {
                    if (checked) {
                        nr++;
                        mAdapter.setNewSelection(position, checked);
                    } else {
                        nr--;
                        mAdapter.removeSelection(position);
                        if (mAdapter.getSelectionSize() <= 0)
                            mode.finish();
                    }
                    mode.setTitle("Zaznaczone: " + nr);
                } else {
                    if (mAdapter.getSelectionSize() <= 0)
                        mode.finish();
                }
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
                        WikisHistoryDbHandler db = new WikisHistoryDbHandler(getActivity());
                        List<WikiPreviousListItem> mSelected = mAdapter.getSelectedItems();
                        for (WikiPreviousListItem wikiItem : mSelected) {
                            db.deleteItem(wikiItem.getId());
                        }
                        db.close();
                        mWikisList.removeAll(mSelected);
                        nr = 0;
                        mAdapter.clearSelection();
                        mode.finish();
                        updateRecords();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mAdapter.clearSelection();
                mActionMode = null;
            }
        });

        previouslyUsedList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        previouslyUsedList.setAdapter(mAdapter);

        updateRecords();
        reInitViews();

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        updateRecords();
        setUpColorScheme();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHelper.closeDbs();
    }

    private static void reInitViews() {
        if (mWikisList == null || mWikisList.size() <= 0) {
            previouslyUsedList.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            previouslyUsedList.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
    }

    public static void updateRecords() {
        WikisHistoryDbHandler db = new WikisHistoryDbHandler(MyApplication.getAppContext());
        mWikisList.clear();
        mWikisList.addAll(db.getAllItems());
        mAdapter.notifyDataSetChanged();
        reInitViews();
    }

    public static void refreshList(){
        mAdapter.notifyDataSetChanged();
    }

    public static void setUpNightMode() {
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.nightBackground));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));

        updateRecords();

        mAdapter.notifyDataSetChanged();
    }

    public static void setUpNormalMode() {
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.background));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));

        updateRecords();

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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previuslyUsed_addWikiBtn:
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
                                updateRecords();

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
        }
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

}
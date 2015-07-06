package andrzej.example.com.fragments.ManagementTabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.adapters.OnLongItemClickListener;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.fragments.ManagementTabs.adapters.FavoritesAdapter;
import andrzej.example.com.fragments.ManagementTabs.adapters.WikiHistoryAdapter;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnItemClickListener;
import andrzej.example.com.utils.WikiManagementHelper;
import andrzej.example.com.views.MaterialEditText;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by andrzej on 24.06.15.
 */
public class PreviouslyUsedWikisFragment extends Fragment implements View.OnClickListener {

    //UI Elements
    private static RelativeLayout rootView;
    private static LinearLayout errorLayout;
    private static TextView errorMessage;
    private static RecyclerView mRecyclerView;
    private BootstrapButton addWikiButton;
    public static ActionMode mActionMode;

    //Adapters
    private static WikiHistoryAdapter mAdapter;

    //Utils
    private static WikiManagementHelper mHelper;

    //List
    public static ArrayList<WikiPreviousListItem> mWikisList = new ArrayList<>();

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
        mAdapter = new WikiHistoryAdapter(getActivity(), mWikisList);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.previuslyUsed_recyclerView);
        rootView = (RelativeLayout) v.findViewById(R.id.previuslyUsed_rootView);
        errorLayout = (LinearLayout) v.findViewById(R.id.previuslyUsed_errorLayout);
        errorMessage = (TextView) v.findViewById(R.id.previuslyUsed_errorMsg);

        addWikiButton = (BootstrapButton) v.findViewById(R.id.previuslyUsed_addWikiBtn);

        //Listeners
        addWikiButton.setOnClickListener(this);

        //SetUpRecycler
        //Set up recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getActivity().getResources().getColor(R.color.divider))
                .sizeResId(R.dimen.divider)
                .showLastDivider()
                .build());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                WikiPreviousListItem item = mWikisList.get(position);

                String itemLabel = item.getTitle();
                String itemUrl = mHelper.cleanInputUrl(item.getUrl());

                switch (view.getId()) {
                    case R.id.btnFav:

                        if (mHelper.doesFavItemExsistsUrl(itemUrl))
                            mHelper.removeFavByUrl(itemUrl);
                        else
                            mHelper.addWikiToFavs(itemLabel, itemUrl);

                        FavouriteWikisFragment.updateDataset();
                        mAdapter.notifyItemChanged(position);
                        break;

                    case R.id.rlRootView:

                        if (!APIEndpoints.WIKI_NAME.equals(itemUrl)) {
                            APIEndpoints.WIKI_NAME = itemUrl;
                            setUrlAsPreference(APIEndpoints.WIKI_NAME, itemLabel);
                            APIEndpoints.reInitEndpoints();

                            if (itemLabel != null && itemLabel.trim().length() > 0)
                                MainActivity.account.setTitle(itemLabel);
                            else
                                MainActivity.account.setTitle(mHelper.stripUpWikiUrl(APIEndpoints.WIKI_NAME));

                            MainActivity.account.setSubTitle(APIEndpoints.WIKI_NAME);
                            ((MaterialNavigationDrawer) getActivity()).notifyAccountDataChanged();

                            updateRecords();
                            FavouriteWikisFragment.updateDataset();
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.wiki_succesfully_changed), Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
            }
        });

        mAdapter.setOnLongItemClickListener(new OnLongItemClickListener() {
            @Override
            public void onLongItemClick(View view, final int position) {
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.edit_wiki)
                        .items(R.array.fav_dialog_items)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch (which) {
                                    case 0: //Edycja

                                        boolean wrapInScrollView = true;
                                        boolean currentWikiEdited = false;

                                        final MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                                                .title(R.string.edit_wiki)
                                                .customView(R.layout.dialog_edit_wiki, wrapInScrollView)
                                                .autoDismiss(false)
                                                .negativeText(getActivity().getResources().getString(R.string.back))
                                                .positiveText(getActivity().getResources().getString(R.string.ok))
                                                .build();

                                        View layoutView = materialDialog.getCustomView();

                                        final MaterialEditText labelInput = (MaterialEditText) layoutView.findViewById(R.id.mangementInputLabelET);
                                        final MaterialEditText urlInput = (MaterialEditText) layoutView.findViewById(R.id.mangementInputUrlET);

                                        final String oldUrl = mWikisList.get(position).getUrl();
                                        labelInput.setText(mWikisList.get(position).getTitle());
                                        urlInput.setText(oldUrl);

                                        if (mWikisList.get(position).getUrl().equals(APIEndpoints.WIKI_NAME))
                                            currentWikiEdited = true;
                                        else
                                            currentWikiEdited = false;

                                        final boolean finalCurrentWikiEdited = currentWikiEdited;

                                        materialDialog.getBuilder().callback(new MaterialDialog.ButtonCallback() {
                                            @Override
                                            public void onPositive(MaterialDialog dialog) {
                                                super.onPositive(dialog);

                                                String url_input = mHelper.cleanInputUrl(urlInput.getText().toString().trim());
                                                String label_input = labelInput.getText().toString().trim();

                                                if (url_input == null || url_input.trim().length() <= 0) {
                                                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.url_required), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    mHelper.editFavAndPrevItem(mWikisList.get(position).getId(), label_input, url_input, oldUrl);

                                                    if (finalCurrentWikiEdited) {
                                                        APIEndpoints.WIKI_NAME = mHelper.cleanInputUrl(url_input);
                                                        //setUrlAsPreference(APIEndpoints.WIKI_NAME, label_input);
                                                        APIEndpoints.reInitEndpoints();

                                                        if (label_input == null || label_input.trim().length() <= 0)
                                                            label_input = mHelper.stripUpWikiUrl(url_input);

                                                        MainActivity.account.setTitle(label_input);
                                                        MainActivity.account.setSubTitle(APIEndpoints.WIKI_NAME);

                                                        mHelper.setUrlAsPreference(url_input, label_input);

                                                        ((MaterialNavigationDrawer) getActivity()).notifyAccountDataChanged();
                                                    }

                                                    PreviouslyUsedWikisFragment.updateRecords();
                                                    FavouriteWikisFragment.updateDataset();

                                                    materialDialog.dismiss();

                                                }
                                            }

                                            @Override
                                            public void onNegative(MaterialDialog dialog) {
                                                super.onNegative(dialog);
                                                materialDialog.dismiss();
                                            }
                                        });

                                        materialDialog.show();

                                        break;
                                    case 1: //Usuń
                                        int id = mWikisList.get(position).getId();
                                        mHelper.removeWikiFromAll(id);

                                        if(mHelper.doesFavItemExsistsUrl(mWikisList.get(position).getUrl()))
                                            mHelper.removeFavByUrl(mWikisList.get(position).getUrl());

                                        mWikisList.remove(position);
                                        mAdapter.notifyItemRemoved(position);
                                        reInitViews();
                                        FavouriteWikisFragment.updateDataset();
                                        break;
                                }
                            }
                        })
                        .negativeText(R.string.back)
                        .build();

                dialog.show();
            }
        });


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
            mRecyclerView.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
    }

    public static void updateRecords() {
        mWikisList.clear();
        mWikisList.addAll(mHelper.getAllWikis());
        mAdapter.notifyDataSetChanged();
        reInitViews();
    }

    public static void refreshList() {
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

                                                     String label = label_input;
                                                     if (label == null || label.trim().length() <= 0)
                                                         label = mHelper.stripUpWikiUrl(url_input);

                                                     if (url_input.length() <= 0)
                                                         Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.url_required), Toast.LENGTH_SHORT).show();
                                                     else if (mHelper.doesItemFavExsists(label, url_input)) {
                                                         Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.already_fav_exsists), Toast.LENGTH_SHORT).show();
                                                     } else {

                                                         if (!mHelper.itemPrevExsists(label, url_input)) {

                                                             if (mHelper.doesFavItemExsistsUrl(url_input)) {
                                                                 WikiFavItem item = mHelper.getItemByLabel(url_input);
                                                                 if (item != null)
                                                                     label_input = item.getTitle();
                                                             }

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

                                                             FavouriteWikisFragment.updateDataset();

                                                         }

                                                     }
                                                 }

                                                 @Override
                                                 public void onNegative(MaterialDialog dialog) {
                                                     super.onNegative(dialog);
                                                     dialog.dismiss();
                                                 }
                                             }

                );

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
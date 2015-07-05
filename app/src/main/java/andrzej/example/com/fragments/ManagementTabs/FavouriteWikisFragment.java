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
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.adapters.OnLongItemClickListener;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.fragments.ManagementTabs.adapters.FavoritesAdapter;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnItemClickListener;
import andrzej.example.com.utils.WikiManagementHelper;
import andrzej.example.com.views.DividerItemDecoration;
import andrzej.example.com.views.MaterialEditText;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;

/**
 * Created by andrzej on 02.07.15.
 */
public class FavouriteWikisFragment extends Fragment {

    //UI Elements
    private static RelativeLayout rootView;
    private static RelativeLayout contentView;
    private static LinearLayout errorLayout;
    private RecyclerView recyclerView;
    private static TextView errorMessage;

    //Utils
    private static WikiManagementHelper mHelper;

    //List
    private static List<WikiFavItem> mFavs = new ArrayList<>();

    //Adapter
    public static FavoritesAdapter mAdapter;

    //Material Dialog list
    String[] materialDialogItems = {"Edycja", "Usuń"};

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
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getActivity().getResources().getColor(R.color.divider))
                .sizeResId(R.dimen.divider)
                .showLastDivider()
                .build());
        mAdapter = new FavoritesAdapter(getActivity(), mFavs);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //Listeners
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                switch (view.getId()) {

                    case R.id.rlRootView:
                        String url = mFavs.get(position).getUrl();
                        String label = mFavs.get(position).getTitle();

                        if (!APIEndpoints.WIKI_NAME.equals(url)) {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.wiki_succesfully_changed), Toast.LENGTH_SHORT).show();
                        }

                        mHelper.setCurrentWiki(label, url);

                        break;

                    case R.id.btnRemove:
                        mHelper.removeFav(mFavs.get(position).getId());
                        mFavs.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        reInitViews();
                        PreviouslyUsedWikisFragment.refreshList();
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
                                                .title(getActivity().getResources().getString(R.string.wiki_man_input_title))
                                                .customView(R.layout.dialog_edit_wiki, wrapInScrollView)
                                                .autoDismiss(false)
                                                .negativeText(getActivity().getResources().getString(R.string.back))
                                                .positiveText(getActivity().getResources().getString(R.string.ok)).build();

                                        View layoutView = materialDialog.getCustomView();

                                        final MaterialEditText labelInput = (MaterialEditText) layoutView.findViewById(R.id.mangementInputLabelET);
                                        final MaterialEditText urlInput = (MaterialEditText) layoutView.findViewById(R.id.mangementInputUrlET);

                                        final String oldUrl = mFavs.get(position).getUrl();
                                        labelInput.setText(mFavs.get(position).getTitle());
                                        urlInput.setText(oldUrl);

                                        if (mFavs.get(position).getUrl().equals(APIEndpoints.WIKI_NAME))
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

                                                    mHelper.editPrevAndFavItem(mFavs.get(position).getId(), label_input, url_input, oldUrl);

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
                                        mHelper.removeFav(mFavs.get(position).getId());
                                        mFavs.remove(position);
                                        mAdapter.notifyItemRemoved(position);
                                        reInitViews();
                                        PreviouslyUsedWikisFragment.refreshList();
                                        break;
                                }
                            }
                        })
                        .negativeText(R.string.back)
                        .build();

                dialog.show();
            }
        });

        updateDataset();

        return v;
    }


    public static void updateDataset() {
        mFavs.clear();
        mFavs.addAll(mHelper.getAllFavs());
        mAdapter.notifyDataSetChanged();
        reInitViews();
    }

    private static void reInitViews() {
        if (mFavs.size() <= 0) {
            errorLayout.setVisibility(View.VISIBLE);
            errorMessage.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.GONE);
            errorMessage.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
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

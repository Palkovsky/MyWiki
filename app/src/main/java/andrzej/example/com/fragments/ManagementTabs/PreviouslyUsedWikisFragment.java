package andrzej.example.com.fragments.ManagementTabs;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import andrzej.example.com.fragments.WikisManagementFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.WikiListItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.StringOperations;
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

    //List
    private static ArrayList<WikiListItem> mWikisList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_previously_used_wikis, container, false);

        mAdapter = new PreviouslyUsedListAdapter(getActivity(), mWikisList);

        rootView = (RelativeLayout) v.findViewById(R.id.previuslyUsed_rootView);
        errorLayout = (LinearLayout) v.findViewById(R.id.previuslyUsed_errorLayout);
        errorMessage = (TextView) v.findViewById(R.id.previuslyUsed_errorMsg);
        previouslyUsedList = (ListView) v.findViewById(R.id.previuslyUsed_list);
        addWikiButton = (BootstrapButton) v.findViewById(R.id.previuslyUsed_addWikiBtn);

        //Listeners
        addWikiButton.setOnClickListener(this);

        previouslyUsedList.setAdapter(mAdapter);

        reInitViews();

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        setUpColorScheme();
    }

    private static void reInitViews(){
        if(mWikisList==null || mWikisList.size()<=0){
            previouslyUsedList.setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
        }else{
            previouslyUsedList.setVisibility(View.VISIBLE);
            errorLayout.setVisibility(View.GONE);
        }
    }


    public static void setUpNightMode(){
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.nightBackground));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));


        List<WikiListItem> temp = new ArrayList<>();
        temp.addAll(mWikisList);
        mWikisList.clear();
        mWikisList.addAll(temp);
        temp.clear();

        reInitViews();
        mAdapter.notifyDataSetChanged();
    }

    public static void setUpNormalMode(){
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.background));
        errorMessage.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));


        List<WikiListItem> temp = new ArrayList<>();
        temp.addAll(mWikisList);
        mWikisList.clear();
        mWikisList.addAll(temp);
        temp.clear();

        reInitViews();
        mAdapter.notifyDataSetChanged();
    }



    public static void setUpColorScheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if(nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    public static void addItemToList(String label, String url){
        mWikisList.add(new WikiListItem(label, url));
        mAdapter.notifyDataSetChanged();
        reInitViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

                        if(url_input.length()<=0)
                            Toast.makeText(getActivity(), "Musisz podać URL", Toast.LENGTH_SHORT).show();
                        else {
                            if(!APIEndpoints.WIKI_NAME.equals(WikisManagementFragment.cleanInputUrl(url_input))) {
                                dialog.dismiss();
                                Toast.makeText(getActivity(), url_input, Toast.LENGTH_SHORT).show();
                                PreviouslyUsedWikisFragment.addItemToList(label_input, WikisManagementFragment.cleanInputUrl(url_input));
                                APIEndpoints.WIKI_NAME = WikisManagementFragment.cleanInputUrl(url_input);
                                APIEndpoints.reInitEndpoints();
                                MainActivity.account.setTitle(StringOperations.stripUpWikiUrl(APIEndpoints.WIKI_NAME));
                                MainActivity.account.setSubTitle(APIEndpoints.WIKI_NAME);
                                ((MaterialNavigationDrawer) getActivity()).notifyAccountDataChanged();
                            }else
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
}
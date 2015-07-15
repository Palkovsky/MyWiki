package andrzej.example.com.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SessionArticleHistory;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnBackPressedListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class OfflineArticleFragment extends Fragment implements OnBackPressedListener {

    //Ui
    private RelativeLayout mRootView;
    private TextView mTestTv;

    //Vitals
    private String articleContent = null;

    //Utils
    SharedPreferences prefs;

    public OfflineArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Bundle bundle = this.getArguments();
        articleContent = bundle.getString("article_content");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_offline_article, container, false);

        mRootView = (RelativeLayout) v.findViewById(R.id.offlineArticleRootView);
        mTestTv = (TextView) v.findViewById(R.id.offlineArticleTestTv);

        //Listeners
        ((MainActivity) getActivity()).setOnBackPressedListener(this);


        mTestTv.setText(articleContent);
        setUpColorScheme();

        return v;
    }


    private void setUpNightMode() {
        mRootView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        mTestTv.setTextColor(getActivity().getResources().getColor(R.color.nightFontColor));
    }

    private void setUpNormalMode() {
        mRootView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        mTestTv.setTextColor(getActivity().getResources().getColor(R.color.font_color));
    }

    private void setUpColorScheme() {
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    @Override
    public void doBack() {
        Fragment savedArticlesFragment = new SavedArticlesFragment();
        ((MaterialNavigationDrawer) getActivity()).setFragment(savedArticlesFragment, getActivity().getResources().getString(R.string.drawer_bookmared_articles));
        ((MaterialNavigationDrawer) getActivity()).setSection(MainActivity.section_bookmarks);
    }
}

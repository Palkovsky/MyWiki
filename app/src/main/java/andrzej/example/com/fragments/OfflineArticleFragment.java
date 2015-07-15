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
import android.widget.Toast;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnBackPressedListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class OfflineArticleFragment extends Fragment implements OnBackPressedListener {

    private static final String TAG = "offlinearticlefragmet";

    //Ui
    private RelativeLayout mRootView;
    private TextView mTestTv;

    //Vitals
    private int articleId = -1;
    private String articleTitle = "";
    private String articleContent = "";
    private String articleImage = "";
    private String wikiUrl = null;

    //Finals
    public static final String BUNDLE_KEY_ID = "article_id";
    public static final String BUNDLE_KEY_TITLE = "article_title";
    public static final String BUNDLE_KEY_CONTENT = "article_content";
    public static final String BUNDLE_KEY_WIKI_IMAGE = "article_image";
    public static final String BUNDLE_KEY_WIKI_URL = "wiki_url";

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
        articleId = bundle.getInt(BUNDLE_KEY_ID);
        articleTitle = bundle.getString(BUNDLE_KEY_TITLE);
        articleContent = bundle.getString(BUNDLE_KEY_CONTENT);
        articleImage = bundle.getString(BUNDLE_KEY_WIKI_IMAGE);
        wikiUrl = bundle.getString(BUNDLE_KEY_WIKI_URL);
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

        //Starting methods
        addToArticleHistory();
        mTestTv.setText(articleContent);
        setUpColorScheme();

        return v;
    }

    private void addToArticleHistory(){
        ArticleHistoryItem historyItem = new ArticleHistoryItem(articleId, System.currentTimeMillis(), articleTitle, articleImage, wikiUrl);
        ArticleHistoryDbHandler historyDbHandler = new ArticleHistoryDbHandler(getActivity());
        historyDbHandler.addItem(historyItem);
        historyDbHandler.close();
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

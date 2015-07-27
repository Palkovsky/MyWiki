package andrzej.example.com.fragments.MainTabs;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;


public class NewestArticlesFragment extends Fragment {

    static RelativeLayout mRootView;
    static TextView tv;

    public NewestArticlesFragment() {
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

        View v = inflater.inflate(R.layout.tab_newest_articles, container, false);

        mRootView = (RelativeLayout) v.findViewById(R.id.rootView);
        tv = (TextView) v.findViewById(R.id.tv);

        setUpColorScheme();

        return v;
    }

    private void setUpColorScheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    public static void setUpNightMode(){
        mRootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.nightBackground));
        tv.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
    }

    public static void setUpNormalMode(){
        mRootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.background));
        tv.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mRootView.getChildCount() > 0)
            mRootView.removeAllViews();
    }

}

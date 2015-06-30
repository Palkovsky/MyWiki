package andrzej.example.com.fragments.ManagementTabs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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

/**
 * Created by andrzej on 24.06.15.
 */
public class PreviouslyUsedWikisFragment extends Fragment {

    static RelativeLayout rootView;
    static TextView tv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_previously_used_wikis, container, false);

        rootView = (RelativeLayout) v.findViewById(R.id.previuslyUsed_rootView);
        tv = (TextView) v.findViewById(R.id.previuslyUsed_textView);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpColorScheme();
    }


    public static void setUpNightMode(){
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.nightBackground));
        tv.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
    }

    public static void setUpNormalMode(){
        rootView.setBackgroundColor(MyApplication.getAppContext().getResources().getColor(R.color.background));
        tv.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
    }



    public static void setUpColorScheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if(nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }
}
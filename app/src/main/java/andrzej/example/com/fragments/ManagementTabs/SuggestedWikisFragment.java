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

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;

/**
 * Created by andrzej on 24.06.15.
 */
public class SuggestedWikisFragment extends Fragment {

    RelativeLayout rootView;
    TextView tv;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_suggested_wikis, container, false);

        rootView = (RelativeLayout) v.findViewById(R.id.suggested_rootView);
        tv = (TextView) v.findViewById(R.id.suggested_textView);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpColorScheme();
    }


    private void setUpNightMode(){
        rootView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        tv.setTextColor(getActivity().getResources().getColor(R.color.nightFontColor));
    }

    private void setUpNormalMode(){
        rootView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        tv.setTextColor(getActivity().getResources().getColor(R.color.font_color));
    }



    private void setUpColorScheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if(nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }
}
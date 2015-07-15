package andrzej.example.com.fragments;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;


public class MainFragment extends Fragment {


    //UI Elements
    TextView tv;
    FrameLayout rootView;

    //Articles ids
    List<Integer> article_ids = new ArrayList<>();

    //Networking
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;

    SharedPreferences prefs;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        rootView = (FrameLayout) v.findViewById(R.id.main_rootView);
        tv = (TextView) v.findViewById(R.id.main_textView);

        return v;
    }


    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpColorScheme();
    }

    private void setUpColorScheme() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode)
            setUpNightMode();
        else
            setUpNormalMode();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_fragment_menu, menu);

        MenuItem item = menu.findItem(R.id.menu_nightMode);
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);
        item.setChecked(nightMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
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
        return super.onOptionsItemSelected(item);
    }

    private void setUpNightMode() {
        rootView.setBackgroundColor(getActivity().getResources().getColor(R.color.nightBackground));
        tv.setTextColor(getActivity().getResources().getColor(R.color.nightFontColor));
    }

    private void setUpNormalMode() {
        rootView.setBackgroundColor(getActivity().getResources().getColor(R.color.background));
        tv.setTextColor(getActivity().getResources().getColor(R.color.font_color));
    }
}

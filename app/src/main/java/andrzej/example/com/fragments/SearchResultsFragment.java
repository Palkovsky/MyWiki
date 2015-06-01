package andrzej.example.com.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import andrzej.example.com.mlpwiki.R;

/**
 * Created by andrzej on 01.06.15.
 */
public class SearchResultsFragment extends Fragment {

    TextView tv;
    String query;

    public SearchResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_result, container, false);

        tv = (TextView) v.findViewById(R.id.textview);

        Bundle args = getArguments();
        query = args.getString("query");
        tv.setText(query);

        return v;
    }


}
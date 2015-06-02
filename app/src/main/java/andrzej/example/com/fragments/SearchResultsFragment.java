package andrzej.example.com.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.beardedhen.androidbootstrap.BootstrapButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import andrzej.example.com.adapters.ResultListAdapter;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.recycleritems.Article;


public class SearchResultsFragment extends Fragment {

    // UI Elements
    private ListView results_listview;
    private LinearLayout noInternetLl;
    private BootstrapButton noInternetButton;
    private ProgressBar fetchingProgressBar;

    //Adapter
    BaseAdapter mListAdapter;


    String query, query_url;

    //Lists
    private ArrayList<Article> results;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;


    public SearchResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        query = args.getString("query");
        query_url = APIEndpoints.getUrlSearch(query, BaseConfig.searchLimit);

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        results = new ArrayList<Article>();
        mListAdapter = new ResultListAdapter(getActivity(), results);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_result, container, false);
        fetchingProgressBar = (ProgressBar) v.findViewById(R.id.loadingPb);
        results_listview = (ListView) v.findViewById(R.id.result_listview);


        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            results_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity(), String.valueOf(results.get(position).getId()), Toast.LENGTH_SHORT).show();
                }
            });

            results_listview.setAdapter(mListAdapter);

            sendJsonSearchRequest();

        } else {
            fetchingProgressBar.setVisibility(View.GONE);
            noInternetLl = (LinearLayout) v.findViewById(R.id.noInternetLl);

            noInternetButton = (BootstrapButton) v.findViewById(R.id.noInternetBtn);
            noInternetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "nic", Toast.LENGTH_SHORT).show();
                }
            });

            noInternetLl.setVisibility(View.VISIBLE);
        }


        return v;
    }


    private void parseJsonResponse(JSONObject response) {

        if (response == null || response.length() == 0) {
            return;
        }

        try {
            JSONArray items = response.getJSONArray(Article.KEY_ITEMS);

            for (int i = 0; i < items.length(); i++) {
                JSONObject searchItem = items.getJSONObject(i);

                int id = searchItem.getInt(Article.KEY_ID);
                String title = searchItem.getString(Article.KEY_TITLE);

                if (!stringContainsItemFromList(title, APIEndpoints.STOP_WORDS)) {
                    results.add(new Article(id, title));
                    mListAdapter.notifyDataSetChanged();
                }

                if(results.size()>0)
                    fetchingProgressBar.setVisibility(View.GONE);

            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendJsonSearchRequest() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, query_url, (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parseJsonResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(null, error.getMessage());
            }
        });

        requestQueue.add(request);
    }

    public static boolean stringContainsItemFromList(String inputString, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (inputString.contains(items[i])) {
                return true;
            }
        }
        return false;
    }


}
package andrzej.example.com.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.network.VolleySingleton;


public class MainFragment extends Fragment {

    //Articles ids
    List<Integer> article_ids = new ArrayList<>();

    //Networking
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;

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


        return v;
    }


    /* Last edited
    private void fetchIds(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getLastEdited(BaseConfig.lastEditedLimit), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("items");
                            for(int i = 0; i<items.length(); i++){
                                JSONObject item = items.getJSONObject(i);
                                article_ids.add(item.getInt("article"));
                            }

                            if(article_ids.size()>0)
                                fetchArticles(convertIntegers(article_ids));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);
    }

    private void fetchArticles(int[] ids){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemDetalis(ids), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(null, response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);
    }

*/


    public static int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }

}

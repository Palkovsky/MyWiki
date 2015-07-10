package andrzej.example.com.researchapi;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.ArticleHeader;
import andrzej.example.com.models.Recommendation;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;

/**
 * Created by andrzej on 08.07.15.
 */
public class RequestHandler {
    private Context context;

    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    public RequestHandler(Context context) {
        this.context = context;
        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();
    }



    public void sendWikiInfo(final String label, final String url) {

        JSONObject wikiJSON = APIStatisticalEndpoints.prepareWikiJSONobject(label, url);

        //Send JSON request to Rails API
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                APIStatisticalEndpoints.listPostCreateEndpoint(), wikiJSON,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(null, response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(null, "ERROR: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Token token=" + APIStatisticalEndpoints.getAPIkey());
                return headers;
            }

        };

        requestQueue.add(jsonObjReq);

    }

    private Context getContext() {
        return context;
    }
}

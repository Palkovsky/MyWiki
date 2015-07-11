package andrzej.example.com.researchapi;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import andrzej.example.com.mlpwiki.MyApplication;

/**
 * Created by andrzej on 08.07.15.
 */
public class APIStatisticalEndpoints {

    //Development API KEY
    private static final String API_KEY = "f2a31be10d7a3d4e722fdb9d96990c4f";

    //Symbols
    private static String URL_CHAR_QUESTION = "?";
    private static String URL_CHAR_AMEPERSAND = "&";


    private static final String baseUrl = "http://10.0.2.2:3000/api/v1/";
    private static final String wikisScope = "wikis";

    //Endpoints
    private static final String listEndpoint = baseUrl + wikisScope;

    //Params
    private static final String URL_FILTER = "filter=";
    private static final String URL_PAGE = "page=";
    private static final String URL_LIMIT = "limit=";

    //JSON PARAMS
    private static final String JSON_WIKI_SCOPE = "wiki";
    public static final String JSON_URL_SCOPE = "url";
    public static final String JSON_LABEL_SCOPE = "title";

    //Filters
    public static final String RANDOM_FILTER = "rand";
    public static final String POPULARITY_FILTER = "popularity";

    //Settings
    private static final int MAX_FETCH_VALUE = 20;


    public static JSONObject prepareWikiJSONobject(String label, String url){
        JSONObject wikiJSON = new JSONObject();
        JSONObject jo = new JSONObject();

        try {

            jo.put(JSON_LABEL_SCOPE, label);
            jo.put(JSON_URL_SCOPE, url);
            wikiJSON.put(JSON_WIKI_SCOPE, jo);

            return wikiJSON;
        } catch (JSONException e) {
            Log.e(null, e.getMessage());
        }

        return null;
    }

    public static String getAPIkey(){
        return API_KEY;
    }

    public static String listPostCreateEndpoint(){
        return listEndpoint;
    }

    public static String listGetEndpoint(int page, String filter){

        if(filter==null)
            filter = "";

        return  listEndpoint +
                URL_CHAR_QUESTION +
                URL_PAGE + page +
                URL_CHAR_AMEPERSAND +
                URL_LIMIT + MAX_FETCH_VALUE +
                URL_CHAR_AMEPERSAND +
                URL_FILTER + filter;
    }
}

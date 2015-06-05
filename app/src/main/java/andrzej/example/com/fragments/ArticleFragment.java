package andrzej.example.com.fragments;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.ArticleImage;
import andrzej.example.com.models.SearchResult;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;


public class ArticleFragment extends Fragment {

    //UI
    FrameLayout rootView;
    ImageView parallaxIv;
    ParallaxScrollView parallaxSv;

    private int article_id;

    // Lists
    private List<ArticleImage> imgs = new ArrayList<ArticleImage>();

    //Networking
    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;

    public ArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        article_id = bundle.getInt("article_id", -1);

        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();
        imageLoader = volleySingleton.getImageLoader();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_article, container, false);

        rootView = (FrameLayout) v.findViewById(R.id.rootView);
        parallaxSv = (ParallaxScrollView) v.findViewById(R.id.parallaxSv);
        parallaxIv = (ImageView) v.findViewById(R.id.parallaxIv);


        setImageViewBackground(parallaxIv, getResources().getDrawable(R.drawable.logo));

        fetchArticleInfo(article_id);

        return v;
    }

    private void setImageViewBackground(ImageView imageView, Drawable drawable){

        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(drawable);
        }
        else{
            imageView.setBackgroundDrawable(drawable);
        }
    }

    private void fetchArticleContent(int id){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemContent(id), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray sections = response.getJSONArray(Article.KEY_SECTIONS);

                            for(int i = 0; i<sections.length(); i++){
                                JSONObject section = sections.getJSONObject(i);

                                JSONArray images_section = section.getJSONArray(ArticleImage.KEY_IMAGES);

                                for(int j = 0; j<images_section.length(); j++){
                                    JSONObject image = images_section.getJSONObject(j);

                                    String img_url = image.getString(ArticleImage.KEY_SRC);
                                    String caption = image.getString(ArticleImage.KEY_CAPTION);

                                    if(img_url!=null && img_url.trim().length()>0)
                                        imgs.add(new ArticleImage(img_url, caption));
                                }

                            }

                            if(imgs.size()>0)
                                Picasso.with(MyApplication.getAppContext()).load(imgs.get(0).getImg_url()).into(parallaxIv);

                            for(ArticleImage item : imgs){
                                Log.e(null, item.getImg_url());
                                if(item.getLabel()!=null)
                                    Log.e(null, item.getLabel());
                            }

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

    private void fetchArticleInfo(final int id) {
        int[] array = {id};
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemDetalis(array), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject item = response.getJSONObject(SearchResult.KEY_ITEMS).getJSONObject(String.valueOf(id));
                            String thumbnail_url = item.getString(Article.KEY_THUMBNAIL);

                            if (thumbnail_url != null && thumbnail_url.trim().length() > 0) {
                                JSONObject orginal_dimens = item.getJSONObject(ArticleImage.KEY_ORGINAL_DIMENS);
                                int orginal_width = orginal_dimens.getInt(ArticleImage.KEY_WIDTH);

                                thumbnail_url = pumpUpResolution(orginal_width, thumbnail_url);

                                imgs.add(new ArticleImage(thumbnail_url));
                            }

                            fetchArticleContent(id);

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

    private String pumpUpResolution(int width, String thumbnail_url){
        String chunk_string_beg = "/window-crop/width/";
        String chunk_string_end = "/x-offset/";

        int index_beg = thumbnail_url.indexOf("/window-crop/width/") + chunk_string_beg.length();
        int index_end = thumbnail_url.indexOf(chunk_string_end);

        String width_substring =  thumbnail_url.substring(index_beg, index_end);

        return thumbnail_url.replaceFirst(width_substring, String.valueOf(width));
    }

}

package andrzej.example.com.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.models.ArticleImage;
import andrzej.example.com.models.ArticleSection;
import andrzej.example.com.models.SearchResult;
import andrzej.example.com.network.NetworkUtils;
import andrzej.example.com.network.VolleySingleton;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.utils.StringOperations;


public class ArticleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //UI
    ImageView parallaxIv;
    TextView titleTv;
    ParallaxScrollView parallaxSv;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout noInternetLl;
    LinearLayout rootArticleLl;
    BootstrapButton retryBtn;

    private int article_id;
    String article_title;

    // Lists
    private List<ArticleImage> imgs = new ArrayList<ArticleImage>();
    private List<ArticleSection> sections = new ArrayList<>();

    //Networking
    private VolleySingleton volleySingleton;
    private RequestQueue requestQueue;

    ArticleHistoryDbHandler db;


    public ArticleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        article_id = bundle.getInt("article_id", -1);
        article_title = bundle.getString("article_title");


        volleySingleton = VolleySingleton.getsInstance();
        requestQueue = volleySingleton.getRequestQueue();

        db = new ArticleHistoryDbHandler(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_article, container, false);

        parallaxSv = (ParallaxScrollView) v.findViewById(R.id.parallaxSv);
        parallaxIv = (ImageView) v.findViewById(R.id.parallaxIv);
        titleTv = (TextView) v.findViewById(R.id.titleTv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.article_swipe_refresh_layout);
        noInternetLl = (LinearLayout) v.findViewById(R.id.noInternetLl);
        rootArticleLl = (LinearLayout) v.findViewById(R.id.rootArticle);
        retryBtn = (BootstrapButton) v.findViewById(R.id.noInternetBtn);

        addTextViewToLayout(rootArticleLl, "<p><b>Derpy</b> - no, z Derpy to jak z Derpy. Miała kupić pomidory, a przyniosła i kupiła kilo wiśni. Zawsze była taka fiu!</p>");
        addImageViewToLayout(rootArticleLl, StringOperations.pumpUpSize("http://vignette1.wikia.nocookie.net/mlp/images/b/b3/Derpy_Hooves_szkic.jpg/revision/latest/scale-to-width/150?cb=20130305192012&path-prefix=pl", 600), "Szkic Derpy");
        addTextViewToLayout(rootArticleLl, "<br/>");
        addImageViewToLayout(rootArticleLl, StringOperations.pumpUpSize("http://vignette4.wikia.nocookie.net/mlp/images/e/e9/Dreppy_x666.png/revision/latest?cb=20130715174906&path-prefix=pl", 600), "Mała Derpy");
        addTextViewToLayout(rootArticleLl, "<h2>Charakterystyka</h2>");
        addTextViewToLayout(rootArticleLl, "<p>Derpy pojawiła się już w pierwszym odcinku z celowym błędem animatorów (tzw. easter eggiem), polegającym na desynchronizacji kierunku patrzenia oczu bohaterki, czego skutkiem był charakterystyczny zez. Pomimo, że pegaz pojawiał się tylko w tle, fani bardzo szybko ją zauważyli i polubili. Ostatecznie zadecydowano, by błędu nie korygować (choć niekiedy Derpy ma prawidłowo zsynchronizowane oczy). Znaczek Derpy opiera się na kucyku generacji pierwszej - Bubbles</p>");
        addTextViewToLayout(rootArticleLl, "<h4>Cechy</h4>");
        addTextViewToLayout(rootArticleLl, "<p>&#8226;Indywidualista<br/>&#8226;Pomysłowy<br/>&#8226;Konformista</p>");
        addTextViewToLayout(rootArticleLl, "<h4>Zabawki</h4>");
        addImageViewToLayout(rootArticleLl, StringOperations.pumpUpSize("http://vignette1.wikia.nocookie.net/mlp/images/d/d3/Derpy%21.jpg/revision/latest/scale-to-width/180?cb=20130806190710&path-prefix=pl", 600), "Duża figurka promocyjna, a obok - opakowanie z Derpy do ozdabiania.");

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(getActivity())) {
                    mSwipeRefreshLayout.setEnabled(true);
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(true);
                            fetchArticleInfo(article_id);
                            fetchArticleInfo(article_id);
                        }
                    });
                } else
                    Toast.makeText(getActivity(), getResources().getString(R.string.no_internet_conn), Toast.LENGTH_SHORT).show();
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);

        titleTv.setText(article_title);
        setImageViewBackground(parallaxIv, getResources().getDrawable(R.drawable.logo));

        if (NetworkUtils.isNetworkAvailable(getActivity()))
            fetchArticleInfo(article_id);
        else
            setNoInternetLayout();

        return v;
    }


    private void fetchArticleContent(int id) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemContent(id), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (NetworkUtils.isNetworkAvailable(getActivity()))
                                setInternetPresentLayout();
                            else
                                setNoInternetLayout();
                            JSONArray sections = response.getJSONArray(Article.KEY_SECTIONS);

                            for (int i = 0; i < sections.length(); i++) {
                                JSONObject section = sections.getJSONObject(i);

                                JSONArray images_section = section.getJSONArray(ArticleImage.KEY_IMAGES);

                                for (int j = 0; j < images_section.length(); j++) {
                                    JSONObject image = images_section.getJSONObject(j);

                                    String img_url = image.getString(ArticleImage.KEY_SRC);
                                    String caption = null;
                                    if (image.has(ArticleImage.KEY_CAPTION))
                                        image.getString(ArticleImage.KEY_CAPTION);

                                    if (img_url != null && img_url.trim().length() > 0)
                                        imgs.add(new ArticleImage(img_url, caption));
                                }

                            }

                            if (imgs.size() > 0) {
                                Picasso.with(MyApplication.getAppContext()).load(imgs.get(0).getImg_url()).into(parallaxIv, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        parallaxIv.setBackgroundColor(Color.WHITE);

                                        ArticleHistoryItem item = new ArticleHistoryItem(article_id, System.currentTimeMillis(), article_title, imgs.get(0).getImg_url());
                                        db.addItem(item);
                                    }

                                    @Override
                                    public void onError() {

                                    }
                                });
                            } else {
                                ArticleHistoryItem item = new ArticleHistoryItem(article_id, System.currentTimeMillis(), article_title, null);
                                db.addItem(item);
                            }

                            mSwipeRefreshLayout.setRefreshing(false);

                            for (ArticleImage item : imgs) {
                                Log.e(null, item.getImg_url());
                                if (item.getLabel() != null)
                                    Log.e(null, item.getLabel());
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (!NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                    setNoInternetLayout();
            }
        });

        requestQueue.add(request);
    }

    @Override
    public void onPause() {
        super.onPause();
        db.close();
    }

    private void fetchArticleInfo(final int id) {
        int[] array = {id};
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APIEndpoints.getUrlItemDetalis(array), (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (NetworkUtils.isNetworkAvailable(getActivity()))
                                setInternetPresentLayout();
                            else
                                setNoInternetLayout();
                            JSONObject item = response.getJSONObject(SearchResult.KEY_ITEMS).getJSONObject(String.valueOf(id));
                            String thumbnail_url = item.getString(Article.KEY_THUMBNAIL);

                            if (thumbnail_url != null && thumbnail_url.trim().length() > 0 && !thumbnail_url.isEmpty()) {
                                JSONObject orginal_dimens = item.getJSONObject(ArticleImage.KEY_ORGINAL_DIMENS);
                                int orginal_width = orginal_dimens.getInt(ArticleImage.KEY_WIDTH);

                                thumbnail_url = pumpUpResolution(orginal_width, thumbnail_url);

                                imgs.add(new ArticleImage(thumbnail_url));
                            }

                            fetchArticleContent(id);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                fetchArticleContent(id);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (!NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                    setNoInternetLayout();
                else
                    fetchArticleContent(id);
            }
        });

        requestQueue.add(request);
    }

    private String pumpUpResolution(int width, String thumbnail_url) {
        String chunk_string_beg = "/window-crop/width/";
        String chunk_string_end = "/x-offset/";

        int index_beg = thumbnail_url.indexOf("/window-crop/width/") + chunk_string_beg.length();
        int index_end = thumbnail_url.indexOf(chunk_string_end);

        String width_substring = thumbnail_url.substring(index_beg, index_end);

        return thumbnail_url.replaceFirst(width_substring, String.valueOf(width));
    }

    //Adding Views
    private void addTextViewToLayout(LinearLayout ll, String data) {
        TextView itemTv = new TextView(getActivity());
        itemTv.setTypeface(null, Typeface.NORMAL);
        itemTv.setText(Html.fromHtml(data));
        itemTv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.addView(itemTv);
    }

    private void addImageViewToLayout(LinearLayout ll, String img_url, String caption) {


        LinearLayout imageLl = new LinearLayout(getActivity());
        imageLl.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageLl.setGravity(Gravity.CENTER_HORIZONTAL);
        imageLl.setLayoutParams(LLParams);


        //ImageView Setup
        ImageView imageView = new ImageView(getActivity());
        //imageView.setAdjustViewBounds(true);
        //setting image resource
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(getActivity()).load(img_url).placeholder(getResources().getDrawable(R.drawable.ic_action_picture)).error(getResources().getDrawable(R.drawable.ic_action_picture)).into(imageView);

        LinearLayout.LayoutParams params_iv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);


        //setting image position
        imageView.setId(R.id.imageViewProgramatically);
        imageView.setLayoutParams(params_iv);

        imageLl.addView(imageView);

        if (caption != null && caption.length() > 0) {
            TextView itemTv = new TextView(getActivity());
            itemTv.setTypeface(null, Typeface.ITALIC);
            itemTv.setText(caption);

            LinearLayout.LayoutParams params_tv = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            itemTv.setGravity(Gravity.CENTER_HORIZONTAL);

            itemTv.setLayoutParams(params_tv);

            imageLl.addView(itemTv);
        }

        ll.addView(imageLl);

        /*
        // Creating a new RelativeLayout
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());

        // Defining the RelativeLayout layout parameters.
        // In this case I want to fill its parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        relativeLayout.setLayoutParams(rlp);

        //ImageView Setup
        ImageView imageView = new ImageView(getActivity());
        //imageView.setAdjustViewBounds(true);
        //setting image resource
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(getActivity()).load(img_url).into(imageView);

        RelativeLayout.LayoutParams params_iv = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params_iv.addRule(RelativeLayout.CENTER_IN_PARENT);


        //setting image position
        imageView.setId(R.id.imageViewProgramatically);
        imageView.setLayoutParams(params_iv);

        relativeLayout.addView(imageView);

        if(caption!=null && caption.length()>0) {
            TextView itemTv = new TextView(getActivity());
            itemTv.setTypeface(null, Typeface.ITALIC);
            itemTv.setText(caption);

            RelativeLayout.LayoutParams params_tv = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            params_tv.addRule(RelativeLayout.BELOW, R.id.imageViewProgramatically);
            itemTv.setGravity(Gravity.CENTER_HORIZONTAL);

            itemTv.setLayoutParams(params_tv);

            relativeLayout.addView(itemTv);
        }

        //adding view to layout
        ll.addView(relativeLayout);

        */
    }

    private void setNoInternetLayout() {
        parallaxSv.setVisibility(View.GONE);
        noInternetLl.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setEnabled(false);
    }

    private void setInternetPresentLayout() {
        parallaxSv.setVisibility(View.VISIBLE);
        noInternetLl.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(true);
    }

    private void setImageViewBackground(ImageView imageView, Drawable drawable) {

        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(drawable);
        } else {
            imageView.setBackgroundDrawable(drawable);
        }
    }

    @Override
    public void onRefresh() {
        fetchArticleInfo(article_id);
    }
}

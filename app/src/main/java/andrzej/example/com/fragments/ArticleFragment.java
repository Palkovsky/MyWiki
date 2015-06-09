package andrzej.example.com.fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.fab.FloatingActionButton;
import andrzej.example.com.fab.ObservableScrollView;
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
import andrzej.example.com.utils.ArticleViewsManager;
import andrzej.example.com.utils.StringOperations;


public class ArticleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //UI
    ImageView parallaxIv;
    TextView titleTv;
    ObservableScrollView parallaxSv;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout noInternetLl;
    LinearLayout rootArticleLl;
    BootstrapButton retryBtn;
    ArticleViewsManager viewsManager;
    andrzej.example.com.fab.FloatingActionButton fab;

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

        parallaxSv = (ObservableScrollView) v.findViewById(R.id.parallaxSv);
        parallaxIv = (ImageView) v.findViewById(R.id.parallaxIv);
        titleTv = (TextView) v.findViewById(R.id.titleTv);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.article_swipe_refresh_layout);
        noInternetLl = (LinearLayout) v.findViewById(R.id.noInternetLl);
        rootArticleLl = (LinearLayout) v.findViewById(R.id.rootArticle);
        retryBtn = (BootstrapButton) v.findViewById(R.id.noInternetBtn);
        fab = (andrzej.example.com.fab.FloatingActionButton) v.findViewById(R.id.fab);

        fab.attachToScrollView(parallaxSv);

        viewsManager = new ArticleViewsManager(MyApplication.getAppContext());
        viewsManager.setLayout(rootArticleLl);


        /*
        viewsManager.addTextViewToLayout("<b>Derpy</b> - no, z Derpy to jak z Derpy. Miała kupić pomidory, a przyniosła i kupiła kilo wiśni. Zawsze była taka fiu!");
        viewsManager.addImageViewToLayout(StringOperations.pumpUpSize("http://vignette1.wikia.nocookie.net/mlp/images/b/b3/Derpy_Hooves_szkic.jpg/revision/latest/scale-to-width/150?cb=20130305192012&path-prefix=pl", 600), "Szkic Derpy");
        viewsManager.addImageViewToLayout(StringOperations.pumpUpSize("http://vignette4.wikia.nocookie.net/mlp/images/e/e9/Dreppy_x666.png/revision/latest?cb=20130715174906&path-prefix=pl", 600), "  ");
        viewsManager.addHeader(2, "Charakterystyka");
        viewsManager.addTextViewToLayout("Derpy pojawiła się już w pierwszym odcinku z celowym błędem animatorów (tzw. easter eggiem), polegającym na desynchronizacji kierunku patrzenia oczu bohaterki, czego skutkiem był charakterystyczny zez. Pomimo, że pegaz pojawiał się tylko w tle, fani bardzo szybko ją zauważyli i polubili. Ostatecznie zadecydowano, by błędu nie korygować (choć niekiedy Derpy ma prawidłowo zsynchronizowane oczy). Znaczek Derpy opiera się na kucyku generacji pierwszej - Bubbles");
        viewsManager.addHeader(4, "Cechy");
        ArrayList<String> itemz = new ArrayList<String>();
        itemz.add("Idywidualista");
        itemz.add("Pomysłowy");
        itemz.add("Komformista");
        viewsManager.addListToLayout(itemz);
        viewsManager.addHeader(4, "Zabawki");
        viewsManager.addImageViewToLayout(StringOperations.pumpUpSize("http://vignette1.wikia.nocookie.net/mlp/images/d/d3/Derpy%21.jpg/revision/latest/scale-to-width/180?cb=20130806190710&path-prefix=pl", 600), "Duża figurka promocyjna, a obok - opakowanie z Derpy do ozdabiania.");
        viewsManager.addHeader(1, "Header 1");
        viewsManager.addHeader(2, "Header 2");
        viewsManager.addHeader(3, "Header 3");
        viewsManager.addHeader(4, "Header 4");
        */

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
                            if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                setInternetPresentLayout();
                            else
                                setNoInternetLayout();
                            JSONArray sections = response.getJSONArray(Article.KEY_SECTIONS);

                            for (int i = 0; i < sections.length(); i++) {
                                JSONObject section = sections.getJSONObject(i);

                                //Parsowanie zawartości
                                JSONArray content = section.getJSONArray(Article.KEY_CONTENT);

                                int level = section.getInt(Article.KEY_LEVEL);
                                String title = section.getString(Article.KEY_TITLE);
                                viewsManager.addHeader(level, title);


                                if (content.length() > 0) {
                                    for (int j = 0; j < content.length(); j++) {
                                        JSONObject content_section = content.getJSONObject(j);
                                        String type = content_section.getString(Article.KEY_TYPE);

                                        if (type.equals(Article.KEY_PARAGRAPH)) {
                                            String text = content_section.getString(Article.KEY_TEXT);
                                            if (text != null && text.trim().length() > 0)
                                                viewsManager.addTextViewToLayout(text, level);
                                        } else if (type.equals(Article.KEY_LIST)) {
                                            JSONArray elements = content_section.getJSONArray(Article.KEY_ELEMENTS);
                                            if (elements != null && elements.length() > 0)
                                                fetchList(elements, 0, level);
                                        }
                                    }
                                }

                                JSONArray images_section = section.getJSONArray(ArticleImage.KEY_IMAGES);

                                for (int j = 0; j < images_section.length(); j++) {
                                    JSONObject image = images_section.getJSONObject(j);

                                    String img_url = image.getString(ArticleImage.KEY_SRC);
                                    String caption = null;
                                    if (image.has(ArticleImage.KEY_CAPTION))
                                        image.getString(ArticleImage.KEY_CAPTION);

                                    if (img_url != null && img_url.trim().length() > 0) {
                                        imgs.add(new ArticleImage(img_url, caption));
                                        viewsManager.addImageViewToLayout(StringOperations.pumpUpSize(img_url, 600), caption);
                                    }
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




                        } catch (JSONException e) {
                            e.printStackTrace();
                            mSwipeRefreshLayout.setRefreshing(false);
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

    private void fetchList(JSONArray elements, int level, int layout_level) {

        int tree_level;

        if (level <= 0)
            tree_level = 1;
        else
            tree_level = level;

        try {
            for (int i = 0; i < elements.length(); i++) {

                JSONObject element = elements.getJSONObject(i);
                String text = element.getString(Article.KEY_TEXT);
                viewsManager.addListItemToLayout(text, tree_level, layout_level);
                JSONArray nested_elements = element.getJSONArray(Article.KEY_ELEMENTS);

                if (nested_elements != null && nested_elements.length() > 0) {
                    fetchList(nested_elements, tree_level + 1, layout_level);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            mSwipeRefreshLayout.setRefreshing(false);
        }
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
                            if (NetworkUtils.isNetworkAvailable(MyApplication.getAppContext()))
                                setInternetPresentLayout();
                            else
                                setNoInternetLayout();
                            JSONObject item = response.getJSONObject(SearchResult.KEY_ITEMS).getJSONObject(String.valueOf(id));
                            String thumbnail_url = item.getString(Article.KEY_THUMBNAIL);

                            if (thumbnail_url != null && thumbnail_url.trim().length() > 0 && !thumbnail_url.isEmpty()) {
                                JSONObject orginal_dimens = item.getJSONObject(ArticleImage.KEY_ORGINAL_DIMENS);
                                int orginal_width = orginal_dimens.getInt(ArticleImage.KEY_WIDTH);

                                thumbnail_url = StringOperations.pumpUpResolution(orginal_width, thumbnail_url);

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

    private void fetchSimilarArticles(){

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
        rootArticleLl.removeAllViews();
        fetchArticleInfo(article_id);
    }
}

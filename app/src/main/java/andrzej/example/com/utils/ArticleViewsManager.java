package andrzej.example.com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;
import andrzej.example.com.models.Recommendation;
import andrzej.example.com.prefs.SharedPrefsKeys;

/**
 * Created by andrzej on 08.06.15.
 */
public class ArticleViewsManager {
    Context c;
    LinearLayout ll;

    private static final int h1_Size = 32;
    private static final int h2_size = 28;
    private static final int h3_size = 24;
    private static final int h4_size = 20;

    private static int paragraph_vertical_margin = 10;
    private static int header_vertical_margin = 20;
    private static int paragraphLeftMarginCons = 10;
    private static final int imageView_margin = 25;

    private static int recommendationsImageSize = 360;

    private static int textSize = 18; //defaults
    private static int lineSpacing = 10;

    SharedPreferences prefs;


    boolean nightMode;


    public ArticleViewsManager(Context c) {
        this.c = c;
        prefs = PreferenceManager.getDefaultSharedPreferences(this.c);
    }

    public void setLayout(LinearLayout ll) {
        this.ll = ll;
    }

    //Adding Views
    public TextView addTextViewToLayout(String data, int level) {


        textSize = prefs.getInt(SharedPrefsKeys.KEY_TEXT_SIZE_PREF, textSize);
        nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, false);
        lineSpacing = prefs.getInt(SharedPrefsKeys.KEY_LINE_SPACING_PREF, lineSpacing);
        paragraph_vertical_margin = prefs.getInt(SharedPrefsKeys.KEY_PARAGRAPH_MARGIN, paragraph_vertical_margin);
        paragraphLeftMarginCons = prefs.getInt(SharedPrefsKeys.KEY_PAR_MARGIN_LEFT_CON, paragraphLeftMarginCons);
        boolean liteArtice = prefs.getBoolean(SharedPrefsKeys.LITE_ARTICLE_PREF, false);

        final TextView itemTv = new TextView(MyApplication.getAppContext());

        itemTv.setTypeface(null, Typeface.NORMAL);
        itemTv.setText(data);

        final TextSelectionCallback action_mode = new TextSelectionCallback(itemTv, c);

        itemTv.setCustomSelectionActionModeCallback(action_mode);
        itemTv.setTextIsSelectable(true);


        itemTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArticleFragment.finishActionMode();
                RandomArticleFragment.finishActionMode();
                itemTv.startActionMode(action_mode);
                return false;
            }
        });

        if (!nightMode)
            itemTv.setTextColor(c.getResources().getColor(R.color.font_color));
        else
            itemTv.setTextColor(c.getResources().getColor(R.color.nightFontColor));

        if (!liteArtice) {
            itemTv.setTextSize(textSize);
            itemTv.setLineSpacing(lineSpacing, 1);


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(paragraphLeftMarginCons * level, paragraph_vertical_margin, 0, paragraph_vertical_margin);
            itemTv.setLayoutParams(params);
        }

        ll.addView(itemTv);


        return itemTv;
    }


    public TextView addListItemToLayout(String label, int level, int layout_level) {

        nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, false);

        final TextView itemTv = new TextView(MyApplication.getAppContext());
        itemTv.setTypeface(null, Typeface.NORMAL);
        itemTv.setText(Html.fromHtml("<br/> &#8226;" + label));
        itemTv.setTextIsSelectable(true);
        itemTv.setTextSize(textSize);
        itemTv.setLineSpacing(lineSpacing, 1);
        final TextSelectionCallback action_mode = new TextSelectionCallback(itemTv, c);

        itemTv.setCustomSelectionActionModeCallback(action_mode);
        itemTv.setTextIsSelectable(true);

        itemTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArticleFragment.finishActionMode();
                RandomArticleFragment.finishActionMode();
                itemTv.startActionMode(action_mode);
                return false;
            }
        });

        paragraphLeftMarginCons = prefs.getInt(SharedPrefsKeys.KEY_PAR_MARGIN_LEFT_CON, paragraphLeftMarginCons);

        if (!nightMode)
            itemTv.setTextColor(c.getResources().getColor(R.color.font_color));
        else
            itemTv.setTextColor(c.getResources().getColor(R.color.nightFontColor));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(paragraphLeftMarginCons * level * layout_level, 0, 0, 0);

        itemTv.setLayoutParams(params);
        ll.addView(itemTv);

        return itemTv;
    }

    public LinearLayout addRecommendationButtonToLayout(Recommendation recommendation) {

        paragraphLeftMarginCons = prefs.getInt(SharedPrefsKeys.KEY_PAR_MARGIN_LEFT_CON, paragraphLeftMarginCons);
        recommendationsImageSize = prefs.getInt(SharedPrefsKeys.RECOMMENDATION_IMAGE_SIZE_PREF, recommendationsImageSize);
        nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, false);

        LinearLayout recommendationLl = new LinearLayout(MyApplication.getAppContext());
        recommendationLl.setOrientation(LinearLayout.HORIZONTAL);

        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            recommendationLl.setBackground(null);
            recommendationLl.setBackground(ContextCompat.getDrawable(c, R.drawable.selectable_item_background));
        } else {
            recommendationLl.setBackgroundDrawable(null);
            recommendationLl.setBackgroundDrawable(ContextCompat.getDrawable(c, R.drawable.selectable_item_background));
        }


        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LLParams.setMargins(paragraphLeftMarginCons * 2, 0, 0, 10);
        recommendationLl.setGravity(Gravity.CENTER_VERTICAL);
        recommendationLl.setLayoutParams(LLParams);

        final ImageView imageView = new ImageView(c);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(ivParams);
        imageView.setAdjustViewBounds(true);
        imageView.getLayoutParams().height = recommendationsImageSize;
        imageView.getLayoutParams().width = recommendationsImageSize;

        Picasso.with(c).load(recommendation.getSquaredImage(recommendationsImageSize)).resize(recommendationsImageSize, recommendationsImageSize).memoryPolicy(MemoryPolicy.NO_CACHE).
                networkPolicy(NetworkPolicy.NO_CACHE).skipMemoryCache().
                placeholder(ContextCompat.getDrawable(c, R.drawable.ic_action_picture)).error(ContextCompat.getDrawable(c, R.drawable.ic_action_picture)).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                imageView.getLayoutParams().height = recommendationsImageSize;
                imageView.getLayoutParams().width = recommendationsImageSize;
            }
        });
        recommendationLl.addView(imageView);


        TextView textView = new TextView(c);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setText(recommendation.getTitle());
        if (!nightMode)
            textView.setTextColor(c.getResources().getColor(R.color.font_color));
        else
            textView.setTextColor(c.getResources().getColor(R.color.nightFontColor));

        LinearLayout.LayoutParams params_tv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params_tv.setMargins(40, 0, 0, 0);
        textView.setLayoutParams(params_tv);


        if (ArticleFragment.textViews != null)
            ArticleFragment.textViews.add(textView);
        if (RandomArticleFragment.textViews != null)
            RandomArticleFragment.textViews.add(textView);

        recommendationLl.addView(textView);

        ll.addView(recommendationLl);

        return recommendationLl;
    }

    public ImageView addImageViewToLayout(String img_url, String caption) {
        LinearLayout imageLl = new LinearLayout(MyApplication.getAppContext());
        imageLl.setOrientation(LinearLayout.VERTICAL);

        nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, false);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageLl.setGravity(Gravity.CENTER_HORIZONTAL);
        imageLl.setLayoutParams(LLParams);


        //ImageView Setup
        ImageView imageView = new ImageView(c);
        //imageView.setAdjustViewBounds(true);
        //setting image resource
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(MyApplication.getAppContext()).load(img_url).placeholder(ContextCompat.getDrawable(c, R.drawable.ic_action_picture)).memoryPolicy(MemoryPolicy.NO_CACHE).
                networkPolicy(NetworkPolicy.NO_CACHE).error(ContextCompat.getDrawable(c, R.drawable.ic_action_picture)).into(imageView);

        LinearLayout.LayoutParams params_iv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        if (caption != null && caption.trim().length() > 0)
            params_iv.setMargins(0, imageView_margin, 0, 0);
        else
            params_iv.setMargins(0, imageView_margin, 0, imageView_margin);

        //setting image position
        imageView.setId(R.id.imageViewProgramatically);
        imageView.setLayoutParams(params_iv);

        imageLl.addView(imageView);

        if (caption != null && caption.trim().length() > 0) {
            final TextView itemTv = new TextView(c);
            itemTv.setTypeface(null, Typeface.ITALIC);
            itemTv.setText(caption);
            final TextSelectionCallback action_mode = new TextSelectionCallback(itemTv, c);

            itemTv.setCustomSelectionActionModeCallback(action_mode);
            itemTv.setTextIsSelectable(true);

            itemTv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ArticleFragment.finishActionMode();
                    RandomArticleFragment.finishActionMode();
                    itemTv.startActionMode(action_mode);
                    return false;
                }
            });
            if (!nightMode)
                itemTv.setTextColor(c.getResources().getColor(R.color.font_color));
            else
                itemTv.setTextColor(c.getResources().getColor(R.color.nightFontColor));
            LinearLayout.LayoutParams params_tv = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params_tv.setMargins(0, 0, 0, imageView_margin);

            itemTv.setGravity(Gravity.CENTER_HORIZONTAL);

            itemTv.setLayoutParams(params_tv);

            if (ArticleFragment.textViews != null)
                ArticleFragment.textViews.add(itemTv);
            if (RandomArticleFragment.textViews != null)
                RandomArticleFragment.textViews.add(itemTv);

            imageLl.addView(itemTv);
        }

        ll.addView(imageLl);

        return imageView;
    }

    public TextView addHeader(int level, String label) {
        TextView itemTv;
        float pixels;
        switch (level) {
            case 1:
                itemTv = getTextView(h1_Size, label, level);
                ll.addView(itemTv);
                return itemTv;

            case 2:
                itemTv = getTextView(h2_size, label, level);
                ll.addView(itemTv);
                return itemTv;

            case 3:
                itemTv = getTextView(h3_size, label, level);
                ll.addView(itemTv);
                return itemTv;


            default:
                itemTv = getTextView(h4_size, label, level);
                ll.addView(itemTv);
                return itemTv;
        }
    }

    private TextView getTextView(int size, String text, int level) {

        nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, false);

        final TextView itemTv = new TextView(MyApplication.getAppContext());
        if (level <= 2)
            itemTv.setTypeface(null, Typeface.BOLD);


        itemTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        itemTv.setText(text);
        final TextSelectionCallback action_mode = new TextSelectionCallback(itemTv, c);

        itemTv.setCustomSelectionActionModeCallback(action_mode);
        itemTv.setTextIsSelectable(true);

        itemTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ArticleFragment.finishActionMode();
                RandomArticleFragment.finishActionMode();
                itemTv.startActionMode(action_mode);
                return false;
            }
        });

        if (!nightMode)
            itemTv.setTextColor(c.getResources().getColor(R.color.font_color));
        else
            itemTv.setTextColor(c.getResources().getColor(R.color.nightFontColor));

        header_vertical_margin = prefs.getInt(SharedPrefsKeys.KEY_HEADERS_MARGIN, header_vertical_margin);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5 * level, header_vertical_margin, 0, header_vertical_margin);

        itemTv.setLayoutParams(params);

        return itemTv;
    }

    public void destroyAllViews() {
        if (ll.getChildCount() > 0)
            ll.removeAllViews();
    }

}

package andrzej.example.com.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.views.LetterSpacingTextView;

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


    private static int textSize = 18; //defaults
    private static int lineSpacing = 10;


    public ArticleViewsManager(Context c) {
        this.c = c;
    }

    public void setLayout(LinearLayout ll) {
        this.ll = ll;
    }

    //Adding Views
    public void addTextViewToLayout(String data, int level) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.c);
        textSize = prefs.getInt(SharedPrefsKeys.KEY_TEXT_SIZE_PREF, textSize);
        lineSpacing = prefs.getInt(SharedPrefsKeys.KEY_LINE_SPACING_PREF, lineSpacing);

        final TextView itemTv = new TextView(MyApplication.getAppContext());
        itemTv.setTypeface(null, Typeface.NORMAL);
        itemTv.setText(Html.fromHtml(data));
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



        itemTv.setTextColor(c.getResources().getColor(R.color.font_color));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(15 * level, 0, 0, 0);

        itemTv.setLayoutParams(params);
        ll.addView(itemTv);
    }

    public void addListToLayout(ArrayList<String> list) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.c);
        textSize = prefs.getInt(SharedPrefsKeys.KEY_TEXT_SIZE_PREF, textSize);
        lineSpacing = prefs.getInt(SharedPrefsKeys.KEY_LINE_SPACING_PREF, lineSpacing);

        if (list != null && list.size() > 0) {
            String list_string = "";

            int counter = 0;
            for (String item : list) {
                counter++;
                if (counter < list.size())
                    list_string += "&#8226;" + item + "<br/>";
                else
                    list_string += "&#8226;" + item;
            }

            final TextView itemTv = new TextView(MyApplication.getAppContext());
            itemTv.setTypeface(null, Typeface.NORMAL);
            itemTv.setText(Html.fromHtml(list_string));
            itemTv.setLineSpacing(lineSpacing, 1);
            itemTv.setTextSize(textSize);
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

            itemTv.setTextColor(c.getResources().getColor(R.color.font_color));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(15, 0, 0, 0);

            itemTv.setLayoutParams(params);
            ll.addView(itemTv);
        }
    }

    public void addListItemToLayout(String label, int level, int layout_level) {

        final TextView itemTv = new TextView(MyApplication.getAppContext());
        itemTv.setTypeface(null, Typeface.NORMAL);
        itemTv.setText(Html.fromHtml("&#8226;" + label + "<br/>"));
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
        itemTv.setTextColor(c.getResources().getColor(R.color.font_color));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(15 * level * layout_level, 0, 0, 0);

        itemTv.setLayoutParams(params);
        ll.addView(itemTv);

    }

    public void addImageViewToLayout(String img_url, String caption) {


        LinearLayout imageLl = new LinearLayout(MyApplication.getAppContext());
        imageLl.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        imageLl.setGravity(Gravity.CENTER_HORIZONTAL);
        imageLl.setLayoutParams(LLParams);


        //ImageView Setup
        ImageView imageView = new ImageView(c);
        //imageView.setAdjustViewBounds(true);
        //setting image resource
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(MyApplication.getAppContext()).load(img_url).placeholder(c.getResources().getDrawable(R.drawable.ic_action_picture)).error(c.getResources().getDrawable(R.drawable.ic_action_picture)).into(imageView);

        LinearLayout.LayoutParams params_iv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        if (caption != null && caption.trim().length() > 0)
            params_iv.setMargins(0, 25, 0, 0);
        else
            params_iv.setMargins(0, 25, 0, 25);

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
            itemTv.setTextColor(c.getResources().getColor(R.color.font_color));

            LinearLayout.LayoutParams params_tv = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params_tv.setMargins(0, 0, 0, 25);

            itemTv.setGravity(Gravity.CENTER_HORIZONTAL);

            itemTv.setLayoutParams(params_tv);

            imageLl.addView(itemTv);
        }

        ll.addView(imageLl);

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

        itemTv.setTextColor(c.getResources().getColor(R.color.font_color));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5 * level, 25, 0, 25);

        itemTv.setLayoutParams(params);

        return itemTv;
    }
}

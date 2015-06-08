package andrzej.example.com.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;

/**
 * Created by andrzej on 08.06.15.
 */
public class ArticleViewsManager {
    Context c;
    LinearLayout ll;

    private static final int H1_Size = 32;
    private static final int H2_Size = 28;
    private static final int H3_Size = 24;
    private static final int H4_Size = 20;
    private static final int H5_Size = 18;


    public ArticleViewsManager(Context c) {
        this.c = c;
    }

    public void setLayout(LinearLayout ll) {
        this.ll = ll;
    }

    //Adding Views
    public void addTextViewToLayout(String data, int level) {
        TextView itemTv = new TextView(MyApplication.getAppContext());
        itemTv.setTypeface(null, Typeface.NORMAL);
        itemTv.setText(Html.fromHtml(data));
        itemTv.setTextColor(c.getResources().getColor(R.color.font_color));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(15 * level, 0, 0, 0);

        itemTv.setLayoutParams(params);
        ll.addView(itemTv);
    }

    public void addListToLayout(ArrayList<String> list) {
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

            TextView itemTv = new TextView(MyApplication.getAppContext());
            itemTv.setTypeface(null, Typeface.NORMAL);
            itemTv.setText(Html.fromHtml(list_string));
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

        TextView itemTv = new TextView(MyApplication.getAppContext());
        itemTv.setTypeface(null, Typeface.NORMAL);
        itemTv.setText(Html.fromHtml("&#8226;"+ label + "<br/>"));
        itemTv.setTextColor(c.getResources().getColor(R.color.font_color));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(15*level*layout_level, 0, 0, 0);

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
            TextView itemTv = new TextView(c);
            itemTv.setTypeface(null, Typeface.ITALIC);
            itemTv.setText(caption);
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

    public void addHeader(int level, String label) {
        TextView itemTv;
        float pixels;
        switch (level) {
            case 1:
                itemTv = getTextView(H1_Size, label, level);
                ll.addView(itemTv);
                break;

            case 2:
                itemTv = getTextView(H2_Size, label, level);
                ll.addView(itemTv);
                break;

            case 3:
                itemTv = getTextView(H3_Size, label, level);
                ll.addView(itemTv);
                break;


            default:
                itemTv = getTextView(H4_Size, label, level);
                ll.addView(itemTv);
                break;
        }
    }

    private TextView getTextView(int size, String text, int level) {
        TextView itemTv = new TextView(MyApplication.getAppContext());
        if (level <= 2)
            itemTv.setTypeface(null, Typeface.BOLD);


        itemTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        itemTv.setText(text);
        itemTv.setTextColor(c.getResources().getColor(R.color.font_color));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(5*level, 25, 0, 25);

        itemTv.setLayoutParams(params);

        return itemTv;
    }
}

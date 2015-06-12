package andrzej.example.com.adapters;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHeader;

// here's our beautiful adapter
public class ArticleStructureListAdapter extends ArrayAdapter<ArticleHeader> {

    Context mContext;
    int layoutResourceId;
    List<ArticleHeader> data = null;

    public ArticleStructureListAdapter(Context mContext, int layoutResourceId, List<ArticleHeader> data) {
        super(mContext, layoutResourceId, data);

        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /*
         * The convertView argument is essentially a "ScrapView" as described is Lucas post
         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
         * It will have a non-null value when ListView is asking you recycle the row layout.
         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
         */


        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        convertView = inflater.inflate(layoutResourceId, parent, false);
        TextView textViewItem = (TextView) convertView.findViewById(R.id.tvTitle);


        // object item based on the position
        ArticleHeader objectItem = data.get(position);

        int level = objectItem.getLevel();
        String title = objectItem.getTitle();


        textViewItem.setText(objectItem.getTitle());

        if (level == 2) {
            textViewItem.setTypeface(null, Typeface.BOLD);
            textViewItem.setTextSize(18);
        } else if (level == 1) {
            textViewItem.setTextSize(24);
            textViewItem.setTypeface(null, Typeface.BOLD);
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.list_item_selected));

            if(objectItem.getView()==null) {
                textViewItem.setClickable(false);
                convertView.setClickable(false);
            }
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(50, 0, 0, 0);
            textViewItem.setLayoutParams(params);
        }
        return convertView;
    }


}
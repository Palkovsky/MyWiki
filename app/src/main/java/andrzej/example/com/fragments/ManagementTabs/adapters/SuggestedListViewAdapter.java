package andrzej.example.com.fragments.ManagementTabs.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.models.SuggestedItem;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;

/**
 * Created by andrzej on 10.07.15.
 */
public class SuggestedListViewAdapter extends BaseAdapter implements DemoAdapter{

    List<SuggestedItem> myList;
    LayoutInflater inflater;
    Context context;
    private SharedPreferences prefs;



    public SuggestedListViewAdapter(Context context, List<SuggestedItem> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
        prefs = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public SuggestedItem getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ResultViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item_suggested_grid_view, null);
            mViewHolder = new ResultViewHolder();

            mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.wikiTitleTv);
            mViewHolder.thumbnailIv = (ImageView) convertView.findViewById(R.id.wikiThumbnailIv);
            mViewHolder.rootLayout = (RelativeLayout) convertView.findViewById(R.id.rootView);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ResultViewHolder) convertView.getTag();
        }

        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);
        SuggestedItem item = myList.get(position);

        Drawable placeholder = ContextCompat.getDrawable(context, R.drawable.ic_action_picture);


        mViewHolder.tvTitle.setText(item.getTitle());
        Picasso.with(context).load(item.getImageUrl()).placeholder(placeholder).error(placeholder).into(mViewHolder.thumbnailIv);

        return convertView;
    }


    private void setIvBackground(ImageView iv, Drawable drawable) {
        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            iv.setBackground(drawable);
        } else {
            iv.setBackgroundDrawable(drawable);
        }
    }

    @Override
    public void appendItems(List<SuggestedItem> newItems) {
        myList.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public void setItems(List<SuggestedItem> moreItems) {
        myList.clear();
        appendItems(moreItems);
    }


    private class ResultViewHolder {
        RelativeLayout rootLayout;
        ImageView thumbnailIv;
        TextView tvTitle;
    }
}

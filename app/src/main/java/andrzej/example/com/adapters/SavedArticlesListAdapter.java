package andrzej.example.com.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.BookmarkedArticle;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.StringOperations;

/**
 * Created by andrzej on 15.07.15.
 */
public class SavedArticlesListAdapter extends BaseAdapter {

    List<BookmarkedArticle> myList;
    LayoutInflater inflater;
    Context context;
    SharedPreferences prefs;

    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    private List<BookmarkedArticle> selectedItems = new ArrayList<BookmarkedArticle>();

    public SavedArticlesListAdapter(Context context, List<BookmarkedArticle> myList) {
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
    public BookmarkedArticle getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<BookmarkedArticle> getSelectedItems() {
        return selectedItems;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResultViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_saved_articles_list, null);
            mViewHolder = new ResultViewHolder();

            mViewHolder.rootView = (LinearLayout) convertView.findViewById(R.id.rootView);
            mViewHolder.groupTv = (TextView) convertView.findViewById(R.id.sectionHeaderTv);
            mViewHolder.thumbnailIv = (ImageView) convertView.findViewById(R.id.thumbnailIv);
            mViewHolder.titleTv = (TextView) convertView.findViewById(R.id.titleTv);

            convertView.setTag(mViewHolder);
        } else
            mViewHolder = (ResultViewHolder) convertView.getTag();


        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode) {
            mViewHolder.titleTv.setTextColor(context.getResources().getColor(R.color.nightFontColor));
            mViewHolder.groupTv.setTextColor(context.getResources().getColor(R.color.nightFontColor));
        } else {
            mViewHolder.titleTv.setTextColor(context.getResources().getColor(R.color.font_color));
            mViewHolder.groupTv.setTextColor(context.getResources().getColor(R.color.font_color));
        }

        BookmarkedArticle item = myList.get(position);
        Drawable placeholderDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_action_picture);

        Picasso.with(getContext()).load(item.getImgUrl()).placeholder(placeholderDrawable).error(placeholderDrawable).into(mViewHolder.thumbnailIv);
        mViewHolder.titleTv.setText(item.getTitle());
        mViewHolder.groupTv.setText(item.getWikiName() + " ["+ StringOperations.stripUpWikiUrl(item.getWikiUrl()) +"]");

        if (mSelection.get(position) != null) {
            mViewHolder.rootView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        }else
            mViewHolder.rootView.setBackgroundColor(Color.TRANSPARENT);

        // Showing group view logic
        if (position == 0)
            mViewHolder.groupTv.setVisibility(View.VISIBLE);
        else {
            if (!myList.get(position - 1).getWikiName().equals(item.getWikiName())) {
                mViewHolder.groupTv.setVisibility(View.VISIBLE);
            } else
                mViewHolder.groupTv.setVisibility(View.GONE);
        }


        return convertView;
    }


    private class ResultViewHolder {
        LinearLayout rootView;
        TextView groupTv;
        ImageView thumbnailIv;
        TextView titleTv;
    }

    public void setNewSelection(int position, boolean value) {
        mSelection.put(position, value);
        selectedItems.add(myList.get(position));
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        selectedItems.remove(myList.get(position));
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        selectedItems = new ArrayList<BookmarkedArticle>();
        notifyDataSetChanged();
    }

    private Context getContext() {
        return this.context;
    }
}
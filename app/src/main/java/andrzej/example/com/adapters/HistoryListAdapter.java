package andrzej.example.com.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHistoryItem;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SearchResult;

/**
 * Created by andrzej on 02.06.15.
 */
public class HistoryListAdapter extends BaseAdapter {

    List<ArticleHistoryItem> myList;
    LayoutInflater inflater;
    Context context;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

    private List<ArticleHistoryItem> selectedItems = new ArrayList<ArticleHistoryItem>();


    public HistoryListAdapter(Context context, List<ArticleHistoryItem> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public ArticleHistoryItem getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<ArticleHistoryItem> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ResultViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.history_recycler_view_item, null);
            mViewHolder = new ResultViewHolder();

            mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.history_title);
            mViewHolder.groupTv = (TextView) convertView.findViewById(R.id.history_section_header_text);
            mViewHolder.iconIv = (ImageView) convertView.findViewById(R.id.history_thumbnail);
            mViewHolder.rootLayout = (LinearLayout) convertView.findViewById(R.id.history_root_view);

            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ResultViewHolder) convertView.getTag();
        }

        ArticleHistoryItem item = myList.get(position);
        if(item!=null){
            mViewHolder.tvTitle.setText(item.getLabel());
            mViewHolder.groupTv.setText(item.getDateInString());

            // Showing group view logic
            if (position == 0)
                mViewHolder.groupTv.setVisibility(View.VISIBLE);
            else {
                if (!myList.get(position - 1).getDateInString().contains(item.getDateInString())) {
                    mViewHolder.groupTv.setVisibility(View.VISIBLE);
                }else
                    mViewHolder.groupTv.setVisibility(View.GONE);


            }

            if (mSelection.get(position) != null) {
                mViewHolder.rootLayout.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red\
            }else
                mViewHolder.rootLayout.setBackgroundColor(Color.TRANSPARENT);

            String thumb_url = item.getScaledDownImage();

            if(thumb_url!=null) {
                Picasso.with(context).load(item.getScaledDownImage()).error(context.getResources().getDrawable(R.drawable.ic_action_picture))
                        .placeholder(context.getResources().getDrawable(R.drawable.ic_action_picture)).into(mViewHolder.iconIv, new Callback() {
                    @Override
                    public void onSuccess() {
                        mViewHolder.iconIv.setBackgroundColor(Color.TRANSPARENT);
                    }

                    @Override
                    public void onError() {
                        mViewHolder.iconIv.setImageResource(android.R.color.transparent);
                        Drawable d = context.getResources().getDrawable(R.drawable.ic_action_picture);
                        setIvBackground(mViewHolder.iconIv, d);
                    }
                });
            }else{
                Drawable d = context.getResources().getDrawable(R.drawable.ic_action_picture);
                mViewHolder.iconIv.setImageResource(android.R.color.transparent);
                setIvBackground(mViewHolder.iconIv, d);
            }
        }

        return convertView;
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
        selectedItems = new ArrayList<ArticleHistoryItem>();
        notifyDataSetChanged();
    }



    private void setIvBackground(ImageView iv, Drawable drawable) {
        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            iv.setBackground(drawable);
        } else {
            iv.setBackgroundDrawable(drawable);
        }
    }

    // or you can try better way
    private TextView title(View v, int resId, String text) {
        TextView tv = (TextView) v.findViewById(resId);
        tv.setText(text);
        return tv;
    }


    private class ResultViewHolder {
        LinearLayout rootLayout;
        TextView groupTv;
        TextView tvTitle;
        ImageView iconIv;
    }
}

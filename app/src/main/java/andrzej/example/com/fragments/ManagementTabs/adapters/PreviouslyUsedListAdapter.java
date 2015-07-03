package andrzej.example.com.fragments.ManagementTabs.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import andrzej.example.com.databases.WikisFavsDbHandler;
import andrzej.example.com.fragments.ManagementTabs.FavouriteWikisFragment;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.StringOperations;

/**
 * Created by andrzej on 30.06.15.
 */
public class PreviouslyUsedListAdapter extends BaseAdapter {

    List<WikiPreviousListItem> myList;
    LayoutInflater inflater;
    Context context;
    SharedPreferences prefs;
    private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

    private List<WikiPreviousListItem> selectedItems = new ArrayList<WikiPreviousListItem>();

    public PreviouslyUsedListAdapter(Context context, ArrayList myList) {
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
    public WikiPreviousListItem getItem(int position) {
        return myList.get(position);
    }

    public List<WikiPreviousListItem> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ResultViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.previously_used_list_item, null);
            mViewHolder = new ResultViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ResultViewHolder) convertView.getTag();
        }


        if (myList.get(position).getTitle() != null && myList.get(position).getTitle().length() > 0) {
            mViewHolder.tvTitle = title(convertView, R.id.tvTitle, myList.get(position).getTitle());
            mViewHolder.tvUrl = url(convertView, R.id.tvUrl, myList.get(position).getUrl());
        } else {
            mViewHolder.tvTitle = title(convertView, R.id.tvTitle, StringOperations.stripUpWikiUrl(myList.get(position).getUrl()));
            mViewHolder.tvUrl = url(convertView, R.id.tvUrl, myList.get(position).getUrl());
        }

        mViewHolder.btnFav = btnFav(convertView, R.id.btnFav);


        final WikisFavsDbHandler db = new WikisFavsDbHandler(context);

        if (db.itemExsists(myList.get(position).getUrl(), myList.get(position).getTitle())) {
            mViewHolder.btnFav.setRightIcon("fa-remove");
        } else {
            mViewHolder.btnFav.setRightIcon("fa-heart");
        }

        mViewHolder.btnFav.setClickable(true);
        //mViewHolder.btnFav.setEnabled(true);

        mViewHolder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                db.addItem(new WikiFavItem(myList.get(position).getTitle(), myList.get(position).getUrl()));


                if (db.itemExsists(myList.get(position).getUrl(), myList.get(position).getTitle())) {
                    mViewHolder.btnFav.setRightIcon("fa-remove");
                } else {
                    mViewHolder.btnFav.setRightIcon("fa-heart");
                }

                FavouriteWikisFragment.updateDataset();

            }
        });


        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode) {
            mViewHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.nightFontColor));
            mViewHolder.tvUrl.setTextColor(context.getResources().getColor(R.color.drawer_option_active));
        } else {
            mViewHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.font_color));
            mViewHolder.tvUrl.setTextColor(context.getResources().getColor(R.color.font_color));
        }

        if (mSelection.get(position) != null)
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red\
        else
            convertView.setBackgroundColor(Color.TRANSPARENT);


        if (myList.get(position).getUrl().toLowerCase().trim().equals(APIEndpoints.WIKI_NAME.toLowerCase().trim())) {
            convertView.setClickable(false);
            convertView.setFocusable(false);
            convertView.setActivated(false);
            convertView.setEnabled(false);
            convertView.setSelected(false);
            convertView.setPressed(false);
            convertView.setFocusableInTouchMode(false);
            convertView.setBackgroundColor(context.getResources().getColor(R.color.header_background));
        }

        return convertView;
    }


    // or you can try better way
    private TextView title(View v, int resId, String text) {
        TextView tv = (TextView) v.findViewById(resId);
        tv.setText(text);
        return tv;
    }

    private TextView url(View v, int resId, String text) {
        TextView tv = (TextView) v.findViewById(resId);
        tv.setText(text);
        return tv;
    }

    private BootstrapButton btnFav(View v, int resId) {
        BootstrapButton btnFav = (BootstrapButton) v.findViewById(resId);
        return btnFav;
    }

    private class ResultViewHolder {
        TextView tvTitle;
        TextView tvUrl;
        BootstrapButton btnFav;
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

    public int getSelectionSize() {
        return selectedItems.size();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        selectedItems = new ArrayList<WikiPreviousListItem>();
        notifyDataSetChanged();
    }
}

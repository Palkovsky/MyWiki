package andrzej.example.com.fragments.ManagementTabs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.SearchResult;
import andrzej.example.com.models.WikiListItem;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;

/**
 * Created by andrzej on 30.06.15.
 */
public class PreviouslyUsedListAdapter extends BaseAdapter {

    List<WikiListItem> myList;
    LayoutInflater inflater;
    Context context;
    SharedPreferences prefs;

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
    public WikiListItem getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResultViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_search_list_item, null);
            mViewHolder = new ResultViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ResultViewHolder) convertView.getTag();
        }

        String label = myList.get(position).getTitle();

        if (label != null && label.length() > 0)
            mViewHolder.tvTitle = title(convertView, R.id.tvTitle, label);
        else
            mViewHolder.tvTitle = title(convertView, R.id.tvTitle, myList.get(position).getUrl());

        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode) {
            mViewHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.nightFontColor));
        } else
            mViewHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.font_color));

        return convertView;
    }

    // or you can try better way
    private TextView title(View v, int resId, String text) {
        TextView tv = (TextView) v.findViewById(resId);
        tv.setText(text);
        return tv;
    }


    private class ResultViewHolder {
        TextView tvTitle;
    }
}

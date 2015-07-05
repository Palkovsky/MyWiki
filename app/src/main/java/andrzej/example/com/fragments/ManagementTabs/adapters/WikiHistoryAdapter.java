package andrzej.example.com.fragments.ManagementTabs.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.List;

import andrzej.example.com.adapters.OnLongItemClickListener;
import andrzej.example.com.databases.WikisFavsDbHandler;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.models.WikiPreviousListItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnItemClickListener;

/**
 * Created by andrzej on 05.07.15.
 */
public class WikiHistoryAdapter extends RecyclerView.Adapter<WikiHistoryAdapter.ViewHolder> {

    private List<WikiPreviousListItem> mDataset;
    OnItemClickListener mItemClickListener;
    OnLongItemClickListener mLongItemClickListener;
    Context context;

    public WikiHistoryAdapter(Context context, List<WikiPreviousListItem> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }


    @Override
    public WikiHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.previously_used_list_item, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WikiHistoryAdapter.ViewHolder holder, int position) {

        WikiPreviousListItem item = mDataset.get(position);

        if (item.getUrl().toLowerCase().trim().equals(APIEndpoints.WIKI_NAME.toLowerCase().trim()))
            holder.rootView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background_dark));
        else
            holder.rootView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));

        holder.tvTitle.setText(item.getTitle());
        holder.tvUrl.setText(item.getUrl());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode) {
            holder.tvTitle.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
            holder.tvUrl.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
        } else {
            holder.tvTitle.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
            holder.tvUrl.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
        }

        WikisFavsDbHandler db = new WikisFavsDbHandler(getContext());

        if(db.itemExsists(item.getUrl())){
            holder.btnFav.setRightIcon("fa-remove");
        }else{
            holder.btnFav.setRightIcon("fa-heart");
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvTitle;
        public TextView tvUrl;
        public BootstrapButton btnFav;
        public RelativeLayout rootView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rootView = (RelativeLayout) itemLayoutView.findViewById(R.id.rlRootView);
            btnFav = (BootstrapButton) itemLayoutView.findViewById(R.id.btnFav);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.tvTitle);
            tvUrl = (TextView) itemLayoutView.findViewById(R.id.tvUrl);

            //Click listener
            rootView.setOnClickListener(this);
            btnFav.setOnClickListener(this);

            //Long click listener
            rootView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            mLongItemClickListener.onLongItemClick(v, getPosition());
            return true;
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setOnLongItemClickListener(final OnLongItemClickListener mLongItemClickListener){
        this.mLongItemClickListener = mLongItemClickListener;
    }

    public Context getContext() {
        return context;
    }
}
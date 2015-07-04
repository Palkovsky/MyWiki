package andrzej.example.com.fragments.ManagementTabs.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.List;

import andrzej.example.com.fragments.ManagementTabs.FavouriteWikisFragment;
import andrzej.example.com.fragments.ManagementTabs.PreviouslyUsedWikisFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.WikiFavItem;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnItemClickListener;

/**
 * Created by andrzej on 03.07.15.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private List<WikiFavItem> mDataset;
    OnItemClickListener mItemClickListener;
    Context context;

    public FavoritesAdapter(Context context, List<WikiFavItem> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
    }


    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favs_list, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavoritesAdapter.ViewHolder holder, int position) {

        WikiFavItem item = mDataset.get(position);

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


    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTitle;
        public TextView tvUrl;
        public BootstrapButton btnRemove;
        public RelativeLayout rootView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rootView = (RelativeLayout) itemLayoutView.findViewById(R.id.rlRootView);
            btnRemove = (BootstrapButton) itemLayoutView.findViewById(R.id.btnRemove);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.tvTitle);
            tvUrl = (TextView) itemLayoutView.findViewById(R.id.tvUrl);

            /*
                                FavouriteWikisFragment.mHelper.removeFav(FavouriteWikisFragment.mFavs.get(getPosition()).getId());
                    FavouriteWikisFragment.mFavs.remove(getPosition());
                    FavouriteWikisFragment.mAdapter.notifyItemRemoved(getPosition());
                    FavouriteWikisFragment.reInitViews();
                    PreviouslyUsedWikisFragment.refreshList();
             */

            rootView.setOnClickListener(this);
            btnRemove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getPosition());
        }


    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public Context getContext() {
        return context;
    }
}

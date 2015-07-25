package andrzej.example.com.fragments.ManagementTabs.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.adapters.OnItemExpanded;
import andrzej.example.com.adapters.OnLongItemClickListener;
import andrzej.example.com.databases.WikisFavsDbHandler;
import andrzej.example.com.databases.WikisHistoryDbHandler;
import andrzej.example.com.libraries.expandablelayout.ExpandableLayoutListner;
import andrzej.example.com.libraries.expandablelayout.ExpandableRelativeLayout;
import andrzej.example.com.libraries.expandablelayout.OnExpandableClickListener;
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

    private List<String> mExpandedItems = new ArrayList<>();

    SharedPreferences prefs;

    public WikiHistoryAdapter(Context context, List<WikiPreviousListItem> mDataset) {
        this.context = context;
        this.mDataset = mDataset;
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
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
    public void onBindViewHolder(WikiHistoryAdapter.ViewHolder holder, final int position) {

        removeUnusedItemsFromExpanded();

        if (mExpandedItems.contains(mDataset.get(position).getUrl()))
            holder.rootView.setExpaned(true);
        else
            holder.rootView.setExpaned(false);

        WikiPreviousListItem item = mDataset.get(position);


        if (item.getUrl().toLowerCase().trim().equals(APIEndpoints.WIKI_NAME.toLowerCase().trim())) {
            setViewBackground(holder.rootView, ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background_dark));
            setViewBackground(holder.rootView.getNormalPart(), ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background_dark));
            //setViewBackground(holder.rootView.getExtendedPart(), ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background_dark));
        } else {
            setViewBackground(holder.rootView, ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));
            setViewBackground(holder.rootView.getNormalPart(), ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));
            //setViewBackground(holder.rootView.getExtendedPart(), ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));
        }

        if (item.getDescription() == null || item.getDescription().trim().equals(""))
            holder.tvDescription.setVisibility(View.GONE);
        else {
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(item.getDescription());
        }

        holder.tvTitle.setText(item.getTitle());
        holder.tvUrl.setText(item.getUrl());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);

        if (nightMode) {
            holder.tvTitle.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
            holder.tvUrl.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
            holder.tvDescription.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.nightFontColor));
        } else {
            holder.tvTitle.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
            holder.tvUrl.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
            holder.tvDescription.setTextColor(MyApplication.getAppContext().getResources().getColor(R.color.font_color));
        }

        WikisFavsDbHandler db = new WikisFavsDbHandler(getContext());

        if (db.itemExsists(item.getUrl()))
            holder.btnFav.setRightIcon("fa-remove");
        else
            holder.btnFav.setRightIcon("fa-heart");


        if (item.getUrl().equals(APIEndpoints.WIKI_NAME))
            holder.btnSetWiki.setVisibility(View.GONE);
        else
            holder.btnSetWiki.setVisibility(View.VISIBLE);

    }

    public void removeUnusedItemsFromExpanded(){

        List<String> toSave = new ArrayList<>();

        for(WikiPreviousListItem item : mDataset) {
            if(mExpandedItems.contains(item.getUrl())){
                toSave.add(item.getUrl());
            }
        }

        mExpandedItems.clear();
        mExpandedItems.addAll(toSave);

    }

    private void setViewBackground(View imageView, Drawable drawable) {

        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            imageView.setBackground(null);
            imageView.setBackground(drawable);
        } else {
            imageView.setBackgroundDrawable(null);
            imageView.setBackgroundDrawable(drawable);
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
        public BootstrapButton btnSetWiki;
        public ExpandableRelativeLayout rootView;
        public TextView tvDescription;

        private RelativeLayout standardPart;
        private RelativeLayout expandedPart;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            rootView = (ExpandableRelativeLayout) itemLayoutView.findViewById(R.id.rlRootView);
            btnFav = (BootstrapButton) itemLayoutView.findViewById(R.id.btnFav);
            btnSetWiki = (BootstrapButton) itemLayoutView.findViewById(R.id.btnSetWiki);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.tvTitle);
            tvUrl = (TextView) itemLayoutView.findViewById(R.id.tvUrl);
            tvDescription = (TextView) itemLayoutView.findViewById(R.id.descriptionTv);

            standardPart = (RelativeLayout) itemLayoutView.findViewById(R.id.standardPart);
            expandedPart = (RelativeLayout) itemLayoutView.findViewById(R.id.expandedPart);


            rootView.setNormalAndExpandPart(standardPart, expandedPart);
            rootView.setExpaned(false);
            rootView.setAnimationDuration(BaseConfig.DEFAULT_DROPDOWN_ANIMATION_DURATION);

            rootView.setOnClickListener(new OnExpandableClickListener() {
                @Override
                public void onClick(View v) {
                    rootView.concealOrExpandLayout();
                    mItemClickListener.onItemClick(v, getPosition());
                }
            });

            rootView.setExpandEventListener(new ExpandableLayoutListner() {
                @Override
                public void onViewExpand(View v, View expandedPart, View standardPart) {
                    mExpandedItems.add(mDataset.get(getPosition()).getUrl());
                }

                @Override
                public void onViewCollapse(View v, View expandedPart, View standardPart) {
                    mExpandedItems.remove(mDataset.get(getPosition()).getUrl());
                }
            });

            //Click listener
            btnSetWiki.setOnClickListener(this);
            btnFav.setOnClickListener(this);

            //Long click listener
            standardPart.setOnLongClickListener(this);
            expandedPart.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            boolean collapseItem  = prefs.getBoolean(SharedPrefsKeys.COLLAPSE_ITEM_ON_WIKI_SET, false);

            if(collapseItem) {
                if (v.getId() == R.id.btnSetWiki)
                    rootView.concealOrExpandLayout();
            }

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

    public void setOnLongItemClickListener(final OnLongItemClickListener mLongItemClickListener) {
        this.mLongItemClickListener = mLongItemClickListener;
    }


    public Context getContext() {
        return context;
    }
}
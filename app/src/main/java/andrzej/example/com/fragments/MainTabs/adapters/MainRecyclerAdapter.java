package andrzej.example.com.fragments.MainTabs.adapters;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.List;

import andrzej.example.com.adapters.OnLongItemClickListener;
import andrzej.example.com.libraries.expandablelayout.ExpandableLayoutListner;
import andrzej.example.com.libraries.expandablelayout.ExpandableRelativeLayout;
import andrzej.example.com.libraries.expandablelayout.OnExpandableClickListener;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.MainPageArticle;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;
import andrzej.example.com.utils.OnItemClickListener;

/**
 * Created by andrzej on 27.07.15.
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private OnItemClickListener mItemClickListener;

    private List<MainPageArticle> mDataset;
    private Context context;
    private SharedPreferences prefs;

    public MainRecyclerAdapter(Context context, List<MainPageArticle> mDataset){
        this.context = context;
        this.mDataset = mDataset;

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public MainRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_fragment_article, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MainRecyclerAdapter.ViewHolder holder, int position) {

        MainPageArticle item = mDataset.get(position);

        holder.tvTitle.setText(item.getTitle());

        //setViewBackground(holder.rootView, ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));

        boolean nightMode = prefs.getBoolean(SharedPrefsKeys.NIGHT_MODE_ENABLED_PREF, BaseConfig.NIGHT_MODE_DEFAULT);
        if(nightMode){
            holder.tvTitle.setTextColor(getContext().getResources().getColor(R.color.nightFontColor));
        }else{
            holder.tvTitle.setTextColor(getContext().getResources().getColor(R.color.font_color));
        }
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTitle;
        public RelativeLayout rootView;


        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            rootView = (RelativeLayout) itemLayoutView.findViewById(R.id.main_fragment_item_rootView);
            tvTitle = (TextView) itemLayoutView.findViewById(R.id.tvTitle);


            //Click listener
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getPosition());
        }

    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private final Context getContext() {
        return context;
    }
}

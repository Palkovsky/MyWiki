package andrzej.example.com.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;

/**
 * Created by Suleiman on 14-04-2015.
 */
public class SimpleRecyclerAdapter extends RecyclerView.Adapter<SimpleRecyclerAdapter.VersionViewHolder> {
    List<String> versionModels;
    Boolean isHomeList = false;

    public static List<String> homeActivitiesList = new ArrayList<String>();
    public static List<String> homeActivitiesSubList = new ArrayList<String>();
    Context context;
    OnItemClickListener clickListener;


    public void setHomeActivitiesList(Context context) {
        String[] listArray = context.getResources().getStringArray(R.array.home_activities);
        String[] subTitleArray = context.getResources().getStringArray(R.array.home_activities_subtitle);
        for (int i = 0; i < listArray.length; ++i) {
            homeActivitiesList.add(listArray[i]);
            homeActivitiesSubList.add(subTitleArray[i]);
        }
    }

    public SimpleRecyclerAdapter(Context context) {
        isHomeList = true;
        this.context = context;
        setHomeActivitiesList(context);
    }


    public SimpleRecyclerAdapter(Context context, List<String> versionModels) {
        isHomeList = false;
        this.context = context;
        this.versionModels = versionModels;
    }

    @Override
    public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerlist_item, viewGroup, false);
        VersionViewHolder viewHolder = new VersionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VersionViewHolder versionViewHolder, final int i) {

        switch (i) {
            case 0: //Title
                versionViewHolder.header.setText(getContext().getResources().getString(R.string.header_title));
                break;
            case 1://Description
                versionViewHolder.header.setText(getContext().getResources().getString(R.string.header_description));
                break;
            case 2: //PagesCount
                versionViewHolder.header.setText(getContext().getResources().getString(R.string.header_pages_count));
                break;
            case 3://URL
                versionViewHolder.header.setText(getContext().getResources().getString(R.string.header_url));
                setViewBackground(versionViewHolder.cardItemLayout, ContextCompat.getDrawable(getContext(), R.drawable.selectable_item_background));
                versionViewHolder.cardItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(versionModels.get(i)));
                        getContext().startActivity(browserIntent);
                    }
                });
                break;
        }


        if (isHomeList) {
            versionViewHolder.title.setText(homeActivitiesList.get(i));
            versionViewHolder.subTitle.setText(homeActivitiesSubList.get(i));
        } else {
            versionViewHolder.title.setText(versionModels.get(i));
        }

        versionViewHolder.title.setTextColor(getContext().getResources().getColor(R.color.font_color));
        versionViewHolder.header.setTextColor(getContext().getResources().getColor(R.color.font_color));
        versionViewHolder.subTitle.setTextColor(getContext().getResources().getColor(R.color.font_color));
        versionViewHolder.cardItemLayout.setBackgroundColor(getContext().getResources().getColor(R.color.background));

    }

    @Override
    public int getItemCount() {
        if (isHomeList)
            return homeActivitiesList == null ? 0 : homeActivitiesList.size();
        else
            return versionModels == null ? 0 : versionModels.size();
    }


    class VersionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardItemLayout;
        TextView header;
        TextView title;
        TextView subTitle;

        public VersionViewHolder(View itemView) {
            super(itemView);

            cardItemLayout = (CardView) itemView.findViewById(R.id.cardlist_item);
            header = (TextView) itemView.findViewById(R.id.listitem_header);
            title = (TextView) itemView.findViewById(R.id.listitem_name);
            subTitle = (TextView) itemView.findViewById(R.id.listitem_subname);

            if (isHomeList) {
                itemView.setOnClickListener(this);
            } else {
                subTitle.setVisibility(View.GONE);
            }

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    private void setViewBackground(View iv, Drawable drawable) {
        int currentVersion = Build.VERSION.SDK_INT;

        if (currentVersion >= Build.VERSION_CODES.JELLY_BEAN) {
            iv.setBackground(drawable);
        } else {
            iv.setBackgroundDrawable(drawable);
        }
    }

    public Context getContext() {
        return this.context;
    }
}
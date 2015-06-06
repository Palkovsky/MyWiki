package andrzej.example.com.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHistoryItem;

/**
 * Created by andrzej on 06.06.15.
 */
public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder> {

    OnItemClickListener mItemClickListener;
    OnLongItemClickListener mLongItemClickListener;

    private LayoutInflater inflater;
    Context c;
    List<ArticleHistoryItem> mDataset;

    public HistoryRecyclerAdapter(Context c, List<ArticleHistoryItem> mDataset) {
        this.c = c;
        inflater = LayoutInflater.from(this.c);
        this.mDataset = mDataset;
    }

    @Override
    public HistoryRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.history_recycler_view_item, parent, false);

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final HistoryRecyclerAdapter.MyViewHolder holder, int position) {

        ArticleHistoryItem item = mDataset.get(position);

        holder.groupTv.setText(item.getDateInString());
        holder.labelTv.setText(item.getLabel());

        // Showing group view logic
        if (position == 0)
            holder.groupTv.setVisibility(View.VISIBLE);
        else {
            if (!mDataset.get(position - 1).getDateInString().equals(item.getDateInString())) {
                holder.groupTv.setVisibility(View.VISIBLE);
            }
        }

        String thumbnail_url = item.getScaledDownImage();
        if (thumbnail_url != null)
            Picasso.with(c).load(thumbnail_url).placeholder(c.getResources().getDrawable(R.drawable.ic_action_picture)).error(c.getResources().getDrawable(R.drawable.ic_action_picture)).into(holder.iconIv, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Drawable d = c.getResources().getDrawable(R.drawable.ic_action_picture);
                    setIvBackground(holder.iconIv, d);
                }
            });
        else {
            Drawable d = c.getResources().getDrawable(R.drawable.ic_action_picture);
            setIvBackground(holder.iconIv, d);
        }
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
    public int getItemCount() {
        return mDataset.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener

    {
        TextView groupTv;
        TextView labelTv;
        ImageView iconIv;
        LinearLayout rootLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            rootLayout = (LinearLayout) itemView.findViewById(R.id.history_root_view);
            groupTv = (TextView) itemView.findViewById(R.id.history_section_header_text);
            labelTv = (TextView) itemView.findViewById(R.id.history_title);
            iconIv = (ImageView) itemView.findViewById(R.id.history_thumbnail);

            rootLayout.setOnClickListener(this);
            rootLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getAdapterPosition()); //OnItemClickListener mItemClickListener;
        }

        @Override
        public boolean onLongClick(View v) {
            mLongItemClickListener.onLongItemClick(v, getAdapterPosition());
            return false;
        }
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void setOnLongItemClickListener(final OnLongItemClickListener mLongItemClickListener) {
        this.mLongItemClickListener = mLongItemClickListener;
    }
}

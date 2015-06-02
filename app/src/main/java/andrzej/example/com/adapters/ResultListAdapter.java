package andrzej.example.com.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.recycleritems.Article;

/**
 * Created by andrzej on 02.06.15.
 */
public class ResultListAdapter extends BaseAdapter{

    ArrayList<Article> myList;
    LayoutInflater inflater;
    Context context;

    public ResultListAdapter(Context context, ArrayList myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);        // only context can also be used
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Article getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResultViewHolder mViewHolder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_search_list_item, null);
            mViewHolder = new ResultViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ResultViewHolder) convertView.getTag();
        }

        mViewHolder.tvTitle = title(convertView, R.id.tvTitle, myList.get(position).getTitle());

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

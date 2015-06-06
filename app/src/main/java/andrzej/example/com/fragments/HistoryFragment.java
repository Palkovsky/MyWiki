package andrzej.example.com.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.activities.SearchActivity;
import andrzej.example.com.adapters.HistoryRecyclerAdapter;
import andrzej.example.com.adapters.OnItemClickListener;
import andrzej.example.com.adapters.OnLongItemClickListener;
import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHistoryItem;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class HistoryFragment extends Fragment implements OnLongItemClickListener, OnItemClickListener {

    //UI
    RecyclerView recyclerHistory;
    TextView noRecordsTv;

    //ADapter
    private HistoryRecyclerAdapter mAdapter;
    LinearLayoutManager llm;

    //List
    List<ArticleHistoryItem> items;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        setHasOptionsMenu(true);

        ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());
        items = db.getAllItems();
        db.close();

        noRecordsTv = (TextView) v.findViewById(R.id.noRecordsTv);
        recyclerHistory = (RecyclerView) v.findViewById(R.id.historyRecycler);
        recyclerHistory.setHasFixedSize(true);

        if(items.size()<=0) {
            noRecordsTv.setVisibility(View.VISIBLE);
            recyclerHistory.setVisibility(View.GONE);
        }

        mAdapter = new HistoryRecyclerAdapter(getActivity(), items);

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLongItemClickListener(this);

        recyclerHistory.setAdapter(mAdapter);

        llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        llm.setSmoothScrollbarEnabled(true);
        recyclerHistory.setLayoutManager(llm);

        return v;
    }

    @Override
    public void onLongItemClick(View view, int position) {
        Toast.makeText(getActivity(), "Long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view, int position) {
        ArticleHistoryItem item = items.get(position);

        Fragment fragment = new ArticleFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("article_id", item.getId());
        bundle.putString("article_title", item.getLabel());
        fragment.setArguments(bundle);

        ((MaterialNavigationDrawer) getActivity()).setFragment(fragment, item.getLabel());
        ((MaterialNavigationDrawer) getActivity()).setSection(MainActivity.section_article);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.history_menu, menu);
        // Associate searchable configuration with the SearchView
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_removeAll:
                ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());
                db.turncateTable();
                db.close();

                items.clear();
                items = new ArrayList<ArticleHistoryItem>();
                mAdapter.notifyDataSetChanged();

                noRecordsTv.setVisibility(View.VISIBLE);
                recyclerHistory.setVisibility(View.GONE);

                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}

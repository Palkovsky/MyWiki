package andrzej.example.com.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.activities.SearchActivity;
import andrzej.example.com.adapters.HistoryRecyclerAdapter;
import andrzej.example.com.adapters.OnItemClickListener;
import andrzej.example.com.adapters.OnLongItemClickListener;
import andrzej.example.com.databases.ArticleHistoryDbHandler;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleHistoryItem;
import andrzej.example.com.views.MaterialEditText;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;


public class HistoryFragment extends Fragment implements OnLongItemClickListener, OnItemClickListener {

    //UI
    RecyclerView recyclerHistory;
    TextView noRecordsTv;
    MaterialEditText filterEt;

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

        //db.addItem(new ArticleHistoryItem(3730, System.currentTimeMillis()+3600000*5, "Diamond Tiara", "http://vignette4.wikia.nocookie.net/mlp/images/6/64/647px-Hay_yeah_by_fyre_flye-d4axgxd.jpg/revision/latest/scale-to-width/150?cb=20120815152559&path-prefix=pl"));

        items = db.getAllItems();
        db.close();

        noRecordsTv = (TextView) v.findViewById(R.id.noRecordsTv);
        filterEt = (MaterialEditText) v.findViewById(R.id.historyEditText);
        recyclerHistory = (RecyclerView) v.findViewById(R.id.historyRecycler);
        recyclerHistory.setHasFixedSize(true);

        if (items.size() <= 0) {
            noRecordsTv.setVisibility(View.VISIBLE);
            recyclerHistory.setVisibility(View.GONE);
            filterEt.setVisibility(View.GONE);
        }

        mAdapter = new HistoryRecyclerAdapter(getActivity(), items);

        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnLongItemClickListener(this);

        filterEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = s.toString().trim();
                ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());

                items.clear();
                items = new ArrayList<ArticleHistoryItem>();

                if (query.trim().length() > 0)
                    items.addAll(db.getAllItemsLike(query));
                else
                    items.addAll(db.getAllItems());


                db.close();
                mAdapter = new HistoryRecyclerAdapter(getActivity(), items);
                recyclerHistory.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                reInitViews(items.size());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        recyclerHistory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext()
                        .getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(
                        filterEt.getWindowToken(), 0);

                return false;
            }
        });

        recyclerHistory.setAdapter(mAdapter);

        llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        llm.setSmoothScrollbarEnabled(true);
        recyclerHistory.setLayoutManager(llm);

        return v;
    }

    @Override
    public void onItemClick(View view, int position) {

        InputMethodManager imm = (InputMethodManager) MyApplication.getAppContext()
                .getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                filterEt.getWindowToken(), 0);

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
    public void onLongItemClick(View view, int position) {
        Toast.makeText(getActivity(), "Long click", Toast.LENGTH_SHORT).show();
    }

    private void reInitViews(int size) {
        if (size <= 0) { // nie ma
            noRecordsTv.setVisibility(View.VISIBLE);
            recyclerHistory.setVisibility(View.INVISIBLE);

        } else { // som
            noRecordsTv.setVisibility(View.INVISIBLE);
            recyclerHistory.setVisibility(View.VISIBLE);
        }
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

                new MaterialDialog.Builder(getActivity()).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        ArticleHistoryDbHandler db = new ArticleHistoryDbHandler(getActivity());
                        db.turncateTable();
                        db.close();

                        items.clear();
                        items = new ArrayList<ArticleHistoryItem>();
                        mAdapter.notifyDataSetChanged();

                        noRecordsTv.setVisibility(View.VISIBLE);
                        recyclerHistory.setVisibility(View.GONE);
                    }

                }).content(getActivity().getResources().getString(R.string.removeAllQuestion))
                        .positiveText(getActivity().getResources().getString(R.string.yes))
                        .negativeText(getActivity().getResources().getString(R.string.no)).show();


                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}

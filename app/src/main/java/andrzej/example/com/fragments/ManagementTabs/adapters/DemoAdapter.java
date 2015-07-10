package andrzej.example.com.fragments.ManagementTabs.adapters;

import android.widget.ListAdapter;

import java.util.List;

import andrzej.example.com.models.SuggestedItem;

public interface DemoAdapter extends ListAdapter {

    void appendItems(List<SuggestedItem> newItems);

    void setItems(List<SuggestedItem> moreItems);
}
package andrzej.example.com.libraries.expandablelayout;

import android.view.View;

/**
 * Created by andrzej on 21.07.15.
 */
public interface ExpandableLayoutListner {
    void onViewExpand(View v, View expandedPart, View standardPart);
    void onViewCollapse(View v, View expandedPart, View standardPart);
}

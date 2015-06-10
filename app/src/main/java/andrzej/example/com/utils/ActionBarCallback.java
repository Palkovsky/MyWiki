package andrzej.example.com.utils;

import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import andrzej.example.com.mlpwiki.R;

class ActionBarCallback implements ActionMode.Callback {

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        mode.getMenuInflater().inflate(R.menu.delete_history_menu, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub

        mode.setTitle("CheckBox is Checked");
        return false;
    }
}
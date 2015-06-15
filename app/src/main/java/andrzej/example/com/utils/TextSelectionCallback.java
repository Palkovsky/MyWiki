package andrzej.example.com.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Browser;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import andrzej.example.com.activities.MainActivity;
import andrzej.example.com.activities.SearchActivity;
import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.mlpwiki.MyApplication;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.network.NetworkUtils;

class TextSelectionCallback implements ActionMode.Callback {

    TextView bodyView;
    Context c;

    public TextSelectionCallback(TextView bodyView, Context c) {
        this.bodyView = bodyView;
        this.c = c;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.text_selection_contextual, menu);

        if (ArticleFragment.mActionModes != null)
            ArticleFragment.mActionModes.add(mode);

        if (RandomArticleFragment.mActionModes != null)
            RandomArticleFragment.mActionModes.add(mode);

        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        mode.setTitle("");
        bodyView.requestFocus();

        if (menu.findItem(android.R.id.selectAll) != null)
            menu.findItem(android.R.id.selectAll).setVisible(false);
        //menu.findItem(android.R.id.selectAll).setTitle(c.getResources().getString(R.string.menu_selectAll));


        if (menu.findItem(android.R.id.copy) != null)
            menu.findItem(android.R.id.copy).setVisible(false);
        //menu.findItem(android.R.id.copy).setTitle(c.getResources().getString(R.string.menu_copy));

        return false;
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        int start = bodyView.getSelectionStart();
        int end = bodyView.getSelectionEnd();
        SpannableStringBuilder ssb = new SpannableStringBuilder(bodyView.getText());
        String text = bodyView.getText().toString().substring(start, end);

        switch (item.getItemId()) {

            case R.id.menu_copy:
                BasicUtils.clipData(c, text);
                Toast.makeText(c, c.getResources().getString(R.string.copy), Toast.LENGTH_SHORT).show();
                mode.finish();
                break;

            case R.id.menu_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                Intent i = Intent.createChooser(sharingIntent, c.getResources().getString(R.string.share_via));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(i);
                break;


            case R.id.menu_search:
                if (!TextUtils.isEmpty(text)) {
                    Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(SearchManager.QUERY, text);
                    intent.putExtra(Browser.EXTRA_APPLICATION_ID, c.getPackageName());
                    try {
                        c.startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        // If no app handles it, do nothing.
                    }
                }
                mode.finish();
                break;

        }
        return false;
    }

    public void onDestroyActionMode(ActionMode mode) {
        bodyView.clearFocus();
    }

}
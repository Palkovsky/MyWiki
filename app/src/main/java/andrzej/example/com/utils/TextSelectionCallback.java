package andrzej.example.com.utils;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.Article;

class TextSelectionCallback implements ActionMode.Callback {

    TextView bodyView;
    Context c;

    public TextSelectionCallback(TextView bodyView, Context c) {
        this.bodyView = bodyView;
        this.c = c;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.d(null, "onCreateActionMode");
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.text_selection_contextual, menu);

        mode.setTitle("");
        menu.findItem(android.R.id.selectAll).setTitle(c.getResources().getString(R.string.menu_selectAll));
        menu.findItem(android.R.id.copy).setTitle(c.getResources().getString(R.string.menu_copy));

        if (ArticleFragment.mActionModes != null)
            ArticleFragment.mActionModes.add(mode);

        if (RandomArticleFragment.mActionModes != null)
            RandomArticleFragment.mActionModes.add(mode);

        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        Log.d(null, String.format("onActionItemClicked item=%s/%d", item.toString(), item.getItemId()));
        CharacterStyle cs;
        int start = bodyView.getSelectionStart();
        int end = bodyView.getSelectionEnd();
        SpannableStringBuilder ssb = new SpannableStringBuilder(bodyView.getText());

        switch (item.getItemId()) {
            case R.id.menu_share:
                String text = bodyView.getText().toString().substring(start, end);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
                Intent i = Intent.createChooser(sharingIntent, c.getResources().getString(R.string.share_via));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(i);
                break;
        }
        return false;
    }

    public void onDestroyActionMode(ActionMode mode) {
    }
}
package andrzej.example.com.utils;

import android.content.Context;
import android.view.ActionMode;

import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.models.Article;

/**
 * Created by andrzej on 14.06.15.
 */
public class BasicUtils {
    public static void clipData(Context c, String text) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText("text", text);
            clipboard.setPrimaryClip(clip);
        }
    }

    public static void deleteActionModes(){
        if(ArticleFragment.mActionModes!=null) {
            for(ActionMode item : ArticleFragment.mActionModes){
                item.finish();
            }
            ArticleFragment.mActionModes.clear();
        }

        if(RandomArticleFragment.mActionModes!=null){
            for(ActionMode item : RandomArticleFragment.mActionModes){
                item.finish();
            }
            RandomArticleFragment.mActionModes.clear();
        }
    }
}

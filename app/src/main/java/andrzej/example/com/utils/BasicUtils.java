package andrzej.example.com.utils;

import android.content.Context;

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
}

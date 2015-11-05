package andrzej.example.com.views;

import android.content.Context;
import android.os.Build;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import com.afollestad.materialdialogs.util.DialogUtils;

/**
 * Created by andrzej on 27.10.15.
 */
public class IntegerEditTextPreference extends EditTextPreference {


    public IntegerEditTextPreference(Context context) {
        super(context);
    }

    public IntegerEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IntegerEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected String getPersistedString(String defaultReturnValue) {
        return String.valueOf(getPersistedInt(-1));
    }

    @Override
    protected boolean persistString(String value) {
        return persistInt(Integer.valueOf(value));
    }
}

package andrzej.example.com.utils;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import andrzej.example.com.activities.MainActivity;

public class BaseBackPressedListener implements OnBackPressedListener {
    private final MainActivity activity;

    public BaseBackPressedListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void doBack() {
        activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
}
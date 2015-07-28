package andrzej.example.com.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import andrzej.example.com.fragments.WikisManagementFragment;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.prefs.APIEndpoints;
import andrzej.example.com.prefs.BaseConfig;
import andrzej.example.com.prefs.SharedPrefsKeys;

public class InitialActivity extends AppCompatActivity implements View.OnClickListener {

    //Bundle Keys
    public static final String KEY_REDIRECT = "KEY_REDIRECTION";

    Shimmer shimmer;
    Bitmap background;


    RelativeLayout rootView;
    ShimmerTextView shimmerTv;
    BootstrapButton addWikiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        APIEndpoints.WIKI_NAME = prefs.getString(SharedPrefsKeys.CURRENT_WIKI_URL, BaseConfig.DEFAULT_WIKI);;

        shimmer = new Shimmer();

        if(APIEndpoints.WIKI_NAME.equals("") || APIEndpoints.WIKI_NAME == null){
            setContentView(R.layout.activity_initial);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            background = BitmapFactory.decodeStream(getResources().openRawResource(R.raw.init_background), null, options);


            //UI Initialization
            rootView = (RelativeLayout) findViewById(R.id.rootView);
            shimmerTv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
            addWikiBtn = (BootstrapButton) findViewById(R.id.addWikiBtn);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                rootView.setBackground(new BitmapDrawable(getResources(), background));
            } else {
                rootView.setBackgroundDrawable(new BitmapDrawable(getResources(), background));
            }

            //Text Animation
            shimmer.setDuration(5000)
                    .setStartDelay(300);
            shimmer.start(shimmerTv);

            //Listeners
            addWikiBtn.setOnClickListener(this);


        }else{
            Intent newIntent = new Intent(this, MainActivity.class);
            startActivity(newIntent);
            finish();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addWikiBtn:
                Intent addWikiIntent = new Intent(this, MainActivity.class);
                addWikiIntent.putExtra(KEY_REDIRECT, WikisManagementFragment.TAG);
                startActivity(addWikiIntent);
                finish();
                break;
        }
    }
}

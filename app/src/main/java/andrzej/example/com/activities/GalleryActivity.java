package andrzej.example.com.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import andrzej.example.com.adapters.GalleryViewPagerAdapter;
import andrzej.example.com.fragments.ArticleFragment;
import andrzej.example.com.fragments.RandomArticleFragment;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleImage;
import andrzej.example.com.utils.BasicUtils;

public class GalleryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    //Finals
    public static final String KEY_TYPE = "key_type";
    public static final String KEY_POSITON = "key_positon";
    public static final String KEY_ARTICLE = "key_article";
    public static final String KEY_RANDOM = "key_random_article";

    //Ui
    private Toolbar toolbar;
    private TextView captionTv;
    private ViewPager pager;

    //Crutials
    int positon = 0;

    //Lists
    private List<ArticleImage> imgs;

    //Adapter
    private GalleryViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarAutoConfig(toolbar);
        pager = (ViewPager) findViewById(R.id.gallery_pager);
        captionTv = (TextView) findViewById(R.id.gallery_captionTv);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String type = extras.getString(KEY_TYPE);
            positon = extras.getInt(KEY_POSITON);

            imgs = initArray(type);

            mAdapter = new GalleryViewPagerAdapter(this, imgs);
            pager.setAdapter(mAdapter);
            pager.setCurrentItem(positon);

            pager.addOnPageChangeListener(this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gallery, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_download:
                ArticleImage iItem = imgs.get(positon);
                downloadFile(iItem.getImg_url(), iItem.getLabel());
                break;

            case R.id.action_copy:
                BasicUtils.clipData(this, imgs.get(positon).getImg_url());
                Toast.makeText(this, getResources().getString(R.string.copy), Toast.LENGTH_SHORT).show();
                break;

            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<ArticleImage> initArray(String type) {
        if (type.equals(KEY_ARTICLE))
            return ArticleFragment.imgs;
        else if (type.equals(KEY_RANDOM))
            return RandomArticleFragment.imgs;

        return null;
    }

    private void toolbarAutoConfig(Toolbar toolbar) {
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        toolbar.setTitle("");
        Drawable background = ContextCompat.getDrawable(this, R.drawable.reversed_aura);
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackgroundDrawable(background);
        } else {
            toolbar.setBackground(background);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.positon = position;

        ArticleImage item = imgs.get(position);

        if (item.getLabel() != null)
            captionTv.setText(item.getLabel());
        else
            captionTv.setText("");
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public void downloadFile(String uRl, String caption) {


        try {
            File direct = new File(Environment.getExternalStorageDirectory()
                    + "/" + getResources().getString(R.string.app_name));

            if (!direct.exists()) {
                direct.mkdirs();
            }

            DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(uRl);
            DownloadManager.Request request = new DownloadManager.Request(
                    downloadUri);


            if (caption == null || caption.trim().length() <= 0)
                caption = "";

            request.setAllowedNetworkTypes(
                    DownloadManager.Request.NETWORK_WIFI
                            | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false).setTitle("Pobieranie obrazka...")
                    .setDescription(caption)
                    .setDestinationInExternalPublicDir(getResources().getString(R.string.app_name) + "DownloadedImages", uRl);

            mgr.enqueue(request);

            Toast.makeText(this, getResources().getString(R.string.download_beg), Toast.LENGTH_SHORT).show();
        }catch (IllegalStateException e){
            Toast.makeText(this, getResources().getString(R.string.download_error), Toast.LENGTH_SHORT).show();
        }

    }
}

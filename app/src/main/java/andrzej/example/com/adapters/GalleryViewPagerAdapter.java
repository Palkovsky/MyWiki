package andrzej.example.com.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import andrzej.example.com.activities.GalleryActivity;
import andrzej.example.com.mlpwiki.R;
import andrzej.example.com.models.ArticleImage;
import andrzej.example.com.utils.StringOperations;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by andrzej on 19.06.15.
 */
public class GalleryViewPagerAdapter extends PagerAdapter {

    private Context c;
    private List<ArticleImage> imgs;
    private LayoutInflater inflater;

    public GalleryViewPagerAdapter(Context c, List<ArticleImage> imgs) {
        this.c = c;
        this.imgs = imgs;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final PhotoView imgDisplay;
        final ProgressBar progressBar;
        RelativeLayout rootView;

        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.gallery_viewpager_item,
                container, false);

        if (viewLayout != null) {
            progressBar = (ProgressBar) viewLayout
                    .findViewById(R.id.gallery_progressBar);
            progressBar.setVisibility(View.VISIBLE);

            imgDisplay = (PhotoView) viewLayout.findViewById(R.id.gallery_image);

            if (position < imgs.size()) {
                final ArticleImage imageItem = imgs.get(position);

                String imgUrl = imageItem.getImg_url();
                if(position>0)
                    imgUrl = StringOperations.pumpUpSize(imgUrl, 1080);

                final String finalImgUrl = imgUrl;
                Picasso.with(getContext()).load(imgUrl).into(imgDisplay, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);

                        imgDisplay.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                            @Override
                            public void onPhotoTap(View view, float v, float v1) {
                                if (GalleryActivity.isInterfaceHidden())
                                    GalleryActivity.showInterface();
                                else
                                    GalleryActivity.hideInterface();
                            }
                        });
                    }

                    @Override
                    public void onError() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }


    public Context getContext() {
        return c;
    }
}

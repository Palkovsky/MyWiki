package andrzej.example.com.fragments.MainTabs.adapters;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import andrzej.example.com.fragments.MainTabs.NewestArticlesFragment;
import andrzej.example.com.fragments.MainTabs.TrendingArticlesFragment;
import andrzej.example.com.mlpwiki.MyApplication;

/**
 * Created by andrzej on 26.07.15.
 */
public class MainTabsAdapter extends FragmentStatePagerAdapter {

    int[] titles; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public MainTabsAdapter(FragmentManager fm, int[] mTitles, int mNumbOfTabsumb) {
        super(fm);
        this.titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                NewestArticlesFragment newestArticlesFragment = new NewestArticlesFragment();
                return newestArticlesFragment;

            default:
                TrendingArticlesFragment trendingArticlesFragment = new TrendingArticlesFragment();
                return  trendingArticlesFragment;
        }

    }

    // This method return the titles for the Tabs in the Tab Strip


    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = ContextCompat.getDrawable(MyApplication.getAppContext(), titles[position]);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}

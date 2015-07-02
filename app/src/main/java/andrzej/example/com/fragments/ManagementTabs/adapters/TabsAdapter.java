package andrzej.example.com.fragments.ManagementTabs.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import andrzej.example.com.fragments.ManagementTabs.FavouriteWikisFragment;
import andrzej.example.com.fragments.ManagementTabs.PreviouslyUsedWikisFragment;
import andrzej.example.com.fragments.ManagementTabs.SuggestedWikisFragment;

/**
 * Created by andrzej on 24.06.15.
 */
public class TabsAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public TabsAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {


        switch (position){
            case 0:
                PreviouslyUsedWikisFragment previouslyUsedWikisFragment = new PreviouslyUsedWikisFragment();
                return previouslyUsedWikisFragment;

            case 1:
                FavouriteWikisFragment favouriteWikisFragment = new FavouriteWikisFragment();
                return  favouriteWikisFragment;

            default:
                SuggestedWikisFragment suggestedWikisFragment = new SuggestedWikisFragment();
                return suggestedWikisFragment;
        }

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}

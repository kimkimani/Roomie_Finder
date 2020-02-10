package com.roomiegh.roomie.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.roomiegh.roomie.fragments.TabBrowse;
import com.roomiegh.roomie.fragments.TabHome;
import com.roomiegh.roomie.fragments.TabProfile;


/**
 * Created by Kwadwo Agyapon-Ntra on 06/10/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter{

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    String currentUserEmail;    //store current user's email

    //Build constructor and assign passed values to appropriate values in class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabs, String mCurrentUserEmail) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabs;
        this.currentUserEmail = mCurrentUserEmail;

    }

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabs) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabs;
    }


    //return the right fragment for every position in the view pager
    @Override
    public Fragment getItem(int position) {
        /*Bundle currentUserInfo = new Bundle();
        currentUserInfo.putString(PushUserUtil.USER_EMAIL,currentUserEmail);*/
        if(position == 0) // if the position is 0 we are returning the First tab
        {
            TabHome tabHome = new TabHome();
            //tabHome.setArguments(currentUserInfo);
            return tabHome;
        }
        else if(position==1)
        {
            TabBrowse tabBrowse = new TabBrowse();
            //tabBrowse.setArguments(currentUserInfo);
            return tabBrowse;
        }
        else
        {
            TabProfile tabProfile = new TabProfile();
            //tabProfile.setArguments(currentUserInfo);
            return tabProfile;
        }

    }

    //return titles for tabs in tab strip
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    //return the number of tabs for the tab strip
    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}

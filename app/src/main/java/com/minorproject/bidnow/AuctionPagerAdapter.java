package com.minorproject.bidnow;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AuctionPagerAdapter extends FragmentPagerAdapter {

    public AuctionPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AuctionsFragment("Live");
            case 1:
                return new AuctionsFragment("Upcoming");
            case 2:
                return new AuctionsFragment("Completed");
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3; // Three tabs: Live, Upcoming, Completed
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Live";
            case 1:
                return "Upcoming";
            case 2:
                return "Completed";
            default:
                return null;
        }
    }
}

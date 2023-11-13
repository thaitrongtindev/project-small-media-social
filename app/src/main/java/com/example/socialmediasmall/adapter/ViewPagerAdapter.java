package com.example.socialmediasmall.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.socialmediasmall.fragment.AddFragment;
import com.example.socialmediasmall.fragment.HomeFragment;
import com.example.socialmediasmall.fragment.NotificationFragment;
import com.example.socialmediasmall.fragment.ProfileFragment;
import com.example.socialmediasmall.fragment.SearchFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    //private int noOfTabs = 4;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new AddFragment();
            case 3:
                return new NotificationFragment();
            case 4:
                return new ProfileFragment();

            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}

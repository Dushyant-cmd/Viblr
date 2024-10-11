package com.veblr.android.veblrapp.adapter;

import android.app.ActionBar;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.repositories.HomeRepository;
import com.veblr.android.veblrapp.ui.CategoryFragment;
import com.veblr.android.veblrapp.ui.FollowFragment;
import com.veblr.android.veblrapp.ui.HomeFragment;
import com.veblr.android.veblrapp.ui.NotificationFragment;
import com.veblr.android.veblrapp.ui.UploadFragment;

import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private androidx.appcompat.app.ActionBar actionBar;
    private List<VIdeoItem> list;
    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context=context;
    }
    public ViewPagerAdapter(FragmentManager fm, Context context, androidx.appcompat.app.ActionBar actionBar,List<VIdeoItem> list) {
        super(fm);
        this.context=context;
        this.actionBar =  actionBar;
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0: return  HomeFragment.newInstance(actionBar,list);
            case 1: return  CategoryFragment.newInstance(actionBar);
            case 2: return new UploadFragment();
            case 3: return  FollowFragment.newInstance(actionBar);
            case 4: return  NotificationFragment.newInstance(actionBar);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
package com.veblr.android.veblrapp.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.veblr.android.veblrapp.model.VIdeoItem;
import com.veblr.android.veblrapp.ui.VideoWatchFragment;

import java.util.ArrayList;

public class VideoPageFragmentAdapter extends FragmentPagerAdapter {

    ArrayList<VIdeoItem> vIdeoItemArrayList;

    public VideoPageFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
     //   return VideoWatchFragment.newInstance(vIdeoItemArrayList);
     return    new VideoWatchFragment();
    }

    @Override
    public int getCount() {
        return 0;
    }
}

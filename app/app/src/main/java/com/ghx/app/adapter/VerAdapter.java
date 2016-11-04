package com.ghx.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guo_hx on 2016/11/3.15:48
 */

public class VerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mListFragments = new ArrayList<>();

    public VerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mListFragments = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mListFragments.get(position);
    }

    @Override
    public int getCount() {
        return mListFragments.size();
    }


    public static class Holder {
        private final List<Fragment> fragments = new ArrayList<>();
        private FragmentManager manager;
        public Holder(FragmentManager manager) {
            this.manager = manager;
        }

        public Holder add(Fragment f) {
            fragments.add(f);
            return this;
        }

        public VerAdapter set() {
            return new VerAdapter(manager, fragments);
        }
    }
}

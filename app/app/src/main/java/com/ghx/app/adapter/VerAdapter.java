package com.ghx.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.ghx.app.fragment.ContentFragment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by guo_hx on 2016/11/3.15:48
 */

public class VerAdapter extends FragmentPagerAdapter {

    private ArrayList<Map<String, Object>> mList = new ArrayList<>();

    public VerAdapter(FragmentManager fm, ArrayList<Map<String, Object>> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public Fragment getItem(int position) {
//        return ContentFragment.newInstance(position % mList.size(), mList);
        return ContentFragment.newInstance(10, mList);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

}

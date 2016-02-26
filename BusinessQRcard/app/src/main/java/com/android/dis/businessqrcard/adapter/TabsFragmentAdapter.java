package com.android.dis.businessqrcard.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.android.dis.businessqrcard.fragment.AbstractTabFragment;
import com.android.dis.businessqrcard.fragment.FragmentEditInfo;
import com.android.dis.businessqrcard.fragment.FragmentScanQR;
import com.android.dis.businessqrcard.fragment.FragmentShowQR;

import java.util.HashMap;
import java.util.Map;

public class TabsFragmentAdapter extends FragmentPagerAdapter {

    private Map<Integer, AbstractTabFragment> tabs;
    private Context context;

    public TabsFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        initTabsMap(context);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    private void initTabsMap(Context context) {
        tabs = new HashMap<>();
        tabs.put(0, FragmentShowQR.getInstance(context));
        tabs.put(1, FragmentScanQR.getInstance(context));
        tabs.put(2, FragmentEditInfo.getInstance(context));
    }
}

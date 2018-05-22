package com.thisobeystudio.customviewpager.viewpager;

/*
 * Created by thisobeystudio on 8/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

import android.support.v4.app.FragmentManager;

/**
 * A {@link CustomFragmentStatePagerAdapter} that returns a fragment corresponding
 * to one of the pages.
 */

public abstract class CustomPagerAdapter extends CustomFragmentStatePagerAdapter {
    protected CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }
}
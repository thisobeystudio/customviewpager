package com.thisobeystudio.customviewpager.viewpager;

/*
 * Created by thisobeystudio on 8/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * A Simple {@link Fragment} to be used as 'root' when using {@link CustomViewPagerCallbacks}
 */
public abstract class CustomFragment extends Fragment implements CustomViewPagerCallbacks {

    /**
     * The fragment argument representing the {@link CustomIndexHelper} for this fragment.
     */
    protected static final String ARG_CUSTOM_INDEX_HELPER = "custom_index_helper";

    protected CustomIndexHelper mCustomIndexHelper = null;

    public CustomFragment() {
    }

    protected void setCustomIndexHelper(Bundle args) {
        if (args == null || !args.containsKey(ARG_CUSTOM_INDEX_HELPER)) return;
        mCustomIndexHelper = args.getParcelable(ARG_CUSTOM_INDEX_HELPER);
    }

    protected int getPageIndex() {
        if (mCustomIndexHelper == null) return 0;
        return mCustomIndexHelper.getPagerPosition();
    }

    protected int getDataIndex() {
        if (mCustomIndexHelper == null) return 0;
        return mCustomIndexHelper.getDataPosition();
    }

    protected boolean isHelperFirst() {
        return mCustomIndexHelper != null && mCustomIndexHelper.isHelperFirst();
    }

    protected boolean isRealFirst() {
        return mCustomIndexHelper != null && mCustomIndexHelper.isRealFirst();
    }

    protected boolean isHelperLast() {
        return mCustomIndexHelper != null && mCustomIndexHelper.isHelperLast();
    }

    protected boolean isRealLast() {
        return mCustomIndexHelper != null && mCustomIndexHelper.isRealLast();
    }
}

package com.thisobeystudio.customviewpager.viewpager;

/*
 * Created by thisobeystudio on 8/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewParent;

import com.thisobeystudio.customviewpager.indicator.CustomIndicator;
import com.thisobeystudio.customviewpager.indicator.IndicatorsRecyclerViewAdapter;
import com.thisobeystudio.customviewpager.models.CustomFragment;

import java.util.ArrayList;

public final class CustomViewPager extends ViewPager implements IndicatorsRecyclerViewAdapter.IndicatorCallbacks {

    private Object firstPageData;
    private Object lastPageData;

    private CustomIndicator mCustomIndicator;
    private static final String TAG = "CustomViewPager";

    public CustomViewPager(@NonNull Context context) {
        super(context);
        this.firstPageData = new Object();      // todo remove this initialization ??
        this.lastPageData = new Object();       // todo remove this initialization ??
        this.initCustomViewPagerOnPageChangeListener();
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.firstPageData = new Object();      // todo remove this initialization ??
        this.lastPageData = new Object();       // todo remove this initialization ??
        this.initCustomViewPagerOnPageChangeListener();
    }

    private void initCustomViewPagerOnPageChangeListener() {
        this.addOnPageChangeListener(new OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int posOffsetPixels) {
            }

            public void onPageSelected(int position) {
                updateIndicatorSelection(getRealPosition(position));
            }

            public void onPageScrollStateChanged(int state) {
                if (state == 0 && isFirstHelperPageSelected()) {
                    setCurrentItem(getRealCount() + -1, false);
                } else if (state == 0 && isLastHelperPageSelected()) {
                    setCurrentItem(getRealFirstPageIndex() + -1, false);
                }
            }
        });
    }

    private int getRealFirstPageIndex() {
        return 1;
    }

    private int getRealLastPageIndex() {
        return this.getRealCount();
    }

    private int getHelperFirstPageIndex() {
        return this.getCount() - 1;
    }

    private int getHelperLastPageIndex() {
        return 0;
    }

    public final int getCount() {
        return this.getRealCount() <= 0 ? 0 : this.getRealCount() + 2;
    }

    public final int getRealCount() {
        CustomPagerAdapter adapter = this.getAdapter();
        return adapter != null ? adapter.getRealCount() : 0;
    }

    public final boolean isFirstRealPageSelected() {
        return super.getCurrentItem() == this.getRealFirstPageIndex();
    }

    public final boolean isLastRealPageSelected() {
        return super.getCurrentItem() == this.getRealLastPageIndex();
    }

    private boolean isFirstHelperPageSelected() {
        return super.getCurrentItem() == this.getHelperLastPageIndex();
    }

    private boolean isLastHelperPageSelected() {
        return super.getCurrentItem() == this.getHelperFirstPageIndex();
    }

    @Nullable
    public final ArrayList getFragments() {
        CustomPagerAdapter adapter = this.getAdapter();
        return adapter != null ? adapter.getFragments() : null;
    }

    @Nullable
    public final CustomFragment getRealFirstFragment() {
        return this.getFragment(this.getRealFirstPageIndex());
    }

    @Nullable
    public final CustomFragment getHelperFirstFragment() {
        return this.getFragment(this.getHelperFirstPageIndex());
    }

    @Nullable
    public final CustomFragment getRealLastFragment() {
        return this.getFragment(this.getRealLastPageIndex());
    }

    @Nullable
    public final CustomFragment getHelperLastFragment() {
        return this.getFragment(this.getHelperLastPageIndex());
    }

    @Nullable
    public final CustomFragment getFragment(int index) {

        ArrayList fragments = this.getFragments();

        if (fragments != null && index >= 0 && index < fragments.size()) {
            return (CustomFragment) fragments.get(index);
        } else return null;
    }

    @NonNull
    public final Object getFirstPageData() {
        return this.firstPageData;
    }

    public final void setFirstPageData(@NonNull Object data) {
        this.firstPageData = data;
    }

    @NonNull
    public final Object getLastPageData() {
        return this.lastPageData;
    }

    public final void setLastPageData(@NonNull Object data) {
        this.lastPageData = data;
    }

    public final void clearPagesData() {
        this.clearFirstPageData();
        this.clearLastPageData();
    }

    public final void clearFirstPageData() {
        this.firstPageData = new Object();
    }

    public final void clearLastPageData() {
        this.lastPageData = new Object();
    }

    public final void setPageData(boolean first, boolean last, @Nullable Object data) {
        CustomFragment customFragment;
        if (first && last) {
            if (data == null) {
                return;
            }

            this.firstPageData = data;
            this.lastPageData = this.firstPageData;
            customFragment = this.getHelperFirstFragment();
            if (customFragment == null) {
                return;
            }

            customFragment.setHelperPageData(this.firstPageData);
            customFragment = this.getHelperLastFragment();
            if (customFragment == null) {
                return;
            }

            customFragment.setHelperPageData(this.firstPageData);
        } else if (first) {
            if (data == null) {
                return;
            }

            this.firstPageData = data;
            customFragment = this.getHelperFirstFragment();
            if (customFragment == null) {
                return;
            }

            customFragment.setHelperPageData(this.firstPageData);
        } else if (last) {
            if (data == null) {
                return;
            }

            this.lastPageData = data;
            customFragment = this.getHelperLastFragment();
            if (customFragment == null) {
                return;
            }

            customFragment.setHelperPageData(this.lastPageData);
        }

    }

    @NonNull
    public final Object getPageData(boolean first, boolean last) {
        return first && last ? this.firstPageData : (first ? this.firstPageData : (last ? this.lastPageData : new Object()));
    }

    @Nullable
    public CustomPagerAdapter getAdapter() {
        try {
            return (CustomPagerAdapter) super.getAdapter();
        } catch (ClassCastException e) {
            String msg = "Please make sure to use (CustomViewPager.java)\n" +
                    "instead of (ViewPager.java)\n" +
                    "and (CustomPagerAdapter.java)\n" +
                    "instead of (FragmentPagerAdapter.java or FragmentStatePagerAdapter.java).";
            Log.e("CustomViewPager", msg);
            e.printStackTrace();
        }
        return null;
    }

    public void setAdapter(@Nullable PagerAdapter adapter) {
        try {
            super.setAdapter(adapter);
        } catch (ClassCastException e) {
            String msg = "Please make sure to use (CustomViewPager.java)\n" +
                    "instead of (ViewPager.java)\n" +
                    "and (CustomPagerAdapter.java)\n" +
                    "instead of (FragmentPagerAdapter.java or FragmentStatePagerAdapter.java).";
            Log.e("CustomViewPager", msg);
            e.printStackTrace();
        }
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item + 1, smoothScroll);
    }

    public void setCurrentItem(int item) {
        super.setCurrentItem(item + 1);
    }

    public int getCurrentItem() {
        return this.getRealPosition(super.getCurrentItem());
    }

    private int getRealPosition(int position) {
        CustomPagerAdapter adapter = this.getAdapter();
        return adapter != null ? adapter.getRealPosition(position) : 0;
    }

    public void onIndicatorClick(int position) {
        this.setCurrentItem(position, true);
    }

    public final void initIndicators() {

        ViewParent parent = this.getParent();

        if (!(parent instanceof ConstraintLayout)) {
            Log.e(TAG, "Can NOT init CustomViewPager's Indicators.\n" +
                    "Parent ConstraintLayout is null.");
            return;
        }

        ConstraintLayout parentCL = (ConstraintLayout) parent;

        this.mCustomIndicator = new CustomIndicator(this.getContext(), parentCL, this);
        this.mCustomIndicator.setIndicatorCallbacks(this);
    }

    public final void initIndicators(int position, int adjustMode) {
        this.initIndicators();
        this.setIndicatorsMode(position, adjustMode);
    }

    public final void initIndicators(int position, int adjustMode, int maxRows) {
        this.initIndicators();
        this.setIndicatorsMode(position, adjustMode, maxRows);
    }

    public final void setIndicatorsMode(int position, int adjustMode, int maxRows) {
        this.setIndicatorsMode(position, adjustMode);
        this.setMaxVisibleIndicatorRows(maxRows);
    }

    public final void setIndicatorsMode(int position, int adjustMode) {
        CustomIndicator customIndicator = this.mCustomIndicator;
        if (customIndicator != null) customIndicator.setIndicatorsMode(position, adjustMode);
    }

    public final void setMaxVisibleIndicatorRows(int maxRows) {
        CustomIndicator customIndicator = this.mCustomIndicator;
        if (customIndicator != null) customIndicator.setMaxVisibleIndicatorRows(maxRows);
    }

    private void updateIndicatorSelection(int position) {
        CustomIndicator customIndicator = this.mCustomIndicator;
        if (customIndicator != null) customIndicator.updateSelection(position);
    }

    public final void notifyDataSetChanged() {
        CustomPagerAdapter adapter = this.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        CustomIndicator customIndicator = this.mCustomIndicator;
        if (customIndicator != null) {
            Context context = this.getContext();
            customIndicator.setCount(context, this, this.getRealCount());
        }
    }

    public final void showThreePages() {
        int pages = 3;
        int margin = 40;
        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = width / pages + margin * 2 / pages;
        this.setClipChildren(true);
        this.setClipToPadding(false);
        this.setPadding(padding, margin, padding, margin);
        this.setPageMargin(margin);
    }
}

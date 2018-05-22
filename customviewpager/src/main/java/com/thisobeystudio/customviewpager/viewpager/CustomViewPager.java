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

import com.thisobeystudio.customviewpager.indicator.CustomIndicator;
import com.thisobeystudio.customviewpager.indicator.IndicatorsRecyclerViewAdapter;

import java.util.ArrayList;

public class CustomViewPager extends ViewPager
        implements IndicatorsRecyclerViewAdapter.IndicatorCallbacks {

    private static final String TAG = "CustomViewPager";

    // A flag to determine if using {@link CustomViewPagerCallbacks} or not.

    private boolean mUsingCallbacks = false;

    /**
     * Set using {@link CustomViewPagerCallbacks},
     * to update the duplicated pages data.
     * <p>Since we are duplicating real first and last pages,
     * this option should be enabled {@code true} if pages contains Interactive content such as:
     * <p>NestedScrollView, CheckBox, Spinner, EditText, etc...
     *
     * <p>Notice! That when {@code true} the placeholder Fragment must extend {@link CustomFragment}
     * , otherwise we will get a {@link ClassCastException}
     *
     * @param useCallbacks A flag to determine if using {@link CustomViewPagerCallbacks} or not.
     *                     <p>{@code default} is {@code true}
     */
    public void useHelpersCallbacks(boolean useCallbacks) {
        mUsingCallbacks = useCallbacks;
    }

    /**
     * @return {@code true} if using callbacks, {@code false} otherwise.
     * <p>{@code default} is {@code true}
     */
    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "WeakerAccess"})
    public boolean isUsingCallbacks() {
        return mUsingCallbacks;
    }

    public CustomViewPager(@NonNull Context context) {
        super(context);
        initCustomViewPagerOnPageChangeListener();
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initCustomViewPagerOnPageChangeListener();
    }

    private void initCustomViewPagerOnPageChangeListener() {
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int posOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                updateIndicatorSelection(getRealPosition(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                CustomViewPager.this.onPageScrollStateChanged(state);
            }
        });

    }

    // region OnPageChangeListener

//    private void onPageSelected() {
//        if (!isUsingCallbacks()) return;
//        updateHelperFirstPageData();
//        updateHelperLastPageData();
//    }

    // set pages
    private void onPageScrollStateChanged(int state) {
        if (state == SCROLL_STATE_IDLE && isFirstHelperPageSelected()) {
            setCurrentItem(getRealCount() - 1, false);
        } else if (state == SCROLL_STATE_IDLE && isLastHelperPageSelected()) {
            setCurrentItem(getRealFirstPageIndex() - 1, false);
        }
    }

    // endregion OnPageChangeListener

    // region Pager Fragments

    @SuppressWarnings("WeakerAccess")
    public ArrayList<Fragment> getFragments() {
        CustomPagerAdapter adapter = getAdapter();
        if (adapter == null) return null;
        return adapter.getFragments();
    }

    @SuppressWarnings("WeakerAccess")
    public Fragment getFragment(int index) {
        ArrayList<Fragment> fragments = getFragments();
        if (fragments == null || index < 0 || index >= fragments.size()) return null;
        return fragments.get(index);
    }

    @SuppressWarnings("unused")
    public Fragment getRealFirstFragment() {
        return getFragment(getRealFirstPageIndex());
    }

    @SuppressWarnings("WeakerAccess")
    public Fragment getHelperFirstFragment() {
        return getFragment(getHelperFirstPageIndex());
    }

    @SuppressWarnings("unused")
    public Fragment getRealLastFragment() {
        return getFragment(getRealLastPageIndex());
    }

    @SuppressWarnings("WeakerAccess")
    public Fragment getHelperLastFragment() {
        return getFragment(getHelperLastPageIndex());
    }

    // endregion Pager Fragments

    @SuppressWarnings("WeakerAccess")
    public int getCount() {
        if (getRealCount() <= 0) return 0; // just return 0
        return getRealCount() + 2;
    }

    public int getRealCount() {
        CustomPagerAdapter adapter = getAdapter();
        if (adapter == null || adapter.getRealCount() <= 0) return 0; // just return 0
        return adapter.getRealCount();
    }

    @Nullable
    @Override
    public CustomPagerAdapter getAdapter() {
        return (CustomPagerAdapter) super.getAdapter();
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        if (adapter == null) return;
        String msg = "PagerAdapter Not Implemented! " +
                "Please make sure to use " +
                "(CustomViewPager.java) instead of (ViewPager.java) and " +
                "(CustomPagerAdapter.java) instead of " +
                "(FragmentPagerAdapter.java or FragmentStatePagerAdapter.java).";
        throw new RuntimeException(msg);
    }

    public void setAdapter(CustomPagerAdapter adapter) {
        super.setAdapter(adapter);
    }

    @SuppressWarnings("unused")
    public final boolean isFirstRealPageSelected() {
        return super.getCurrentItem() == getRealFirstPageIndex();
    }

    @SuppressWarnings("unused")
    public final boolean isLastRealPageSelected() {
        return super.getCurrentItem() == getRealLastPageIndex();
    }

    private boolean isFirstHelperPageSelected() {
        return super.getCurrentItem() == getHelperLastPageIndex();
    }

    private boolean isLastHelperPageSelected() {
        return super.getCurrentItem() == getHelperFirstPageIndex();
    }

    @SuppressWarnings("SameReturnValue")
    private int getRealFirstPageIndex() {
        return 1;
    }

    private int getRealLastPageIndex() {
        return getRealCount();
    }

    private int getHelperFirstPageIndex() {
        return getCount() - 1;
    }

    @SuppressWarnings("SameReturnValue")
    private int getHelperLastPageIndex() {
        return 0;
    }

    /* NEW */

    // region First Page Data

    private Object mFirstPageData = null;

    public Object getFirstPageData() {
        return mFirstPageData;
    }

    @SuppressWarnings("unused")
    public void clearPagesData() {
        clearFirstPageData();
        clearLastPageData();
    }

    @SuppressWarnings("WeakerAccess")
    public void clearFirstPageData() {
        if (!isUsingCallbacks()) return;
        mFirstPageData = null;
    }

    /**
     * used to share data between first real page and first helper page.
     * null are not allowed, if you want to set it as null pleas use
     * {@link #clearFirstPageData()} or {@link #clearPagesData()} instead of passing null.
     *
     * @param callbacks source {@link CustomViewPagerCallbacks}
     */
    public void setFirstPageDataCallbacks(CustomViewPagerCallbacks callbacks) {
        if (!isUsingCallbacks() || callbacks == null || callbacks.getPageData() == null) return;
        mFirstPageData = callbacks.getPageData(); // update data
        updateHelperFirstPageData();
    }

    @SuppressWarnings("WeakerAccess")
    public void updateHelperFirstPageData() {
        if (mFirstPageData == null) return;
        Fragment firstHelperFragment = getHelperFirstFragment();
        if (firstHelperFragment != null) {
            CustomViewPagerCallbacks helperCallbacks = (CustomFragment) firstHelperFragment;
            helperCallbacks.setHelperPageData(mFirstPageData);
        }
    }
    // endregion First Page Data

    // region Last Page Data

    private Object mLastPageData = null;

    public Object getLastPageData() {
        return mLastPageData;
    }

    @SuppressWarnings("WeakerAccess")
    public void clearLastPageData() {
        if (!isUsingCallbacks()) return;
        mLastPageData = null;
    }

    /**
     * used to share data between last real page and last helper page.
     * null are not allowed, if you want to set it as null pleas use
     * {@link #clearFirstPageData()} or {@link #clearPagesData()} instead of passing null.
     *
     * @param callbacks source {@link CustomViewPagerCallbacks}
     */
    public void setLastPageDataCallbacks(CustomViewPagerCallbacks callbacks) {
        if (!isUsingCallbacks() || callbacks == null || callbacks.getPageData() == null) return;
        mLastPageData = callbacks.getPageData(); // update data
        updateHelperLastPageData();
    }

    @SuppressWarnings("WeakerAccess")
    public void updateHelperLastPageData() {
        if (mLastPageData == null) return;
        Fragment lastHelperFragment = getHelperLastFragment();
        if (lastHelperFragment != null) {
            CustomViewPagerCallbacks helperLastPageCallbacks = (CustomFragment) lastHelperFragment;
            helperLastPageCallbacks.setHelperPageData(mLastPageData);
        }
    }

    // endregion Last Page Data

    private int getRealPosition(int position) {
        CustomPagerAdapter adapter = getAdapter();
        if (adapter == null) return 0;
        return adapter.getRealPosition(position);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item + 1, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item + 1);
    }

    @Override
    public int getCurrentItem() {
        String errMsg = "ERROR! getCurrentItem() not implemented!" +
                " Please use getRealCurrentItem() instead.";
        throw new RuntimeException(errMsg);
    }

    public int getRealCurrentItem() {
        return getRealPosition(super.getCurrentItem());
    }

    // region Indicators

    private CustomIndicator mCustomIndicator;

    public void initIndicators(Context context) {

        if (context == null) {
            Log.e(TAG, "Can NOT init CustomViewPager's Indicators. Context is null.");
            return;
        }

        ConstraintLayout parent = (ConstraintLayout) getParent();

        if (parent == null) {
            Log.e(TAG, "Can NOT init CustomViewPager's Indicators. " +
                    "Parent ConstraintLayout is null.");
            return;
        }

        mCustomIndicator = new CustomIndicator(context, parent, this);

        // set the indicator callbacks for onIndicatorClick
        mCustomIndicator.setIndicatorCallbacks(this);
    }

    public void initIndicators(Context context, int position, int adjustMode) {
        initIndicators(context);
        setIndicatorsMode(position, adjustMode);
    }

    public void initIndicators(Context context, int position, int adjustMode, int maxRows) {
        initIndicators(context);
        setIndicatorsMode(position, adjustMode, maxRows);
    }

    @SuppressWarnings("WeakerAccess")
    public void setIndicatorsMode(int position, int adjustMode, int maxRows) {
        setIndicatorsMode(position, adjustMode);
        setMaxVisibleIndicatorRows(maxRows);
    }

    @SuppressWarnings("WeakerAccess")
    public void setIndicatorsMode(int position, int adjustMode) {
        if (mCustomIndicator == null) return;
        mCustomIndicator.setIndicatorsMode(position, adjustMode);
    }

    @SuppressWarnings("WeakerAccess")
    public void setMaxVisibleIndicatorRows(int maxRows) {
        if (mCustomIndicator == null) return;
        mCustomIndicator.setMaxVisibleIndicatorRows(maxRows);
    }

    private void updateIndicatorSelection(int position) {
        if (mCustomIndicator == null) return;
        mCustomIndicator.updateSelection(position);
    }

    @Override
    public void onIndicatorClick(int position) {
        setCurrentItem(position, true);
    }

    // endregion Indicators

    @SuppressWarnings("unused")
    public void notifyDataSetChanged() {
        CustomPagerAdapter adapter = getAdapter();
        if (adapter == null) return;
        adapter.notifyDataSetChanged();
        if (mCustomIndicator == null) return;
        mCustomIndicator.setCount(getContext(), this, getRealCount());
    }

    @SuppressWarnings("unused")
    public void showThreePages() {
        int pages = 3;
        int margin = 40;
        int width = getResources().getDisplayMetrics().widthPixels;
        int padding = width / pages + (margin * 2 / pages);
        setClipChildren(true);
        setClipToPadding(false);
        setPadding(padding, margin, padding, margin);
        setPageMargin(margin);
    }

}

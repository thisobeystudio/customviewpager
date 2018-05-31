package com.thisobeystudio.customviewpager.indicator;

/*
 * Created by thisobeystudio on 9/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thisobeystudio.customviewpager.R;
import com.thisobeystudio.customviewpager.viewpager.CustomViewPager;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.END;
import static android.support.constraint.ConstraintSet.START;
import static android.support.constraint.ConstraintSet.TOP;

@SuppressWarnings("WeakerAccess")
public class CustomIndicator {

    private static final String TAG = "CustomIndicator";

    private IndicatorsRecyclerViewAdapter mAdapter;

    private RecyclerView mRecyclerView;

    // if mIndicatorsHeightMode is set to WRAP, this param will be ignored.
    private int mMaxVisibleIndicatorRows = 1;

    private int mIndicatorsPositionMode = POSITION_FLOAT_BOTTOM;
    private int mIndicatorsAdjustMode = MODE_CLAMPED_HEIGHT;

    public void setIndicatorsMode(int indicatorsPositionMode, int indicatorsAdjustMode) {
        this.mIndicatorsPositionMode = indicatorsPositionMode;
        this.mIndicatorsAdjustMode = indicatorsAdjustMode;
    }

    // region CustomIndicator Options Public Params

    // not using enums
    public static final int POSITION_FLOAT_TOP = 0;
    public static final int POSITION_FLOAT_BOTTOM = 1;
    public static final int POSITION_INCLUDE_TOP = 2;
    public static final int POSITION_INCLUDE_BOTTOM = 3;

    // from 1 to infinite based on rows count
    public static final int MODE_WRAP_HEIGHT = 4;
    // itemHeight * (margin * 2) * maxVisibleIndicatorRows
    public static final int MODE_FIXED_HEIGHT = 5;
    // from 1 to maxVisibleIndicatorRows
    public static final int MODE_CLAMPED_HEIGHT = 6;

    // endregion CustomIndicator Options Public Params

    private final ConstraintLayout mParent;

    public CustomIndicator(Context context, ConstraintLayout parent, CustomViewPager viewPager) {

        if (context == null || parent == null || viewPager == null) {
            Log.e(TAG, "Can NOT init CustomIndicator. " +
                    "One or more of the Constructors parameters are null.");
            this.mParent = null;
            return;
        }

        this.mParent = parent;
        initRecyclerView(context, viewPager);
    }

    private void initRecyclerView(final Context context,
                                  final CustomViewPager viewPager) {

        if (context == null || mParent == null || viewPager == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);

        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.indicators_view, mParent, false);

        if (mRecyclerView == null) {
            Log.e(TAG, "Can NOT find a Layout named id:indicators_view.");
            return;
        }

        // set layout manager
        // initial spawn count to ONE
        final GridLayoutManager glm = new GridLayoutManager(context, 1);
        mRecyclerView.setLayoutManager(glm);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // this makes scroll smoothly
        mRecyclerView.setNestedScrollingEnabled(false);

        final int totalCount = viewPager.getRealCount();
        int selection = viewPager.getCurrentItem();

        // specify an adapter
        mAdapter = new IndicatorsRecyclerViewAdapter(context, totalCount, selection);

        // set recyclerView adapter
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.scrollToPosition(selection);

        // set recyclerView VISIBLE
        mRecyclerView.setVisibility(View.VISIBLE);

        calcItemsPerRow(context, viewPager, totalCount);
    }

    private void calcItemsPerRow(final Context context,
                                 final ViewPager viewPager,
                                 final int totalCount) {

        if (mRecyclerView == null || context == null || viewPager == null || totalCount <= 0)
            return;

        // Since the indicators container width is based on viewPagers width,
        // use post to get viewPagers real width.
        viewPager.post(new Runnable() {
            @Override
            public void run() {

                if (viewPager.getAdapter() == null) {
                    return;
                }

                int indicatorItemSize = getDimension(context, R.dimen.indicator_item_size);
                int margin = getDimension(context, R.dimen.indicator_horizontal_margin) * 2;

                final int width = viewPager.getWidth();
                final int maxPossibleWidth = width - margin;
                int maxItemsPerRow = maxPossibleWidth / indicatorItemSize;

                // this will keep indicators centered horizontally
                if (maxItemsPerRow > totalCount) maxItemsPerRow = totalCount;

                if (maxItemsPerRow < 1) return;

                // set final spawn count
                ((GridLayoutManager) mRecyclerView.getLayoutManager()).setSpanCount(maxItemsPerRow);

                updateIndicatorsContainerHeight(context,
                        indicatorItemSize,
                        totalCount,
                        maxItemsPerRow);

                if (!mRecyclerView.isAttachedToWindow()) updateConstraints(viewPager);

            }
        });
    }

    private void updateIndicatorsContainerHeight(final Context context,
                                                 final int indicatorItemSize,
                                                 final int totalCount,
                                                 final int maxItemsPerRow) {

        if (context == null || mIndicatorsAdjustMode == MODE_WRAP_HEIGHT) return;

        ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();

        if (params == null) return;

        int height;
        int padding = getDimension(context, R.dimen.indicator_vertical_padding) * 2;

        switch (mIndicatorsAdjustMode) {
            case MODE_FIXED_HEIGHT:
                height = indicatorItemSize * mMaxVisibleIndicatorRows + (padding);
                params.height = height;
                break;
            case MODE_CLAMPED_HEIGHT:
                int rows = (int) Math.ceil(totalCount / (maxItemsPerRow + 0f));
                if (rows < mMaxVisibleIndicatorRows) {
                    height = rows * indicatorItemSize + padding;
                    params.height = height;
                } else {
                    height = indicatorItemSize * mMaxVisibleIndicatorRows + (padding);
                    params.height = height;
                }
                break;
            case MODE_WRAP_HEIGHT:
            default:
                // nothing to do here, this should not be called...
                break;
        }
    }

    private int getDimension(Context context, @DimenRes int dimenID) {
        if (context == null) return 0;
        return context.getResources().getDimensionPixelOffset(dimenID);
    }

    public void setIndicatorCallbacks(
            IndicatorsRecyclerViewAdapter.IndicatorCallbacks indicatorCallbacks) {
        if (mAdapter == null || indicatorCallbacks == null) return;
        mAdapter.setIndicatorCallbacks(indicatorCallbacks);
    }

    private void updateConstraints(final ViewPager pager) {

        if (mRecyclerView == null || mParent == null || pager == null) return;

        ConstraintSet cs = new ConstraintSet();
        cs.clone(mParent);

//       TransitionManager.beginDelayedTransition(mParent);

        cs.centerHorizontally(mRecyclerView.getId(), pager.getId());
//      constraintSet.centerVertically(mRecyclerView.getId(), parent.getId());
        cs.constrainWidth(mRecyclerView.getId(), mRecyclerView.getLayoutParams().width);
        cs.constrainHeight(mRecyclerView.getId(), mRecyclerView.getLayoutParams().height);
//      constraintSet.setMargin(mRecyclerView.getId(), ConstraintSet.START, margin/2);

        switch (mIndicatorsPositionMode) {
            case POSITION_FLOAT_TOP:
                connectIndicatorsToParent(cs, TOP);
                connectIndicatorsToPager(cs, pager, TOP);
                break;
            case POSITION_FLOAT_BOTTOM:
                connectIndicatorsToParent(cs, BOTTOM);
                connectIndicatorsToPager(cs, pager, BOTTOM);
                break;
            case POSITION_INCLUDE_TOP:
                connectIndicatorsToParent(cs, TOP);
                connectPagerTopToIndicatorsBottom(cs, pager);
                break;
            case POSITION_INCLUDE_BOTTOM:
                connectIndicatorsToParent(cs, BOTTOM);
                connectPagerBottomToIndicatorsTop(cs, pager);
                break;
            default:
                Log.e(TAG, "CustomIndicators Position Mode not supported!" +
                        " Please select a valid one.");
                break;
        }

        connectIndicatorsToPager(cs, pager, START);
        connectIndicatorsToPager(cs, pager, END);

        mParent.addView(mRecyclerView);

        cs.applyTo(mParent);
    }

    private void connectIndicatorsToParent(ConstraintSet constraintSet, int pos) {
        if (constraintSet == null) return;
        constraintSet.connect(mRecyclerView.getId(), pos, mParent.getId(), pos, 0);
    }

    private void connectIndicatorsToPager(ConstraintSet constraintSet, ViewPager pager, int pos) {
        if (constraintSet == null || pager == null) return;
        constraintSet.connect(mRecyclerView.getId(), pos, pager.getId(), pos, 0);
    }

    private void connectPagerTopToIndicatorsBottom(ConstraintSet constraintSet, ViewPager pager) {
        if (constraintSet == null || pager == null) return;
        constraintSet.connect(pager.getId(), TOP, mRecyclerView.getId(), BOTTOM, 0);
    }

    private void connectPagerBottomToIndicatorsTop(ConstraintSet constraintSet, ViewPager pager) {
        if (constraintSet == null || pager == null) return;
        constraintSet.connect(pager.getId(), BOTTOM, mRecyclerView.getId(), TOP, 0);
    }

    // region Public Methods

    public void setMaxVisibleIndicatorRows(int maxVisibleIndicatorRows) {
        this.mMaxVisibleIndicatorRows = maxVisibleIndicatorRows;
    }

    public void setCount(Context context, ViewPager viewPager, int count) {
        if (mAdapter == null) return;
        calcItemsPerRow(context, viewPager, count);
        mAdapter.swapData(count);
    }

    public void updateSelection(int newSelection) {
        if (mRecyclerView == null || mAdapter == null) return;
        mRecyclerView.scrollToPosition(newSelection);
        mAdapter.updateSelection(newSelection);
    }

    // endregion Public Methods
}

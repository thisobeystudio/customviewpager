package com.thisobeystudio.customviewpager.viewpager;

/*
 * Created by thisobeystudio on 8/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * <p>
 * Implementation of {@link PagerAdapter} that
 * uses a {@link CustomFragment} to manage each page. This class also handles
 * saving and restoring of fragment's state.
 * </p>
 * <p>
 * When using FragmentPagerAdapter the host ViewPager must have a
 * valid ID set.
 * </p>
 * <p>
 * Subclasses only need to implement {@link #getItem(CustomIndexHelper)}
 * and {@link #getRealCount()} to have a working adapter.
 * </p>
 */
public abstract class CustomFragmentStatePagerAdapter extends PagerAdapter {

    private static final String TAG = "FragmentStatePagerAdapt";
    private static final boolean DEBUG = false;

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;

    private final ArrayList<Fragment.SavedState> mSavedState = new ArrayList<>();
    private final ArrayList<Fragment> mFragments = new ArrayList<>();
    private Fragment mCurrentPrimaryItem = null;

    CustomFragmentStatePagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    protected abstract Fragment getItem(CustomIndexHelper customIndexHelper);

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this
                    + " requires a view id");
        }
    }

    @SuppressLint("CommitTransaction")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        // If we already have this item instantiated, there is nothing
        // to do.  This can happen when we are restoring the entire pager
        // from its saved state, where the fragment manager has already
        // taken care of restoring the fragments we previously had instantiated.
        if (mFragments.size() > position) {
            Fragment f = mFragments.get(position);
            if (f != null) {
                return f;
            }
        }

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        final boolean isRealFirst = isRealFirst(position);
        final boolean isRealLast = isRealLast(position);
        final boolean isHelperFirst = isHelperFirst(position);
        final boolean isHelperLast = isHelperLast(position);

        CustomIndexHelper customIndexHelper = new CustomIndexHelper(position,
                getRealPosition(position),
                isRealFirst,
                isRealLast,
                isHelperFirst,
                isHelperLast);

        Fragment fragment = getItem(customIndexHelper);
        if (DEBUG) Log.v(TAG, "Adding item #" + position + ": f=" + fragment);
        if (mSavedState.size() > position) {
            Fragment.SavedState fss = mSavedState.get(position);
            if (fss != null) {
                fragment.setInitialSavedState(fss);
            }
        }
        while (mFragments.size() <= position) {
            mFragments.add(null);
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mFragments.set(position, fragment);
        mCurTransaction.add(container.getId(), fragment);

        return fragment;
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = (Fragment) object;

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        if (DEBUG) Log.v(TAG, "Removing item #" + position + ": f=" + object
                + " v=" + ((Fragment) object).getView());
        while (mSavedState.size() <= position) {
            mSavedState.add(null);
        }
        mSavedState.set(position, fragment.isAdded()
                ? mFragmentManager.saveFragmentInstanceState(fragment) : null);
        mFragments.set(position, null);

        mCurTransaction.remove(fragment);
    }

    @Override
    @SuppressWarnings({"ReferenceEquality", "ConstantConditions"})
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitNowAllowingStateLoss();
            mCurTransaction = null;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            mSavedState.toArray(fss);
            state.putParcelableArray("states", fss);
        }
        for (int i = 0; i < mFragments.size(); i++) {
            Fragment f = mFragments.get(i);
            if (f != null && f.isAdded()) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                mFragmentManager.putFragment(state, key, f);
            }
        }
        return state;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            mSavedState.clear();
            mFragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    mSavedState.add((Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment f = mFragmentManager.getFragment(bundle, key);
                    if (f != null) {
                        while (mFragments.size() <= index) {
                            mFragments.add(null);
                        }
                        f.setMenuVisibility(false);
                        mFragments.set(index, f);
                    } else {
                        Log.w(TAG, "Bad fragment at key " + key);
                    }
                }
            }
        }
    }

    /**
     * @return The real count of pages excluding the two extra pages. (first and last)
     */
    @SuppressWarnings("SameReturnValue")
    public abstract int getRealCount();

    /**
     * <p>Do NOT use {@link #getCount()} to set the pages count,
     * use {@link #getRealCount()} instead.</p>
     * <p>If using it, make sure to set it as follows: <code>return super.getCount();</code></p>
     * <p>Otherwise, first and last pages will be ignored, so they wont be available.</p>
     *
     * @return The total count of pages including the two (first and last) extra pages.
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public int getCount() {
        int realCount = getRealCount();
        if (realCount <= 0) return 0;
        return realCount + 2;
    }

    int getRealPosition(int pagerPosition) {
        int realCount = getRealCount();
        // last page to first position.
        if (pagerPosition == 0) {
            return realCount - 1;
        }
        // first page to last position.
        if (pagerPosition == realCount + 1) {
            return 0;
        }
        return pagerPosition - 1;
    }

    ArrayList<Fragment> getFragments() {
        return mFragments;
    }

    private boolean isRealFirst(int index) {
        return index == 1;
    }

    private boolean isRealLast(int index) {
        return index == getCount() - 2;
    }

    private boolean isHelperFirst(int index) {
        return index == getCount() - 1;
    }

    private boolean isHelperLast(int index) {
        return index == 0;
    }
}
package com.thisobeystudio.customviewpager.models;

/*
 * Created by thisobeystudio on 8/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simple class to store helper data.
 * <b>Useful methods:</b>
 * {@link #getPagerPosition()}
 * {@link #getDataPosition()}
 * {@link #isRealFirst()}
 * {@link #isRealLast()}
 * {@link #isHelperFirst()}
 * {@link #isHelperLast()}
 */
public class CustomIndexHelper implements Parcelable {

    private final int mPagerPosition;
    private final int mDataPosition;
    private final boolean isRealFirst;
    private final boolean isRealLast;
    private final boolean isHelperFirst;
    private final boolean isHelperLast;

    public CustomIndexHelper(int pagerPosition,
                             int dataPosition,
                             boolean isRealFirst,
                             boolean isRealLast,
                             boolean isHelperFirst,
                             boolean isHelperLast) {
        this.mPagerPosition = pagerPosition;
        this.mDataPosition = dataPosition;
        this.isRealFirst = isRealFirst;
        this.isRealLast = isRealLast;
        this.isHelperFirst = isHelperFirst;
        this.isHelperLast = isHelperLast;
    }

    private CustomIndexHelper(Parcel in) {
        mPagerPosition = in.readInt();
        mDataPosition = in.readInt();
        isRealFirst = in.readByte() != 0;
        isRealLast = in.readByte() != 0;
        isHelperFirst = in.readByte() != 0;
        isHelperLast = in.readByte() != 0;
    }

    public static final Creator<CustomIndexHelper> CREATOR = new Creator<CustomIndexHelper>() {
        @Override
        public CustomIndexHelper createFromParcel(Parcel in) {
            return new CustomIndexHelper(in);
        }

        @Override
        public CustomIndexHelper[] newArray(int size) {
            return new CustomIndexHelper[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mPagerPosition);
        dest.writeInt(mDataPosition);
        dest.writeByte((byte) (isRealFirst ? 1 : 0));
        dest.writeByte((byte) (isRealLast ? 1 : 0));
        dest.writeByte((byte) (isHelperFirst ? 1 : 0));
        dest.writeByte((byte) (isHelperLast ? 1 : 0));
    }

    public int getPagerPosition() {
        return mPagerPosition;
    }

    public int getDataPosition() {
        return mDataPosition;
    }

    boolean isRealFirst() {
        return isRealFirst;
    }

    boolean isRealLast() {
        return isRealLast;
    }

    boolean isHelperFirst() {
        return isHelperFirst;
    }

    boolean isHelperLast() {
        return isHelperLast;
    }
}
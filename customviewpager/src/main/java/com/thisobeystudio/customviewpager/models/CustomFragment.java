package com.thisobeystudio.customviewpager.models;

/*
 * Created by thisobeystudio on 8/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * A Simple {@link Fragment} to be used as 'root' when using complex views
 */
public abstract class CustomFragment extends Fragment {

    private static final String ARG_CUSTOM_INDEX_HELPER = "custom_index_helper";

    private CustomIndexHelper customIndexHelper;

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCustomIndexHelper(this.getArguments());
    }

    public abstract void setHelperPageData(@NonNull Object data);

    protected final int getPageIndex() {
        CustomIndexHelper customIndexHelper = this.customIndexHelper;
        if (this.customIndexHelper == null) return 0;
        return customIndexHelper.getPagerPosition();
    }

    protected final int getDataIndex() {
        CustomIndexHelper customIndexHelper = this.customIndexHelper;
        if (this.customIndexHelper == null) return 0;
        return customIndexHelper.getDataPosition();
    }

    protected final boolean isHelperFirst() {
        CustomIndexHelper customIndexHelper = this.customIndexHelper;
        return this.customIndexHelper != null && customIndexHelper.isHelperFirst();
    }

    protected final boolean isRealFirst() {
        CustomIndexHelper customIndexHelper = this.customIndexHelper;
        return this.customIndexHelper != null && customIndexHelper.isRealFirst();
    }

    protected final boolean isHelperLast() {
        CustomIndexHelper customIndexHelper = this.customIndexHelper;
        return this.customIndexHelper != null && customIndexHelper.isHelperLast();
    }

    protected final boolean isRealLast() {
        CustomIndexHelper customIndexHelper = this.customIndexHelper;
        return customIndexHelper != null && customIndexHelper.isRealLast();
    }

    private void setCustomIndexHelper(Bundle args) {
        if (args == null || !args.containsKey(ARG_CUSTOM_INDEX_HELPER)) return;
        this.customIndexHelper = args.getParcelable(ARG_CUSTOM_INDEX_HELPER);
    }
}

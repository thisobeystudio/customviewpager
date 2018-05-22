package com.thisobeystudio.customviewpager.viewpager;

/*
 * Created by thisobeystudio on 8/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

/**
 * A simple interface to share data between real and helper pages.
 */
public interface CustomViewPagerCallbacks {
    Object getPageData();

    void setHelperPageData(Object data);
}

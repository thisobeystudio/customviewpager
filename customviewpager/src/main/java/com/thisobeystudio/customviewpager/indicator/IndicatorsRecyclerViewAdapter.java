package com.thisobeystudio.customviewpager.indicator;

/*
 * Created by thisobeystudio on 9/5/18.
 * Copyright: (c) 2018 ThisObey Studio
 * Contact: thisobeystudio@gmail.com
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.thisobeystudio.customviewpager.R;

public class IndicatorsRecyclerViewAdapter extends
        RecyclerView.Adapter<IndicatorsRecyclerViewAdapter.IndicatorViewHolder> {

    private final Context mContext;
    private int mCount;
    private int mSelection;

    class IndicatorViewHolder extends RecyclerView.ViewHolder {

        final ImageView indicatorImage;

        IndicatorViewHolder(View itemView) {
            super(itemView);

            indicatorImage = itemView.findViewById(R.id.indicators_image);
        }
    }

    IndicatorsRecyclerViewAdapter(Context context, int count, int selection) {
        this.mContext = context;
        this.mCount = count;
        this.mSelection = selection;
    }

    @Override
    public int getItemCount() {
        if (this.mCount < 0) return 0;
        return this.mCount;
    }

    @NonNull
    @Override
    public IndicatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View rowView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.indicator_item, parent, false);
        return new IndicatorViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(@NonNull final IndicatorViewHolder viewHolder, int position) {

        if (mContext == null) return;

        viewHolder.indicatorImage.setSelected(position == mSelection);

        final int finalPosition = position;
        viewHolder.indicatorImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.indicatorImage.setSelected(true);
                notifyItemChanged(mSelection);

                if (mIndicatorCallbacks != null)
                    mIndicatorCallbacks.onIndicatorClick(finalPosition);
            }
        });
    }

    void updateSelection(int newSelection) {
        notifyItemChanged(mSelection);
        mSelection = newSelection;
        notifyItemChanged(mSelection);
    }

    public interface IndicatorCallbacks {
        void onIndicatorClick(int position);
    }

    private IndicatorCallbacks mIndicatorCallbacks;

    // sets indicator click callback
    void setIndicatorCallbacks(IndicatorCallbacks callbacks) {
        this.mIndicatorCallbacks = callbacks;
    }

    void swapData(int newCount) {
        if (newCount < 0) newCount = 0;
        mCount = newCount;
        notifyDataSetChanged();
    }
}

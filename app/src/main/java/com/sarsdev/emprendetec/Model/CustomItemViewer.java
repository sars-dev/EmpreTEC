package com.sarsdev.emprendetec.Model;

import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.sarsdev.emprendetec.R;

public class CustomItemViewer extends DefaultItemAnimator {
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return super.animateRemove(holder);
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(
                holder.itemView.getContext(),
                R.anim.viewholder_add_anim
        ));
        return super.animateAdd(holder);
    }

    @Override
    public long getAddDuration() {
        return 500;
    }
}

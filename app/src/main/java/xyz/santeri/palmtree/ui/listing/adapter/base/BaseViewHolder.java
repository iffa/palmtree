package xyz.santeri.palmtree.ui.listing.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Santeri Elo
 */

public abstract class BaseViewHolder<M> extends RecyclerView.ViewHolder {
    public static int TYPE_VIDEO = 1;

    public static int TYPE_IMAGE = 2;

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(RecyclerView.Adapter adapter, M item);
}

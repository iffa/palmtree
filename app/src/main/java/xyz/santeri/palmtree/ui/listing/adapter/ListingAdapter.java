package xyz.santeri.palmtree.ui.listing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseAdapter;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseViewHolder;

/**
 * TODO: The view holders might get bloated, so somehow combine the two so new functionality is quick and easy to implement.
 *
 * @author Santeri Elo
 */
public class ListingAdapter extends BaseAdapter<ImageDetails> {
    @Override
    public BaseViewHolder<ImageDetails> onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        final BaseViewHolder<ImageDetails> viewHolder;

        if (viewType == BaseViewHolder.TYPE_IMAGE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(ImageViewHolder.LAYOUT_RES, parent, false);
            viewHolder = new ImageViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(ThumbnailViewHolder.LAYOUT_RES, parent, false);
            viewHolder = new ThumbnailViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        switch (items.get(position).type()) {
            case IMAGE:
                return BaseViewHolder.TYPE_IMAGE;
            case VIDEO:
                return BaseViewHolder.TYPE_VIDEO;
            default:
                return BaseViewHolder.TYPE_IMAGE;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<ImageDetails> holder, int position) {
        holder.bind(this, items.get(position));
    }
}

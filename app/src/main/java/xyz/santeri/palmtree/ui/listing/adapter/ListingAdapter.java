package xyz.santeri.palmtree.ui.listing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ImageType;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseAdapter;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseViewHolder;

/**
 * @author Santeri Elo
 */
public class ListingAdapter extends BaseAdapter<ImageDetails, BaseViewHolder> {
    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        final BaseViewHolder viewHolder;

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
        return items.get(position).type().equals(ImageType.IMAGE) ? BaseViewHolder.TYPE_IMAGE : BaseViewHolder.TYPE_VIDEO;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(this, items.get(position));
    }
}

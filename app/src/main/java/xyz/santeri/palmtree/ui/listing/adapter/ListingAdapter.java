package xyz.santeri.palmtree.ui.listing.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseAdapter;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseViewHolder;
import xyz.santeri.palmtree.ui.listing.adapter.base.HolderItemType;

/**
 * @author Santeri Elo
 */
public class ListingAdapter extends BaseAdapter<ImageDetails> {
    private final boolean dataSaving;

    public ListingAdapter(boolean dataSaving) {
        this.dataSaving = dataSaving;
    }

    @Override
    public BaseViewHolder<ImageDetails> onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view;
        final BaseViewHolder<ImageDetails> viewHolder;

        view = LayoutInflater.from(parent.getContext())
                .inflate(ImageViewHolder.LAYOUT_RES, parent, false);
        viewHolder = new ImageViewHolder(view, dataSaving);

        return viewHolder;
    }

    @Override
    public
    @HolderItemType
    int getItemViewType(int position) {
        switch (items.get(position).type()) {
            case IMAGE:
                return HolderItemType.TYPE_IMAGE;
            case VIDEO:
                return HolderItemType.TYPE_VIDEO;
            case UNDEFINED:
                return HolderItemType.TYPE_IMAGE;
            default:
                return HolderItemType.TYPE_IMAGE;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<ImageDetails> holder, int position) {
        holder.bind(this, items.get(position), getItemViewType(position));
    }
}

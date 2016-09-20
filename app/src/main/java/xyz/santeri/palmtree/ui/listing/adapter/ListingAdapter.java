package xyz.santeri.palmtree.ui.listing.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ImageType;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseViewHolder;

/**
 * @author Santeri Elo
 */
public class ListingAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<ImageDetails> items = new ArrayList<>();

    public ListingAdapter() {
        super();
        setHasStableIds(true);
    }

    public void addItem(ImageDetails imageDetails) {
        items.add(imageDetails);
        notifyItemInserted(items.indexOf(imageDetails));
    }

    public void addItems(List<ImageDetails> newItems) {
        items = newItems;
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

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

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ImageDetails getItemAt(Integer position) {
        return items.get(position);
    }
}

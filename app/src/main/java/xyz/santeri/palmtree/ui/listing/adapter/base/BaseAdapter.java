package xyz.santeri.palmtree.ui.listing.adapter.base;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Santeri Elo
 */
public abstract class BaseAdapter<M> extends RecyclerView.Adapter<BaseViewHolder<M>> {
    protected ArrayList<M> items = new ArrayList<>();

    public BaseAdapter() {
        super();
        setHasStableIds(true);
    }

    public void addItem(M item) {
        items.add(item);
        notifyItemInserted(items.indexOf(item));
    }

    public void addItems(List<M> newItems) {
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public M getItemAt(int position) {
        return items.get(position);
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

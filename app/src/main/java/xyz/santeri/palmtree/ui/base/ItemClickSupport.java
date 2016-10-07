package xyz.santeri.palmtree.ui.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import xyz.santeri.palmtree.R;

/**
 * Handles item click listeners for {@link RecyclerView} in an easy way.
 * <p>
 * Source: http://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
 */
public class ItemClickSupport {
    private final RecyclerView recyclerView;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
                itemClickListener.onItemClicked(recyclerView, holder.getAdapterPosition(), v);
            }
        }
    };
    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (itemLongClickListener != null) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
                return itemLongClickListener.onItemLongClicked(
                        recyclerView, holder.getAdapterPosition(), v);
            }
            return false;
        }
    };
    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (itemClickListener != null) {
                view.setOnClickListener(clickListener);
            }
            if (itemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };

    private ItemClickSupport(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.recyclerView.setTag(R.id.item_click_support, this);
        this.recyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    public static ItemClickSupport addTo(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }

    public static ItemClickSupport removeFrom(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support != null) {
            support.detach(view);
        }
        return support;
    }

    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        itemClickListener = listener;
        return this;
    }

    public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
        itemLongClickListener = listener;
        return this;
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_support, null);
    }

    public interface OnItemClickListener {

        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
    }
}
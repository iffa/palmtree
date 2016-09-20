package xyz.santeri.palmtree.ui.listing.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.lid.lib.LabelImageView;

import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseViewHolder;

/**
 * @author Santeri Elo
 */
class ImageViewHolder extends BaseViewHolder {
    static final int LAYOUT_RES = R.layout.item_listing;

    private final TextView title;
    private final LabelImageView image;
    private final RequestManager requestManager;

    ImageViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.title);
        image = (LabelImageView) itemView.findViewById(R.id.image);

        requestManager = Glide.with(itemView.getContext());
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, ImageDetails item) {
        image.setLabelVisual(false);
        title.setText(item.title());

        requestManager.load(item.fileUrl())
                .centerCrop()
                .into(image);
    }
}

package xyz.santeri.palmtree.ui.listing.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lid.lib.LabelImageView;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import timber.log.Timber;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseViewHolder;

/**
 * @author Santeri Elo
 */
class ImageViewHolder extends BaseViewHolder<ImageDetails> {
    static final int LAYOUT_RES = R.layout.item_listing;

    private final TextView title;
    private final LabelImageView image;
    private final MaterialProgressBar progressBar;
    private final RequestManager requestManager;

    ImageViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.title);
        image = (LabelImageView) itemView.findViewById(R.id.image);
        progressBar = (MaterialProgressBar) itemView.findViewById(R.id.progress);

        requestManager = Glide.with(itemView.getContext());
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, ImageDetails item) {
        progressBar.setVisibility(View.VISIBLE);
        image.setLabelVisual(false);
        title.setText(item.title());

        requestManager.load(item.fileUrl())
                .centerCrop()
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        Timber.e(e, "Failed to load image");
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(image);
    }
}

package xyz.santeri.palmtree.ui.listing.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lid.lib.LabelImageView;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import wseemann.media.FFmpegMediaMetadataRetriever;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.ui.listing.adapter.base.BaseViewHolder;
import xyz.santeri.palmtree.ui.listing.adapter.base.HolderItemType;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author Santeri Elo
 */
class ImageViewHolder extends BaseViewHolder<ImageDetails> {
    static final int LAYOUT_RES = R.layout.item_listing;

    private boolean dataSaving;
    private boolean fullPreviews;
    private final ViewGroup imageFrame;
    private final TextView title;
    private final TextView description;
    private final LabelImageView image;
    private final MaterialProgressBar progressBar;
    private final RequestManager requestManager;

    private ImageViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        image = (LabelImageView) itemView.findViewById(R.id.image);
        progressBar = (MaterialProgressBar) itemView.findViewById(R.id.progress);
        imageFrame = (ViewGroup) itemView.findViewById(R.id.image_frame);

        requestManager = Glide.with(itemView.getContext());
    }

    ImageViewHolder(View itemView, boolean dataSaving, boolean fullPreviews) {
        this(itemView);

        this.dataSaving = dataSaving;
        this.fullPreviews = fullPreviews;

        if (dataSaving) {
            imageFrame.setVisibility(View.GONE);
        }

        if (fullPreviews) {
            image.getLayoutParams().height = WRAP_CONTENT;
        } else {
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            image.getLayoutParams().height = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 200, image.getResources().getDisplayMetrics());
        }
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, ImageDetails item, @HolderItemType int type) {
        title.setText(item.title());

        if (item.description() == null) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(item.description());
            description.setVisibility(View.VISIBLE);
        }

        if (dataSaving) { // Goodbye
            return;
        }

        if (type == HolderItemType.TYPE_IMAGE) {
            image.setLabelVisual(false);

            DrawableRequestBuilder<String> load = requestManager.load(item.fileUrl());
            if (fullPreviews) {
                load = load.fitCenter();
            } else {
                load = load.centerCrop();
            }

            load.listener(new RequestListener<String, GlideDrawable>() {
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
            }).into(image);
        } else {
            // TODO: Use Toro again when full previews is enabled?
            image.setLabelVisual(true);
            getVideoThumbnail(item.fileUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            bitmap -> {
                                image.setImageBitmap(bitmap);
                                progressBar.setVisibility(View.GONE);
                            },
                            throwable -> {
                                Timber.e(throwable, "Failed to load thumbnail for video");
                                progressBar.setVisibility(View.GONE);
                            });
        }
    }

    /**
     * Loads a thumbnail for a video.
     *
     * @param path Path to video
     * @return {@link Single} emitting a thumbnail
     */
    private Single<Bitmap> getVideoThumbnail(String path) {
        return Single.create(subscriber -> {
            Bitmap bitmap = null;

            FFmpegMediaMetadataRetriever fmmr = new FFmpegMediaMetadataRetriever();

            try {
                fmmr.setDataSource(path);

                final byte[] data = fmmr.getEmbeddedPicture();

                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                }

                if (bitmap == null) {
                    bitmap = fmmr.getFrameAtTime();
                }
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                fmmr.release();
            }

            subscriber.onSuccess(bitmap);
        });
    }
}

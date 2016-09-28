package xyz.santeri.palmtree.ui.listing.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

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

/**
 * @author Santeri Elo
 */
class ThumbnailViewHolder extends BaseViewHolder<ImageDetails> {
    static final int LAYOUT_RES = R.layout.item_listing;

    private final TextView title;
    private final TextView description;
    private final LabelImageView image;
    private final MaterialProgressBar progressBar;

    ThumbnailViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        image = (LabelImageView) itemView.findViewById(R.id.image);
        progressBar = (MaterialProgressBar) itemView.findViewById(R.id.progress);
    }


    @Override
    public void bind(RecyclerView.Adapter adapter, ImageDetails item) {
        progressBar.setVisibility(View.VISIBLE);
        title.setText(item.title());

        if (item.description() == null) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(item.description());
            description.setVisibility(View.VISIBLE);
        }

        getVideoThumbnail(item.fileUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bitmap -> {
                            image.setImageBitmap(bitmap);
                            image.setLabelVisual(true);
                            progressBar.setVisibility(View.GONE);
                        },
                        throwable -> {
                            Timber.e(throwable, "Failed to load thumbnail for video");
                            progressBar.setVisibility(View.GONE);
                        });
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

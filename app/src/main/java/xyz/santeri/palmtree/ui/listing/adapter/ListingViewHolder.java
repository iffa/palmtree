package xyz.santeri.palmtree.ui.listing.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import im.ene.lab.toro.ToroPlayer;
import im.ene.lab.toro.ToroPlayerViewHelper;
import im.ene.lab.toro.ToroUtil;
import im.ene.lab.toro.ToroViewHolder;
import im.ene.lab.toro.media.Cineer;
import im.ene.lab.toro.media.PlaybackException;
import im.ene.lab.toro.player.ExoVideo;
import im.ene.lab.toro.player.widget.ToroVideoView;
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
class ListingViewHolder extends BaseViewHolder<ImageDetails> implements ToroPlayer, ToroViewHolder {
    static final int LAYOUT_RES = R.layout.item_listing;

    private boolean dataSaving;
    private boolean fullPreviews;

    private boolean isPlayable = false;
    private final Cineer.Player videoPlayer;

    private final ViewGroup imageFrame;
    private final TextView title;
    private final TextView description;
    private final TextView error;
    private final LabelImageView image;
    private final ToroVideoView video;
    private final MaterialProgressBar progressBar;
    private final RequestManager requestManager;
    private final ToroPlayerViewHelper helper;

    private ListingViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        image = (LabelImageView) itemView.findViewById(R.id.image);
        video = (ToroVideoView) itemView.findViewById(R.id.video);
        progressBar = (MaterialProgressBar) itemView.findViewById(R.id.progress);
        imageFrame = (ViewGroup) itemView.findViewById(R.id.image_frame);
        error = (TextView) itemView.findViewById(R.id.error);

        requestManager = Glide.with(itemView.getContext());

        helper = new ToroPlayerViewHelper(this, itemView);

        if (getPlayerView() instanceof Cineer.Player) {
            videoPlayer = (Cineer.Player) getPlayerView();
        } else {
            throw new IllegalArgumentException("Illegal video player widget, Cineer.Player is required");
        }

        videoPlayer.setOnPlayerStateChangeListener(helper);
    }

    ListingViewHolder(View itemView, boolean dataSaving, boolean fullPreviews) {
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

            video.setVisibility(View.GONE);
        }
    }

    @Override
    public void bind(RecyclerView.Adapter adapter, ImageDetails item, @HolderItemType int type) {
        title.setText(item.title());
        error.setVisibility(View.GONE);

        if (item.description() == null) {
            description.setVisibility(View.GONE);
        } else {
            description.setText(item.description());
            description.setVisibility(View.VISIBLE);
        }

        if (dataSaving) { // Goodbye
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (type == HolderItemType.TYPE_IMAGE) {
            video.setVisibility(View.GONE);

            if (item.nsfw()) {
                image.setLabelVisual(true);
                image.setLabelBackgroundColor(
                        ContextCompat.getColor(image.getContext(), R.color.primary));
                image.setLabelText(image.getResources().getString(R.string.badge_nsfw));
            } else {
                image.setLabelVisual(false);
            }

            DrawableRequestBuilder<String> load = requestManager.load(item.fileUrl());
            if (fullPreviews) {
                load = load.fitCenter();
            } else {
                load = load.centerCrop();
            }

            load.listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e,
                                           String model,
                                           Target<GlideDrawable> target,
                                           boolean isFirstResource) {
                    Timber.e(e, "Failed to load image");
                    progressBar.setVisibility(View.GONE);
                    error.setVisibility(View.VISIBLE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource,
                                               String model,
                                               Target<GlideDrawable> target,
                                               boolean isFromMemoryCache,
                                               boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }
            }).into(image);
        } else {
            if (fullPreviews) {
                video.setVisibility(View.VISIBLE);
                image.setLabelVisual(false);
                videoPlayer.setMedia(
                        new ExoVideo(Uri.parse(item.fileUrl()), String.valueOf(item.id())));
            } else {
                video.setVisibility(View.GONE);

                image.setLabelBackgroundColor(
                        ContextCompat.getColor(image.getContext(), R.color.accent));
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

    @Override
    public void preparePlayer(boolean playWhenReady) {
        videoPlayer.preparePlayer(playWhenReady);
    }

    @Override
    public void start() {
        videoPlayer.start();
    }

    @Override
    public void pause() {
        videoPlayer.pause();
    }

    @Override
    public void stop() {
        videoPlayer.stop();
    }

    @Override
    public void releasePlayer() {
        videoPlayer.releasePlayer();
    }

    @Override
    public long getDuration() {
        return videoPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        return videoPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(long pos) {
        videoPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return videoPlayer.isPlaying();
    }

    @Override
    public boolean wantsToPlay() {
        return isPlayable && visibleAreaOffset() >= 0.85;
    }

    @Override
    public boolean isLoopAble() {
        return true;
    }

    @Override
    public float visibleAreaOffset() {
        return ToroUtil.visibleAreaOffset(this, itemView.getParent());
    }

    @Nullable
    @Override
    public String getMediaId() {
        return null;
    }

    @Override
    public int getPlayOrder() {
        return getAdapterPosition();
    }

    @NonNull
    @Override
    public View getPlayerView() {
        return video;
    }

    @Override
    public void onActivityActive() {
    }

    @Override
    public void onActivityInactive() {
    }

    @Override
    public void onVideoPreparing() {
    }

    @Override
    public void onVideoPrepared(Cineer mp) {
        this.isPlayable = true;
    }

    @Override
    public void onPlaybackStarted() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPlaybackPaused() {
    }

    @Override
    public void onPlaybackCompleted() {
        this.isPlayable = false;
    }

    @Override
    public boolean onPlaybackError(Cineer mp, PlaybackException error) {
        this.isPlayable = false;
        return true;
    }

    @Override
    public void onAttachedToParent() {
        helper.onAttachedToParent();
    }

    @Override
    public void onDetachedFromParent() {
        helper.onDetachedFromParent();
    }
}

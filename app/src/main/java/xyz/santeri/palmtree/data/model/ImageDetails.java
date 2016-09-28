package xyz.santeri.palmtree.data.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * @author Santeri Elo
 */
@AutoValue
public abstract class ImageDetails implements Parcelable {
    /**
     * @return File id, e.g. 79085
     */
    public abstract int id();

    /**
     * @return File URL
     */
    public abstract String fileUrl();

    /**
     * @return {@link ImageType}
     */
    public abstract ImageType type();

    /**
     * @return True if image is NSFW (not safe for work)
     */
    public abstract boolean nsfw();

    /**
     * @return File title
     */
    public abstract String title();

    /**
     * @return File rating, e.g. +123
     */
    @Nullable
    public abstract String rating(); // Don't need it as an integer

    /**
     * @return File description
     */
    @Nullable
    public abstract String description();

    /**
     * @return Metadata, e.g. sender & date
     */
    @Nullable
    public abstract String metadata();

    public static ImageDetails create(int id,
                                      String fileUrl,
                                      ImageType type,
                                      boolean nsfw,
                                      String title,
                                      @Nullable String rating,
                                      @Nullable String description,
                                      @Nullable String metadata) {
        return new AutoValue_ImageDetails(id, fileUrl, type, nsfw, title, rating, description, metadata);
    }
}

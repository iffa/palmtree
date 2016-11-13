package xyz.santeri.palmtree.data.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/**
 * @author Santeri Elo
 */
@AutoValue
public abstract class ImageDetails implements Parcelable {
    public abstract int id();

    public abstract String fileUrl();

    public abstract ImageType type();

    public abstract boolean nsfw();

    public abstract String title();

    @Nullable
    public abstract String rating();

    @Nullable
    public abstract String description();

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

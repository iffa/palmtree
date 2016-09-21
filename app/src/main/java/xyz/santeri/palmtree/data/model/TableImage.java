package xyz.santeri.palmtree.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * Model class used for items that are from a table listing (LATEST_VIDEOs, LATEST_ALL).
 *
 * @author Santeri Elo
 */
@AutoValue
public abstract class TableImage implements Parcelable {
    /**
     * @return File id, e.g. 79085
     */
    public abstract int id();

    /**
     * @return File URL
     */
    public abstract String thumbnailUrl();

    /**
     * @return True if image is NSFW (not safe for work)
     */
    public abstract boolean nsfw();

    /**
     * @return File title
     */
    public abstract String title();

    public static TableImage create(int id, String thumbnailUrl, boolean nsfw, String title) {
        return new AutoValue_TableImage(id, thumbnailUrl, nsfw, title);
    }
}

package xyz.santeri.palmtree.ui.listing.adapter.base;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static xyz.santeri.palmtree.ui.listing.adapter.base.HolderItemType.TYPE_IMAGE;
import static xyz.santeri.palmtree.ui.listing.adapter.base.HolderItemType.TYPE_VIDEO;

/**
 * @author Santeri Elo
 */
@Retention(RetentionPolicy.CLASS)
@IntDef({TYPE_VIDEO, TYPE_IMAGE})
public @interface HolderItemType {
    int TYPE_VIDEO = 1;

    int TYPE_IMAGE = 2;
}

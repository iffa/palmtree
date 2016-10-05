package xyz.santeri.palmtree.ui.dialog;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_CHANGELOG;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_CONFIRM_EXIT;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_ERROR_INFO;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_IMAGE_INFO;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_LICENSES;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_LISTING_QUALITY;

/**
 * @author Santeri Elo
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({DIALOG_CHANGELOG, DIALOG_LISTING_QUALITY, DIALOG_LICENSES,
        DIALOG_CONFIRM_EXIT, DIALOG_IMAGE_INFO, DIALOG_ERROR_INFO})
public @interface DialogType {
    int DIALOG_CHANGELOG = 0;
    int DIALOG_LISTING_QUALITY = 1;
    int DIALOG_LICENSES = 2;
    int DIALOG_CONFIRM_EXIT = 3;
    int DIALOG_IMAGE_INFO = 4;
    int DIALOG_ERROR_INFO = 5;
}

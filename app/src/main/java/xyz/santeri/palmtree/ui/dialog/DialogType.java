package xyz.santeri.palmtree.ui.dialog;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static xyz.santeri.palmtree.ui.dialog.DialogFactory.DIALOG_CHANGELOG;
import static xyz.santeri.palmtree.ui.dialog.DialogFactory.DIALOG_CONFIRM_EXIT;
import static xyz.santeri.palmtree.ui.dialog.DialogFactory.DIALOG_IMAGE_INFO;
import static xyz.santeri.palmtree.ui.dialog.DialogFactory.DIALOG_LICENSES;
import static xyz.santeri.palmtree.ui.dialog.DialogFactory.DIALOG_LISTING_QUALITY;

/**
 * @author Santeri Elo
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({DIALOG_CHANGELOG, DIALOG_LISTING_QUALITY, DIALOG_LICENSES, DIALOG_CONFIRM_EXIT, DIALOG_IMAGE_INFO})
@interface DialogType {
}

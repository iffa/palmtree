package xyz.santeri.palmtree.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import xyz.santeri.palmtree.BuildConfig;
import xyz.santeri.palmtree.R;

/**
 * @author Santeri Elo
 */
public class DialogFactory extends DialogFragment {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DIALOG_CHANGELOG, DIALOG_LISTING_QUALITY})
    public @interface DialogType {
    }

    public static final int DIALOG_CHANGELOG = 0;
    public static final int DIALOG_LISTING_QUALITY = 1;
    private static final String ARG_DIALOG_TYPE = "dialog_type";

    public static DialogFactory newInstance(@DialogType int dialogType) {
        DialogFactory fragment = new DialogFactory();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_TYPE, dialogType);

        fragment.setArguments(args);

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @DialogType int dialogType = getArguments().getInt(ARG_DIALOG_TYPE, DIALOG_CHANGELOG);

        switch (dialogType) {
            case DIALOG_CHANGELOG:
                return new MaterialDialog.Builder(getActivity())
                        .canceledOnTouchOutside(false)
                        .title(getString(R.string.changelog_title, BuildConfig.VERSION_NAME))
                        .content(R.string.changelog)
                        .positiveText(R.string.ok)
                        .build();
            case DIALOG_LISTING_QUALITY:
                return new MaterialDialog.Builder(getActivity())
                        .canceledOnTouchOutside(false)
                        .title(R.string.dialog_listing_quality_title)
                        .content(R.string.dialog_listing_quality_content)
                        .positiveText(R.string.ok)
                        .build();
            default:
                throw new UnsupportedOperationException("Invalid dialog type specified");
        }
    }
}
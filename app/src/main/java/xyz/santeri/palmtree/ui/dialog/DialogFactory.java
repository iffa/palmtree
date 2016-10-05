package xyz.santeri.palmtree.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.BuildConfig;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.ui.main.MainActivity;

import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_CHANGELOG;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_CONFIRM_EXIT;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_ERROR_INFO;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_IMAGE_INFO;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_LICENSES;
import static xyz.santeri.palmtree.ui.dialog.DialogType.DIALOG_LISTING_QUALITY;

/**
 * @author Santeri Elo
 */
public class DialogFactory extends DialogFragment {
    private static final String ARG_DIALOG_TYPE = "dialog_type";
    private static final String ARG_IMAGE = "image";
    private static final String ARG_ERROR = "error";

    public static DialogFactory newInstance(@DialogType int dialogType) {
        if (dialogType == DialogType.DIALOG_IMAGE_INFO) {
            throw new UnsupportedOperationException("Use constructor with ImageDetails if showing dialog of DIALOG_IMAGE_INFO type");
        } else if (dialogType == DialogType.DIALOG_ERROR_INFO) {
            throw new UnsupportedOperationException("Use constructor with Throwable if showing dialog of DIALOG_ERROR_INFO type");
        }

        DialogFactory fragment = new DialogFactory();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_TYPE, dialogType);

        fragment.setArguments(args);

        return fragment;
    }

    public static DialogFactory newImageDialogInstance(ImageDetails imageDetails) {
        DialogFactory fragment = new DialogFactory();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_TYPE, DialogType.DIALOG_IMAGE_INFO);
        args.putParcelable(ARG_IMAGE, imageDetails);

        fragment.setArguments(args);

        return fragment;
    }

    public static DialogFactory newErrorDialogInstance(String errorMessage) {
        DialogFactory fragment = new DialogFactory();

        Bundle args = new Bundle();
        args.putInt(ARG_DIALOG_TYPE, DialogType.DIALOG_ERROR_INFO);
        args.putString(ARG_ERROR, errorMessage);

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
            case DIALOG_CONFIRM_EXIT:
                return new MaterialDialog.Builder(getActivity())
                        .title(R.string.dialog_confirm_exit)
                        .content(R.string.dialog_confirm_exit_content)
                        .positiveText(R.string.exit)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) -> startActivity(MainActivity.getStartIntent(getContext(), true, true)))
                        .build();
            case DIALOG_IMAGE_INFO:
                ImageDetails image = getArguments().getParcelable(ARG_IMAGE);

                assert image != null;
                return new MaterialDialog.Builder(getActivity())
                        .title(image.title())
                        .content(getString(R.string.dialog_image_info_content, image.description(), image.rating(), image.nsfw(), image.metadata()))
                        .positiveText(R.string.ok)
                        .build();
            case DIALOG_LICENSES:
                StringBuilder buf = new StringBuilder();
                try {
                    InputStream json = getActivity().getAssets().open("licenses.html");
                    BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
                    String str;
                    while ((str = in.readLine()) != null)
                        buf.append(str);
                    in.close();
                } catch (IOException e) {
                    Timber.e(e, "Failed to load licenses.html");
                }

                return new MaterialDialog.Builder(getActivity())
                        .canceledOnTouchOutside(false)
                        .title(R.string.dialog_licenses_title)
                        .content(Html.fromHtml(buf.toString()))
                        .positiveText(R.string.ok)
                        .build();
            case DIALOG_ERROR_INFO:
                String errorMessage = getArguments().getString(ARG_ERROR);

                return new MaterialDialog.Builder(getActivity())
                        .canceledOnTouchOutside(false)
                        .title(R.string.dialog_error_title)
                        .content(Html.fromHtml(getString(R.string.dialog_error_content, errorMessage)))
                        .positiveText(R.string.ok)
                        .build();
            default:
                throw new UnsupportedOperationException("Invalid dialog type specified");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        App.get(getContext()).refWatcher().watch(this);
    }
}
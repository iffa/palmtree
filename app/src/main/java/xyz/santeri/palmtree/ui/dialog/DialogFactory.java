package xyz.santeri.palmtree.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Html;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import timber.log.Timber;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.BuildConfig;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.ui.main.MainActivity;

/**
 * @author Santeri Elo
 */
public class DialogFactory extends DialogFragment {
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DIALOG_CHANGELOG, DIALOG_LISTING_QUALITY, DIALOG_LICENSES, DIALOG_CONFIRM_EXIT})
    public @interface DialogType {
    }

    public static final int DIALOG_CHANGELOG = 0;
    public static final int DIALOG_LISTING_QUALITY = 1;
    public static final int DIALOG_LICENSES = 2;
    public static final int DIALOG_CONFIRM_EXIT = 3;

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
            case DIALOG_CONFIRM_EXIT:
                return new MaterialDialog.Builder(getActivity())
                        .title(R.string.dialog_confirm_exit)
                        .content(R.string.dialog_confirm_exit_content)
                        .positiveText(R.string.exit)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) -> startActivity(MainActivity.getStartIntent(getContext(), true, true)))
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
package xyz.santeri.palmtree.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.afollestad.materialdialogs.MaterialDialog;

import xyz.santeri.palmtree.R;

/**
 * @author Santeri Elo
 */
public class ChangelogDialogFragment extends DialogFragment {
    public static ChangelogDialogFragment newInstance() {
        return new ChangelogDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.changelog_title, getString(R.string.app_version)))
                .content(R.string.changelog)
                .positiveText(R.string.ok)
                .build();
    }
}
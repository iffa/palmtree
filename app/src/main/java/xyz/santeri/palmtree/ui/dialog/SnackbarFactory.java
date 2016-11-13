package xyz.santeri.palmtree.ui.dialog;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import xyz.santeri.palmtree.R;

/**
 * @author Santeri Elo
 */
public class SnackbarFactory {
    /**
     * Utility method to create an error Snackbar just the way we like it.
     *
     * @param view           View
     * @param retryAction    True if retry-action should be shown
     * @param message        Message
     * @param retryListener  Click listener for retry
     * @param dialogListener Click listener for general click
     * @return Snackbar prebuilt with awesomeness
     */
    public static Snackbar createErrorSnackbar(View view,
                                               boolean retryAction,
                                               @StringRes int message,
                                               @Nullable View.OnClickListener retryListener,
                                               @NonNull View.OnClickListener dialogListener) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);

        if (retryAction) {
            snackbar.setAction(R.string.retry, retryListener);
        }

        snackbar.getView().setOnClickListener(dialogListener);

        return snackbar;
    }

    public static Snackbar createRestartSnackbar(View view,
                                                 View.OnClickListener actionListener) {
        Snackbar snackbar = Snackbar.make(view, R.string.pref_restart_message, Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction(R.string.ok, actionListener);

        return snackbar;
    }
}

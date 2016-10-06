package xyz.santeri.palmtree.ui.dialog

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.view.View
import xyz.santeri.palmtree.R

/**
 * @author Santeri Elo
 */
class SnackbarFactory {
    companion object {
        /**
         * Utility method to create an error Snackbar just the way we like it.

         * @param view           View
         * *
         * @param retryAction    True if retry-action should be shown
         * *
         * @param message        Message
         * *
         * @param retryListener  Click listener for retry
         * *
         * @param dialogListener Click listener for general click
         * *
         * @return Snackbar prebuilt with awesomeness
         */
        @JvmStatic
        fun createErrorSnackbar(view: View,
                                retryAction: Boolean,
                                @StringRes message: Int,
                                retryListener: View.OnClickListener?,
                                dialogListener: View.OnClickListener): Snackbar {
            val snackbar = Snackbar.make(
                    view,
                    view.context.getString(message),
                    Snackbar.LENGTH_INDEFINITE)

            if (retryAction) {
                snackbar.setAction(R.string.retry, retryListener)
            }

            snackbar.view.setOnClickListener(dialogListener)

            return snackbar
        }
    }
}
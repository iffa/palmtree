package xyz.santeri.palmtree.ui.listing;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import xyz.santeri.palmtree.data.model.ImageDetails;

/**
 * @author Santeri Elo
 */
interface ListingView extends TiView {
    @CallOnMainThread
    void showError(int currentPage, Throwable throwable, int message);

    @CallOnMainThread
    void startLoading(boolean swipeToRefresh);

    @CallOnMainThread
    void finishLoading();

    @CallOnMainThread
    void scrollToTop();

    @CallOnMainThread
    void restoreCurrentPage(int currentPage);

    @CallOnMainThread
    void openDetails(ImageDetails position);

    @CallOnMainThread
    void showQualityInfo();

    @CallOnMainThread
    void openDialogDetails(ImageDetails item);
}

package xyz.santeri.palmtree.ui.listing;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import java.util.List;

import xyz.santeri.palmtree.data.model.ImageDetails;

/**
 * @author Santeri Elo
 */
interface ListingView extends TiView {
    @CallOnMainThread
    void addImage(ImageDetails imageDetails);

    @CallOnMainThread
    void showError(boolean snack);

    @CallOnMainThread
    void startLoading(boolean swipeToRefresh);

    @CallOnMainThread
    void finishLoading();

    @CallOnMainThread
    void scrollToTop();

    @CallOnMainThread
    void clear();

    @CallOnMainThread
    void restoreImages(List<ImageDetails> items, int currentPage, int scrollPosition);

    @CallOnMainThread
    void openDetails(Integer position);
}

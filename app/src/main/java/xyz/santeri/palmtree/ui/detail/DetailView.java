package xyz.santeri.palmtree.ui.detail;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import xyz.santeri.palmtree.data.model.ImageDetails;

/**
 * @author Santeri Elo
 */
interface DetailView extends TiView {
    @CallOnMainThread
    void showLoading();

    @CallOnMainThread
    void showImage(ImageDetails imageDetails);

    @CallOnMainThread
    void startShareIntent(String shareUrl);

    @CallOnMainThread
    void showSwipeHint();
}

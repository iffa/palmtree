package xyz.santeri.palmtree.ui.main;

import net.grandcentrix.thirtyinch.TiView;
import net.grandcentrix.thirtyinch.callonmainthread.CallOnMainThread;

import rx.Observable;

/**
 * @author Santeri Elo
 */
interface MainView extends TiView {
    @CallOnMainThread
    Observable<Void> onToolbarClick();

    @CallOnMainThread
    void showUpdateInfo();
}

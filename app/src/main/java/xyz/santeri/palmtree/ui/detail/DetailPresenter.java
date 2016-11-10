package xyz.santeri.palmtree.ui.detail;

import android.content.Context;
import android.support.annotation.NonNull;

import net.grandcentrix.thirtyinch.TiPresenter;

import javax.inject.Inject;

import io.github.prashantsolanki3.shoot.Shoot;
import io.github.prashantsolanki3.shoot.listener.OnShootListener;
import io.github.prashantsolanki3.shoot.utils.Scope;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.data.DataManager;
import xyz.santeri.palmtree.data.model.ImageDetails;

/**
 * @author Santeri Elo
 */
public class DetailPresenter extends TiPresenter<DetailView> {
    private ImageDetails imageDetails = null;

    @Inject
    DataManager dataManager;

    DetailPresenter(Context context) {
        App.get(context).getComponent().inject(this);
    }

    @Override
    protected void onAttachView(@NonNull DetailView view) {
        super.onAttachView(view);

        view.showLoading();

        // Show a swipe hint to the user when first starting
        Shoot.once(App.SHOOT_DETAILS_SWIPE, new OnShootListener() {
            @Override
            public void onExecute(@Scope int scope, String tag, int iterationCount) {
                view.showSwipeHint();
            }
        });
    }

    void load(ImageDetails imageDetails) {
        if (imageDetails != null) {
            if (getView() != null) getView().showImage(imageDetails);
        }

        this.imageDetails = imageDetails;
        if (getView() != null) getView().showImage(imageDetails);
    }

    void load(int fileId) {
        if (imageDetails != null) {
            if (getView() != null) getView().showImage(imageDetails);
        }

        dataManager.getImageDetails(fileId).subscribe(
                item -> {
                    this.imageDetails = item;
                    if (getView() != null) getView().showImage(item);
                }, throwable -> {
                    if (getView() != null) getView().showError(dataManager.getDetailError(throwable), throwable);
                });
    }

    void onShareClicked() {
        if (getView() != null) getView().startShareIntent(dataManager.getShareUrl(imageDetails));
    }

    void onDetailsClicked() {
        if (imageDetails != null) {
            if (getView() != null) getView().showDetailsDialog(imageDetails);
        }
    }
}

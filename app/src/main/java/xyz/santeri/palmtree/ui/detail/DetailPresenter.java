package xyz.santeri.palmtree.ui.detail;

import android.content.Context;

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
        App.get(context).component().inject(this);
    }

    @Override
    protected void onWakeUp() {
        super.onWakeUp();

        getView().showLoading();

        // Show a swipe hint to the user when first starting
        Shoot.once(App.SHOOT_DETAILS_SWIPE, new OnShootListener() {
            @Override
            public void onExecute(@Scope int scope, String TAG, int iterationCount) {
                getView().showSwipeHint();
            }
        });
    }

    void load(ImageDetails imageDetails) {
        if (imageDetails != null) {
            getView().showImage(imageDetails);
        }

        this.imageDetails = imageDetails;
        getView().showImage(imageDetails);
    }

    void load(int fileId) {
        if (imageDetails != null) {
            getView().showImage(imageDetails);
        }

        dataManager.getImageDetails(fileId).subscribe(
                item -> {
                    this.imageDetails = item;
                    getView().showImage(item);
                }, throwable -> {
                    getView().showError(dataManager.getDetailError(throwable), throwable);
                });
    }

    void onShareClicked() {
        getView().startShareIntent(dataManager.getShareUrl(imageDetails));
    }

    void onDetailsClicked() {
        if (imageDetails != null) {
            getView().showDetailsDialog(imageDetails);
        }
    }
}

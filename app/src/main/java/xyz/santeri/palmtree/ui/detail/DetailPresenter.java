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
    private ImageDetails imageDetails;

    @Inject
    DataManager dataManager;

    DetailPresenter(Context context) {
        App.get(context).component().inject(this);
    }

    @Override
    protected void onWakeUp() {
        super.onWakeUp();

        getView().showLoading();

        getView().showImage(imageDetails);

        // Show a swipe hint to the user when first starting
        Shoot.once(App.SHOOT_DETAILS_SWIPE, new OnShootListener() {
            @Override
            public void onExecute(@Scope int scope, String TAG, int iterationCount) {
                getView().showSwipeHint();
            }
        });
    }

    void load(ImageDetails imageDetails) {
        this.imageDetails = imageDetails;
    }

    void onShareClicked() {
        getView().startShareIntent(dataManager.getShareUrl(imageDetails));
    }
}

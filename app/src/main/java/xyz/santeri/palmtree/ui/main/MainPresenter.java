package xyz.santeri.palmtree.ui.main;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import org.greenrobot.eventbus.EventBus;

import io.github.prashantsolanki3.shoot.Shoot;
import io.github.prashantsolanki3.shoot.listener.OnShootListener;
import io.github.prashantsolanki3.shoot.utils.Scope;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.ui.base.event.ListingTypeChangeEvent;
import xyz.santeri.palmtree.ui.base.event.ScrollToTopEvent;

/**
 * @author Santeri Elo
 */
class MainPresenter extends TiPresenter<MainView> {
    private RxTiPresenterSubscriptionHandler subscriptionHelper = new RxTiPresenterSubscriptionHandler(this);

    @Override
    protected void onWakeUp() {
        super.onWakeUp();

        subscriptionHelper.manageViewSubscription(getView().onToolbarClick().subscribe(aVoid -> {
            EventBus.getDefault().post(new ScrollToTopEvent());
        }));

        Shoot.once(Shoot.APP_VERSION, App.SHOOT_UPDATE_TAG, new OnShootListener() {
            @Override
            public void onExecute(@Scope int scope, String TAG, int iterationCount) {
                getView().showUpdateInfo();
            }
        });
    }

    void onListingTypeChange(ListingType type) {
        EventBus.getDefault().post(new ListingTypeChangeEvent(type));
    }
}

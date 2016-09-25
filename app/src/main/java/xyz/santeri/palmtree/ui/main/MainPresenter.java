package xyz.santeri.palmtree.ui.main;

import android.content.Context;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.github.prashantsolanki3.shoot.Shoot;
import io.github.prashantsolanki3.shoot.listener.OnShootListener;
import io.github.prashantsolanki3.shoot.utils.Scope;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.data.local.PreferencesHelper;
import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.ui.base.event.ListingTypeChangeEvent;
import xyz.santeri.palmtree.ui.base.event.ScrollToTopEvent;

/**
 * @author Santeri Elo
 */
public class MainPresenter extends TiPresenter<MainView> {
    private RxTiPresenterSubscriptionHandler subscriptionHelper = new RxTiPresenterSubscriptionHandler(this);
    private ListingType currentCategory;

    @Inject
    PreferencesHelper preferencesHelper;

    MainPresenter(Context context) {
        App.get(context).component().inject(this);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        currentCategory = preferencesHelper.getCategory();
    }

    @Override
    protected void onWakeUp() {
        super.onWakeUp();

        subscriptionHelper.manageViewSubscription(getView().onToolbarClick().subscribe(aVoid -> {
            EventBus.getDefault().post(new ScrollToTopEvent());
        }));

        Shoot.once(Shoot.APP_VERSION, App.SHOOT_UPDATE_TAG, new OnShootListener() {
            @Override
            public void onExecute(@Scope int scope, String TAG, int iterationCount) {
                if (preferencesHelper.getShowChangelog()) {
                    getView().showUpdateInfo();
                }
            }
        });
    }

    void onListingTypeChange(ListingType type) {
        currentCategory = type;
        EventBus.getDefault().post(new ListingTypeChangeEvent(type));
    }

    ListingType getCurrentCategory() {
        return currentCategory;
    }
}

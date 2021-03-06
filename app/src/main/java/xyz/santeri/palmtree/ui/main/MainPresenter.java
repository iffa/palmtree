package xyz.santeri.palmtree.ui.main;

import android.content.Context;
import android.support.annotation.NonNull;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import io.github.prashantsolanki3.shoot.Shoot;
import io.github.prashantsolanki3.shoot.listener.OnShootListener;
import io.github.prashantsolanki3.shoot.utils.Scope;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.base.ListingType;
import xyz.santeri.palmtree.data.local.PreferencesHelper;
import xyz.santeri.palmtree.ui.base.event.ListingTypeChangeEvent;
import xyz.santeri.palmtree.ui.base.event.ScrollToTopEvent;

/**
 * @author Santeri Elo
 */
public class MainPresenter extends TiPresenter<MainView> {
    private RxTiPresenterSubscriptionHandler subscriptionHelper =
            new RxTiPresenterSubscriptionHandler(this);
    private ListingType currentCategory;

    @Inject
    PreferencesHelper preferencesHelper;

    MainPresenter(Context context) {
        App.get(context).getComponent().inject(this);
    }

    @Override
    protected void onCreate() {
        super.onCreate();

        currentCategory = preferencesHelper.getCategory();
    }

    @Override
    protected void onAttachView(@NonNull MainView view) {
        super.onAttachView(view);

        subscriptionHelper.manageViewSubscription(view.onToolbarClick().subscribe(aVoid -> {
            EventBus.getDefault().post(new ScrollToTopEvent());
        }));

        Shoot.once(Shoot.APP_VERSION, App.SHOOT_UPDATE_TAG, new OnShootListener() {
            @Override
            public void onExecute(@Scope int scope, String tag, int iterationCount) {
                if (preferencesHelper.isChangelogEnabled()) {
                    view.showUpdateInfo();
                }
            }
        });
    }

    boolean shouldConfirmExit() {
        return preferencesHelper.isConfirmExitEnabled();
    }

    void onListingTypeChange(ListingType type) {
        currentCategory = type;
        EventBus.getDefault().post(new ListingTypeChangeEvent(type));
    }

    ListingType getCurrentCategory() {
        return currentCategory;
    }
}

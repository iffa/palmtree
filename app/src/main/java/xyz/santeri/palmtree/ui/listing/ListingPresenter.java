package xyz.santeri.palmtree.ui.listing;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.grandcentrix.thirtyinch.TiPresenter;
import net.grandcentrix.thirtyinch.rx.RxTiPresenterSubscriptionHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.prashantsolanki3.shoot.Shoot;
import io.github.prashantsolanki3.shoot.listener.OnShootListener;
import io.github.prashantsolanki3.shoot.utils.Scope;
import timber.log.Timber;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.base.ListingType;
import xyz.santeri.palmtree.data.DataManager;
import xyz.santeri.palmtree.data.local.PreferencesHelper;
import xyz.santeri.palmtree.ui.base.event.ListingTypeChangeEvent;
import xyz.santeri.palmtree.ui.base.event.ScrollToTopEvent;
import xyz.santeri.palmtree.ui.listing.adapter.ListingAdapter;

/**
 * @author Santeri Elo
 */
@Singleton
public class ListingPresenter extends TiPresenter<ListingView> {
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private Bundle recyclerState = new Bundle();
    private ListingAdapter listingAdapter;
    private int currentPage;
    private ListingType listingType;
    private RxTiPresenterSubscriptionHandler subscriptionHelper =
            new RxTiPresenterSubscriptionHandler(this);

    @Inject
    DataManager dataManager;

    @Inject
    PreferencesHelper preferences;

    ListingPresenter(Context context, ListingType listingType) {
        App.get(context).getComponent().inject(this);
        this.listingType = listingType;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        listingAdapter = new ListingAdapter(preferences.isDataSavingEnabled(),
                preferences.isFullPreviewEnabled());
    }

    @Override
    protected void onAttachView(@NonNull ListingView view) {
        super.onAttachView(view);

        EventBus.getDefault().register(this);

        if (listingAdapter.getItemCount() > 0) {
            Timber.d("Already have items in adapter (configuration change?)");

            view.restoreCurrentPage(currentPage);
        } else {
            Timber.d("No existing data, loading fresh from page 1");

            onRefresh();
        }
    }

    @Override
    protected void onDetachView() {
        super.onDetachView();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onScrollToTopEvent(ScrollToTopEvent event) {
        if (getView() != null) getView().scrollToTop();
    }

    @Subscribe
    public void onListingTypeChangeEvent(ListingTypeChangeEvent event) {
        Timber.d("Changing listing type from %s to %s",
                listingType.name(), event.getListingType().name());
        listingType = event.getListingType();
        onRefresh();
    }

    void onRefresh() {
        Timber.d("Refreshing front page");

        listingAdapter.clear();
        recyclerState.clear();

        if (getView() != null) getView().restoreCurrentPage(1);

        if (listingType == ListingType.LATEST_VIDEOS
                || listingType == ListingType.LATEST_ALL || listingType == ListingType.RANDOM) {
            Shoot.once(App.SHOOT_LISTING_QUALITY, new OnShootListener() {
                @Override
                public void onExecute(@Scope int scope, String tag, int iterationCount) {
                    Timber.d("Showing listing quality info");
                    if (getView() != null) getView().showQualityInfo();
                }
            });
        }

        load(1);
    }

    void load(int page) {
        if (page == 1) {
            if (getView() != null) getView().startLoading(true);
        } else {
            if (getView() != null) getView().startLoading(false);
        }

        currentPage = page;

        subscriptionHelper.manageSubscription(
                dataManager.getListing(listingType, page).filter(image -> {
                    if (preferences.isShowNsfwEnabled()) return true;

                    if (image.nsfw()) {
                        Timber.d("Not showing NSFW image as current settings don't allow it");
                        return false;
                    } else {
                        return true;
                    }
                }).subscribe(
                        item -> listingAdapter.addItem(item),
                        throwable -> {
                            Timber.e(throwable, "Failed to load %s page %s", listingType, page);
                            getView().showError(page, throwable,
                                    dataManager.getListingError(throwable));
                        },
                        () -> getView().finishLoading()));
    }

    void onItemClick(int position) {
        if (getView() != null) getView().openDetails(listingAdapter.getItemAt(position));
    }

    void putRecyclerState(@NonNull Parcelable state) {
        recyclerState.putParcelable(KEY_RECYCLER_STATE, state);
    }

    @Nullable
    Parcelable getRecyclerState() {
        return recyclerState.getParcelable(KEY_RECYCLER_STATE);
    }

    ListingAdapter getListingAdapter() {
        return listingAdapter;
    }

    void onItemLongClick(int position) {
        if (getView() != null) getView().openDialogDetails(listingAdapter.getItemAt(position));
    }
}

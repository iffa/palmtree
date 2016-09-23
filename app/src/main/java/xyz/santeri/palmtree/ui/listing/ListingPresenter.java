package xyz.santeri.palmtree.ui.listing;

import android.content.Context;

import net.grandcentrix.thirtyinch.TiPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.prashantsolanki3.shoot.Shoot;
import io.github.prashantsolanki3.shoot.listener.OnShootListener;
import io.github.prashantsolanki3.shoot.utils.Scope;
import timber.log.Timber;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.data.DataManager;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.ui.base.event.ListingTypeChangeEvent;
import xyz.santeri.palmtree.ui.base.event.ScrollToTopEvent;

/**
 * @author Santeri Elo
 */
@Singleton
public class ListingPresenter extends TiPresenter<ListingView> {
    private LinkedHashMap<Integer, List<ImageDetails>> itemsListing = new LinkedHashMap<>();
    private int scrollPosition;
    private int currentPage;
    private ListingType listingType = ListingType.FRONT_PAGE;

    @Inject
    DataManager dataManager;

    ListingPresenter(Context context) {
        App.get(context).component().inject(this);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onWakeUp() {
        super.onWakeUp();

        EventBus.getDefault().register(this);

        if (itemsListing.size() > 0) {
            Timber.d("Already have %s pages of items (configuration change?)", itemsListing.size());

            List<ImageDetails> restored = new ArrayList<>();

            //noinspection Convert2streamapi
            for (List<ImageDetails> images : itemsListing.values()) {
                restored.addAll(images);
            }

            getView().restoreImages(restored, currentPage, scrollPosition);
        } else {
            Timber.d("No existing data, loading fresh from page 1");

            onRefresh();
        }
    }

    @Override
    protected void onSleep() {
        super.onSleep();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onScrollToTopEvent(ScrollToTopEvent event) {
        getView().scrollToTop();
    }

    @Subscribe
    public void onListingTypeChangeEvent(ListingTypeChangeEvent event) {
        Timber.d("Changing listing type from %s to %s", listingType.name(), event.getListingType().name());
        listingType = event.getListingType();
        onRefresh();
    }

    void onRefresh() {
        Timber.d("Refreshing front page");

        getView().clear();
        itemsListing.clear();

        if (listingType == ListingType.LATEST_VIDEOS || listingType == ListingType.LATEST_ALL) {
            Shoot.once(App.SHOOT_LISTING_QUALITY, new OnShootListener() {
                @Override
                public void onExecute(@Scope int scope, String TAG, int iterationCount) {
                    Timber.d("Showing listing quality info");
                    getView().showQualityInfo();
                }
            });
        }

        load(1);
    }

    void load(int page) {
        if (page == 1) {
            getView().startLoading(true);
        } else {
            getView().startLoading(false);
        }

        currentPage = page;

        List<ImageDetails> newItems = new ArrayList<>();

        dataManager.getListing(listingType, page)
                .subscribe(
                        item -> {
                            newItems.add(item);
                            getView().addImage(item);
                        },
                        throwable -> {
                            Timber.e(throwable, "Failed to load %s page %s", listingType, page);
                            getView().showError(true);
                        },
                        () -> {
                            itemsListing.put(page, newItems);
                            getView().finishLoading();
                        });
    }

    void onItemClick(int position) {
        getView().openDetails(position);
    }

    void saveScrollPosition(int firstVisibleItemPosition) {
        scrollPosition = firstVisibleItemPosition;
    }
}

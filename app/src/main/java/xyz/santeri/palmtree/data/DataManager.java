package xyz.santeri.palmtree.data;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatDelegate;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.base.DetailsService;
import xyz.santeri.palmtree.base.ListingService;
import xyz.santeri.palmtree.data.local.PreferencesHelper;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ListingType;

/**
 * @author Santeri Elo
 */
@Singleton
public class DataManager {
    private static final String SHARE_URL_TEMPLATE = "http://naamapalmu.com/file/%s";
    private final ListingService listingService;
    private final DetailsService detailsService;
    private final PreferencesHelper preferences;
    private final ConnectivityManager connectivityManager;

    @SuppressWarnings("RedundantCast") // Not redundant - won't compile without the cast
    private final Observable.Transformer schedulersTransformer =
            observable -> ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());


    @Inject
    DataManager(ListingService listingService, DetailsService detailsService,
                PreferencesHelper preferences, ConnectivityManager connectivityManager) {
        this.listingService = listingService;
        this.detailsService = detailsService;
        this.preferences = preferences;
        this.connectivityManager = connectivityManager;
    }

    /**
     * Gets a listing of the specified type and page number.
     *
     * @param type {@link ListingType}
     * @param page Page number
     * @return {@link Observable} stream of {{@link ImageDetails} objects
     */
    public Observable<ImageDetails> getListing(ListingType type, int page) {
        Timber.d("Getting listing %s page %s", type.name(), page);
        return listingService.getListing(type, page)
                .onBackpressureDrop() // Fails to load 2 listing types without this, what the fuck
                .compose(applySchedulers());
    }

    /**
     * Gets details of the specified image id.
     *
     * @param imageNumber Image id
     * @return {@link Observable} emitting an {@link ImageDetails} object
     */
    public Observable<ImageDetails> getImageDetails(int imageNumber) {
        Timber.d("Getting details for id %s", imageNumber);
        return detailsService.getImageDetails(imageNumber)
                .doOnCompleted(() -> Timber.d("Completed loading details for id %s", imageNumber))
                .doOnError(throwable -> Timber.e(throwable, "Failed to load details for id %s", imageNumber))
                .compose(applySchedulers());
    }

    /**
     * Returns a link to the image on naamapalmu.com.
     *
     * @param imageDetails {@link ImageDetails} to get URL For
     * @return URL, e.g. http://naamapalmu.com/file/79085
     */
    public String getShareUrl(ImageDetails imageDetails) {
        return String.format(SHARE_URL_TEMPLATE, imageDetails.id());
    }

    /**
     * @param throwable Error
     * @return String resource
     */
    public
    @StringRes
    int getErrorMessage(Throwable throwable) {
        if (isNetworkAvailable()) {
            return R.string.error_page_load;
        } else {
            return R.string.error_internet_connection;
        }
    }

    public int getTheme() {
        switch (preferences.getTheme()) {
            case 0:
                Timber.d("Current theme: light");
                return AppCompatDelegate.MODE_NIGHT_NO;
            case 1:
                Timber.d("Current theme: dark");
                return AppCompatDelegate.MODE_NIGHT_YES;
            case 2:
                Timber.d("Current theme: automatic");
                return AppCompatDelegate.MODE_NIGHT_AUTO;
            default:
                Timber.d("Current theme: system default");
                return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
    }

    private boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    @SuppressWarnings("unchecked")
    private <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }
}

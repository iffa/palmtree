package xyz.santeri.palmtree.data;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import xyz.santeri.palmtree.base.DetailsService;
import xyz.santeri.palmtree.base.ListingService;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.data.model.TableImage;

/**
 * @author Santeri Elo
 */
@Singleton
public class DataManager {
    private static final String SHARE_URL_TEMPLATE = "http://naamapalmu.com/file/%s";
    private final ListingService listingService;
    private final DetailsService detailsService;

    @SuppressWarnings("RedundantCast") // Not redundant - won't compile without the cast
    private final Observable.Transformer schedulersTransformer =
            observable -> ((Observable) observable).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());


    @Inject
    DataManager(ListingService listingService, DetailsService detailsService) {
        this.listingService = listingService;
        this.detailsService = detailsService;
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
                .doOnCompleted(() -> Timber.d("Completed loading page %s", page))
                .doOnError(throwable -> Timber.e(throwable, "Failed to load listing %s page %s", type.name(), page))
                .compose(applySchedulers());
    }

    /**
     * Gets a table listing of the specified type and page number.
     *
     * @param type {@link ListingType}
     * @param page Page number
     * @return {@link Observable} stream of {{@link TableImage} objects
     */
    public Observable<TableImage> getTableListing(ListingType type, int page) {
        Timber.d("Getting listing %s page %s", type.name(), page);
        return listingService.getTableListing(type, page)
                .doOnCompleted(() -> Timber.d("Completed loading page %s", page))
                .doOnError(throwable -> Timber.e(throwable, "Failed to load listing %s page %s", type.name(), page))
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

    @SuppressWarnings("unchecked")
    private <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) schedulersTransformer;
    }
}

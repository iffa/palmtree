package xyz.santeri.palmtree.base;

import rx.Observable;
import xyz.santeri.palmtree.data.model.ImageDetails;
import xyz.santeri.palmtree.data.model.ListingType;

/**
 * @author Santeri Elo
 */
public interface ListingService {
    /**
     * Gets a listing of the specified type and page number.
     *
     * @param type       {@link ListingType} - only FRONT_PAGE and LATEST_IMAGES are supported
     * @param pageNumber Page number
     * @return {@link Observable} stream of {{@link ImageDetails} objects
     */
    Observable<ImageDetails> getListing(ListingType type, int pageNumber);

    /**
     * Gets a table listing of the specified type and page number.
     *
     * @param type       {@link ListingType} - only LATEST_VIDEOS and LATEST_ALL are supported
     * @param pageNumber Page number
     * @return {@link Observable} stream of {@link ImageDetails} objects
     */
    Observable<ImageDetails> getTableListing(ListingType type, int pageNumber);
}

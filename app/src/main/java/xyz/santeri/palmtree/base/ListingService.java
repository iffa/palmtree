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
     * @param type       {@link ListingType}
     * @param pageNumber Page number
     * @return {@link Observable} stream of {{@link ImageDetails} objects
     */
    Observable<ImageDetails> getListing(ListingType type, int pageNumber);
}

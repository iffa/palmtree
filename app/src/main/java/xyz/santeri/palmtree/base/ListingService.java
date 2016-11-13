package xyz.santeri.palmtree.base;

import rx.Observable;
import xyz.santeri.palmtree.data.model.ImageDetails;

/**
 * @author Santeri Elo
 */
public interface ListingService {
    /**
     * Gets a listing of the specified type and page number.
     *
     * @param type       ListingType
     * @param pageNumber Page number
     * @return Observable stream of ImageDetails objects
     */
    Observable<ImageDetails> getListing(ListingType type, int pageNumber);
}
package xyz.santeri.palmtree.base

import rx.Observable
import xyz.santeri.palmtree.data.model.ImageDetails

/**
 * @author Santeri Elo
 */
interface ListingService {
    /**
     * Gets a listing of the specified type and page number.

     * @param type       [ListingType]
     * *
     * @param pageNumber Page number
     * *
     * @return [Observable] stream of {[ImageDetails] objects
     */
    fun getListing(type: ListingType, pageNumber: Int): Observable<ImageDetails>
}

enum class ListingType {
    FRONT_PAGE,
    LATEST_IMAGES,
    LATEST_VIDEOS,
    LATEST_ALL,
    RANDOM
}
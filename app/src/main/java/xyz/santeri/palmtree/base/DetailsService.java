package xyz.santeri.palmtree.base;

import rx.Observable;
import xyz.santeri.palmtree.data.model.ImageDetails;

/**
 * @author Santeri Elo
 */
public interface DetailsService {
    /**
     * Get details of an image.

     * @param imageNumber Image id, e.g. 79044
     * *
     * @return Observable emitting an ImageDetails object
     */
    Observable<ImageDetails> getImageDetails(int imageNumber);

}

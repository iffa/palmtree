package xyz.santeri.palmtree.ui.base.event;

import xyz.santeri.palmtree.base.ListingType;

/**
 * Called when the user switches from a {@link ListingType} to another.
 *
 * @author Santeri Elo
 */
public class ListingTypeChangeEvent {
    private ListingType listingType;

    public ListingTypeChangeEvent(ListingType type) {
        this.listingType = type;
    }

    public ListingType getListingType() {
        return listingType;
    }
}

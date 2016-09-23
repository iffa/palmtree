package xyz.santeri.palmtree.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.di.AppContext;

/**
 * @author Santeri Elo
 */
@Singleton
public class PreferencesHelper {
    public static final String PREF_KEY_THEME = "pref_theme";
    private static final String PREF_KEY_CATEGORY = "pref_category";
    private static final String PREF_KEY_SWIPE = "pref_swipe_back";
    private static final String PREF_KEY_CLICK = "pref_click_back";
    private static final String PREF_KEY_TOOLBAR = "pref_toolbar_hide";
    private final SharedPreferences preferences;

    @Inject
    public PreferencesHelper(@AppContext Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public int getTheme() {
        return Integer.parseInt(preferences.getString(PREF_KEY_THEME, "0"));
    }

    public ListingType getCategory() {
        switch (Integer.parseInt(preferences.getString(PREF_KEY_CATEGORY, "0"))) {
            case 0:
                return ListingType.FRONT_PAGE;
            case 1:
                return ListingType.LATEST_IMAGES;
            case 2:
                return ListingType.LATEST_VIDEOS;
            case 3:
                return ListingType.LATEST_ALL;
            case 4:
                return ListingType.RANDOM;
            default:
                return ListingType.FRONT_PAGE;
        }
    }

    public boolean getSwipeToReturnEnabled() {
        return preferences.getBoolean(PREF_KEY_SWIPE, true);
    }

    public boolean getClickToReturnEnabled() {
        return preferences.getBoolean(PREF_KEY_CLICK, true);
    }

    public boolean getHideToolbarEnabled() {
        return preferences.getBoolean(PREF_KEY_TOOLBAR, true);
    }
}
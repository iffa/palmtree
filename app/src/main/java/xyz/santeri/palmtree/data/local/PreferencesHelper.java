package xyz.santeri.palmtree.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import xyz.santeri.palmtree.base.ListingType;
import xyz.santeri.palmtree.di.AppContext;

/**
 * @author Santeri Elo
 */
@Singleton
public class PreferencesHelper {
    private static final String PREF_KEY_THEME = "pref_theme";
    private static final String PREF_KEY_CATEGORY = "pref_category";
    private static final String PREF_KEY_SAVE_DATA = "pref_save_data";
    private static final String PREF_KEY_CHANGELOG = "pref_changelog";
    private static final String PREF_KEY_NSFW = "pref_show_nsfw";
    private static final String PREF_KEY_CONFIRM_EXIT = "pref_confirm_exit";
    private static final String PREF_KEY_FULL_PREVIEW = "pref_full_preview";
    private final SharedPreferences preferences;

    @Inject
    PreferencesHelper(@AppContext Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public int getTheme() {
        return Integer.parseInt(preferences.getString(PREF_KEY_THEME, "0"));
    }

    public boolean getDataSavingEnabled() {
        return preferences.getBoolean(PREF_KEY_SAVE_DATA, false);
    }

    public boolean getFullPreviewEnabled() {
        return preferences.getBoolean(PREF_KEY_FULL_PREVIEW, false);
    }

    public boolean getShowNsfwEnabled() {
        return preferences.getBoolean(PREF_KEY_NSFW, false);
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

    public boolean getShowChangelogEnabled() {
        return preferences.getBoolean(PREF_KEY_CHANGELOG, true);
    }

    public boolean getConfirmExitEnabled() {
        return preferences.getBoolean(PREF_KEY_CONFIRM_EXIT, false);
    }
}
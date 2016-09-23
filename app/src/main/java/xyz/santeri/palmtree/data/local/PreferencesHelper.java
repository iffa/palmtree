package xyz.santeri.palmtree.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import xyz.santeri.palmtree.di.AppContext;

/**
 * @author Santeri Elo
 */
@Singleton
public class PreferencesHelper {
    public static final String PREF_KEY_THEME = "pref_theme";
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
}

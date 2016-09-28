package xyz.santeri.palmtree.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.Preference;
import android.support.v7.preference.XpPreferenceFragment;
import android.view.View;

import javax.inject.Inject;

import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.BuildConfig;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.DataManager;
import xyz.santeri.palmtree.data.local.PreferencesHelper;
import xyz.santeri.palmtree.ui.dialog.DialogFactory;
import xyz.santeri.palmtree.ui.main.MainActivity;

/**
 * @author Santeri Elo
 */
public class SettingsFragment extends XpPreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String PREF_KEY_VERSION = "pref_version";
    private static final String PREF_KEY_LICENSES = "pref_licenses";

    @Inject
    DataManager dataManager;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreatePreferences2(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_customization);

        addPreferencesFromResource(R.xml.pref_general);

        addPreferencesFromResource(R.xml.pref_other);

        bindPreferenceSummaryToValue(findPreference(PREF_KEY_VERSION));

        findPreference(PREF_KEY_LICENSES).setOnPreferenceClickListener(
                preference -> {
                    DialogFactory.newInstance(DialogFactory.DIALOG_LICENSES)
                            .show(getFragmentManager(), "licenses_dialog");
                    return true;
                });
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setSummary(BuildConfig.VERSION_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        App.get(getContext()).refWatcher().watch(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        App.get(getContext()).component().inject(this);

        getListView().setFocusable(false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferencesHelper.PREF_KEY_THEME) || key.equals(PreferencesHelper.PREF_KEY_CATEGORY)) {
            //noinspection WrongConstant
            AppCompatDelegate.setDefaultNightMode(dataManager.getTheme());
            startActivity(MainActivity.getStartIntent(getContext(), true, false));
        }
    }
}
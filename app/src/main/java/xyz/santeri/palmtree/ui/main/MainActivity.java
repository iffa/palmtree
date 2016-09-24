package xyz.santeri.palmtree.ui.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jakewharton.rxbinding.view.RxView;

import net.grandcentrix.thirtyinch.TiActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.local.PreferencesHelper;
import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.ui.dialog.DialogFactory;
import xyz.santeri.palmtree.ui.listing.ListingFragment;
import xyz.santeri.palmtree.ui.settings.SettingsActivity;

/**
 * @author Santeri Elo
 */
public class MainActivity extends TiActivity<MainPresenter, MainView> implements MainView {
    @Inject
    PreferencesHelper preferencesHelper;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Menu menu;

    public static Intent getStartIntent(Context context, boolean newTask) {
        Intent intent = new Intent(context, MainActivity.class);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.get(this).component().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            ListingType listingType = preferencesHelper.getCategory();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, ListingFragment.newInstance(listingType))
                    .commit();

            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.frontpage)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        assert getSupportActionBar() != null; // damn I love doing this
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(SettingsActivity.getStartIntent(this));
                return true;
            case R.id.action_list_frontpage:
                if (item.isChecked()) return true;
                item.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.frontpage)));

                getPresenter().onListingTypeChange(ListingType.FRONT_PAGE);
                return true;
            case R.id.action_list_images:
                if (item.isChecked()) return true;
                item.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.latest_images)));

                getPresenter().onListingTypeChange(ListingType.LATEST_IMAGES);
                return true;
            case R.id.action_list_videos:
                if (item.isChecked()) return true;
                item.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.latest_videos)));

                getPresenter().onListingTypeChange(ListingType.LATEST_VIDEOS);
                return true;
            case R.id.action_list_all:
                if (item.isChecked()) return true;
                item.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.latest_all)));

                getPresenter().onListingTypeChange(ListingType.LATEST_ALL);
                return true;
            case R.id.action_list_random:
                if (item.isChecked()) return true;
                item.setChecked(true);
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.random)));

                getPresenter().onListingTypeChange(ListingType.RANDOM);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public MainPresenter providePresenter() {
        return new MainPresenter();
    }

    @Override
    public Observable<Void> onToolbarClick() {
        return RxView.clicks(toolbar);
    }

    @Override
    public void showUpdateInfo() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        DialogFactory.newInstance(DialogFactory.DIALOG_CHANGELOG).show(fragmentManager, "changelog_dialog");
    }
}

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

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;
import xyz.santeri.palmtree.R;
import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.ui.dialog.DialogFactory;
import xyz.santeri.palmtree.ui.listing.ListingFragment;
import xyz.santeri.palmtree.ui.settings.SettingsActivity;

/**
 * @author Santeri Elo
 */
public class MainActivity extends TiActivity<MainPresenter, MainView> implements MainView {
    private static final String EXTRA_KEY_FINISH = "finish";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static Intent getStartIntent(Context context, boolean newTask, boolean finish) {
        Intent intent = new Intent(context, MainActivity.class);
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        intent.putExtra(EXTRA_KEY_FINISH, finish);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra(EXTRA_KEY_FINISH, false)) {
            finish();
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, ListingFragment.newInstance(getPresenter().getCurrentCategory()))
                    .commit();
        }

        setToolbarTitle(getPresenter().getCurrentCategory());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        switch (getPresenter().getCurrentCategory()) {
            case FRONT_PAGE:
                menu.findItem(R.id.action_list_frontpage).setChecked(true);
                return true;
            case LATEST_IMAGES:
                menu.findItem(R.id.action_list_images).setChecked(true);
                return true;
            case LATEST_VIDEOS:
                menu.findItem(R.id.action_list_videos).setChecked(true);
                return true;
            case LATEST_ALL:
                menu.findItem(R.id.action_list_all).setChecked(true);
                return true;
            case RANDOM:
                menu.findItem(R.id.action_list_random).setChecked(true);
                return true;
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(SettingsActivity.getStartIntent(this));
                return true;
            case R.id.action_list_frontpage:
                if (item.isChecked()) return true;
                item.setChecked(true);

                setToolbarTitle(ListingType.FRONT_PAGE);
                getPresenter().onListingTypeChange(ListingType.FRONT_PAGE);
                return true;
            case R.id.action_list_images:
                if (item.isChecked()) return true;
                item.setChecked(true);

                setToolbarTitle(ListingType.LATEST_IMAGES);
                getPresenter().onListingTypeChange(ListingType.LATEST_IMAGES);
                return true;
            case R.id.action_list_videos:
                if (item.isChecked()) return true;
                item.setChecked(true);

                setToolbarTitle(ListingType.LATEST_VIDEOS);
                getPresenter().onListingTypeChange(ListingType.LATEST_VIDEOS);
                return true;
            case R.id.action_list_all:
                if (item.isChecked()) return true;
                item.setChecked(true);

                setToolbarTitle(ListingType.LATEST_ALL);
                getPresenter().onListingTypeChange(ListingType.LATEST_ALL);
                return true;
            case R.id.action_list_random:
                if (item.isChecked()) return true;
                item.setChecked(true);

                setToolbarTitle(ListingType.RANDOM);
                getPresenter().onListingTypeChange(ListingType.RANDOM);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getPresenter().shouldConfirmExit()) {
            DialogFactory.newInstance(DialogFactory.DIALOG_CONFIRM_EXIT).show(getSupportFragmentManager(), "confirm_exit_dialog");
        } else {
            super.onBackPressed();
        }
    }

    @NonNull
    @Override
    public MainPresenter providePresenter() {
        return new MainPresenter(this);
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

    @Override
    public void setToolbarTitle(ListingType listingType) {
        Timber.d("Setting toolbar title to type %s", listingType.name());
        assert getSupportActionBar() != null;
        switch (listingType) {
            case FRONT_PAGE:
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.frontpage)));
                break;
            case LATEST_IMAGES:
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.latest_images)));
                break;
            case LATEST_VIDEOS:
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.latest_videos)));
                break;
            case LATEST_ALL:
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.latest_all)));
                break;
            case RANDOM:
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.random)));
                break;
        }
    }
}

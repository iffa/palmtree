package xyz.santeri.palmtree.ui.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import xyz.santeri.palmtree.base.DetailsService;
import xyz.santeri.palmtree.base.ListingService;
import xyz.santeri.palmtree.data.model.ListingType;
import xyz.santeri.palmtree.ui.listing.ListingFragment;

/**
 * @author Santeri Elo
 */
public class MainActivity extends TiActivity<MainPresenter, MainView> implements MainView {
    @Inject
    DetailsService detailsService;

    @Inject
    ListingService listingService;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.get(this).component().inject(this);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, ListingFragment.newInstance())
                    .commit();

            //noinspection ConstantConditions
            getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.frontpage)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        assert getSupportActionBar() != null; // damn I love doing this
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: Settings
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
                getSupportActionBar().setTitle(getString(R.string.activity_main_title, getString(R.string.latest_videos)));
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
        Snackbar.make(toolbar, R.string.app_update, Snackbar.LENGTH_INDEFINITE).show();
    }
}

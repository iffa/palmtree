package xyz.santeri.palmtree.di.component;

import javax.inject.Singleton;

import dagger.Component;
import xyz.santeri.palmtree.App;
import xyz.santeri.palmtree.data.DataManager;
import xyz.santeri.palmtree.di.module.AppModule;
import xyz.santeri.palmtree.ui.detail.DetailPresenter;
import xyz.santeri.palmtree.ui.listing.ListingPresenter;
import xyz.santeri.palmtree.ui.main.MainActivity;
import xyz.santeri.palmtree.ui.main.MainPresenter;
import xyz.santeri.palmtree.ui.settings.SettingsFragment;

/**
 * @author Santeri Elo
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(App app);

    void inject(MainActivity activity);

    void inject(SettingsFragment settingsFragment);

    void inject(MainPresenter mainPresenter);

    void inject(ListingPresenter listingPresenter);

    void inject(DetailPresenter detailPresenter);

    DataManager dataManager();
}

package xyz.santeri.palmtree;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

import io.github.prashantsolanki3.shoot.Shoot;
import timber.log.Timber;
import xyz.santeri.palmtree.data.DataManager;
import xyz.santeri.palmtree.di.component.AppComponent;
import xyz.santeri.palmtree.di.component.DaggerAppComponent;
import xyz.santeri.palmtree.di.module.AppModule;

/**
 * @author Santeri Elo
 */
public class App extends Application {
    public static final String SHOOT_UPDATE_TAG = "APP_UPDATE";
    public static final String SHOOT_LISTING_QUALITY = "LISTING_QUALITY";
    public static final String SHOOT_DETAILS_SWIPE = "DETAILS_SWIPE";
    private AppComponent component;
    private RefWatcher refWatcher;

    @Inject
    DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        component().inject(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);

        Timber.plant(new Timber.DebugTree());

        Shoot.with(this);

        //noinspection WrongConstant
        AppCompatDelegate.setDefaultNightMode(dataManager.getTheme());
    }

    public RefWatcher refWatcher() {
        return refWatcher;
    }

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public AppComponent component() {
        if (component == null) {
            component = DaggerAppComponent.builder()
                    .appModule(new AppModule(this))
                    .build();
        }
        return component;
    }
}

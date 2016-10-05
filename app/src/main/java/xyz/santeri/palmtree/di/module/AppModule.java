package xyz.santeri.palmtree.di.module;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xyz.santeri.palmtree.base.DetailsService;
import xyz.santeri.palmtree.base.ListingService;
import xyz.santeri.palmtree.data.remote.JsoupDetailsService;
import xyz.santeri.palmtree.data.remote.JsoupListingService;
import xyz.santeri.palmtree.di.AppContext;

/**
 * @author Santeri Elo
 */
@Module
public class AppModule {
    private final Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Application provideApp() {
        return app;
    }

    @Provides
    @Singleton
    @AppContext
    Context provideContext() {
        return app;
    }

    @Provides
    @Singleton
    ConnectivityManager provideConnectivityManager(@AppContext Context context) {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    @Singleton
    ListingService provideListingService() {
        return new JsoupListingService();
    }

    @Provides
    @Singleton
    DetailsService provideDetailsService() {
        return new JsoupDetailsService();
    }
}

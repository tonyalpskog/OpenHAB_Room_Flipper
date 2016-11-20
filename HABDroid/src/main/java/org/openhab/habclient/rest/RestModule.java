package org.openhab.habclient.rest;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@Module
public abstract class RestModule {
    @Provides @Singleton
    public OkHttpClient provideOkHttp() {
        return new OkHttpClient();
    }

    @Provides @Singleton @Named("default")
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient) {
        //TODO: add baseUrl?
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://demo.openhab.org:8443/rest/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

    }

    @Provides @Singleton @Named("myOpenHab")
    public static Retrofit provideMyOpenHabRetrofit(OkHttpClient okHttpClient) {
        //TODO: add baseUrl?
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://my.openhab.org/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    @Provides @Singleton
    public static OpenHabService provideOpenHabService(@Named("default") Retrofit retrofit) {
        return retrofit.create(OpenHabService.class);
    }

    @Provides @Singleton
    public static MyOpenHabService provideMyOpenHabService(@Named("myOpenHab") Retrofit retrofit) {
        return retrofit.create(MyOpenHabService.class);
    }
}

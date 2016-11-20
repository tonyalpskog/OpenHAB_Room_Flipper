package org.openhab.habclient.rest;


import org.openhab.domain.model.OpenHABSitemap;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OpenHabService {
    @GET("static/uuid")
    Single<String> getUUID();

    @GET("static/version")
    Single<String> getVersion();

    @GET("static/secret")
    Single<String> getSecret();

    @GET("{pageUrl}")
    Single<String> getPage(@Path("pageUrl") String pageUrl);

    @GET("sitemaps")
    Observable<OpenHABSitemap> getSiteMap();

    @GET("sitemaps/{sitemap}")
    Single<OpenHABSitemap> getSiteMap(@Path("sitemap") String sitemap);

    @POST("{link}")
    Completable post(@Path("link") String link, @Body String content);

    @POST("items/{itemName}")
    Completable postItem(@Path("itemName") String itemName, @Body String content);
}

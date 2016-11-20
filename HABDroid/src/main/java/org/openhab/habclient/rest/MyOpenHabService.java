package org.openhab.habclient.rest;


import io.reactivex.Completable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MyOpenHabService {
    @POST("addAndroidRegistration")
    Completable addRegistration(@Query("deviceId") String deviceId,
                                       @Query("deviceModel") String deviceModel,
                                       @Query("regid") String regId);
}

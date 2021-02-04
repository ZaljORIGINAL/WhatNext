package com.zalj.schedule.VersionControl;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface VersionApi {
    @GET("version.json")
    Call<Version> getVersionParams();

    @GET("update.apk")
    Call<ResponseBody> downloadAPKFile();
}

package com.zalj.schedule.VersionControl;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface VersionApi {
    @GET("version.json")
    Call<Version> getVersionParams();

    @GET("https://vk.com/{name}")
    Call<ResponseBody> downloadAPKFile(@Path("name") String name);
}

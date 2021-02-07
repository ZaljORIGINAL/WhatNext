package com.zalj.schedule.VersionControl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.zalj.schedule.BuildConfig;
import com.zalj.schedule.VersionControl.Exceptions.NoMemoryException;
import com.zalj.schedule.VersionControl.Exceptions.VersionNotReceivedException;

import java.io.File;
import java.io.FileOutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VersionManager {
    private static VersionManager manager;

    private Context context;
    private Retrofit retrofit;
    private Version version;
    private VersionApi api;

    public static VersionManager getInstance(Context context){
        if (manager == null){
            manager = new VersionManager(context);
        }
        return manager;
    }

    private VersionManager(Context context) {
        this.context = context;
        retrofit = new Retrofit.Builder()
                .baseUrl("https://scheduleofstudent.000webhostapp.com/VersionApp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(VersionApi.class);
    }

    /**Возвращаем данные о новой версии полученные с сервера*/
    public Version getVersion() throws VersionNotReceivedException {
        if (version == null){
            throw new VersionNotReceivedException("Ошибка в возвращении данные о новой версии. " +
                        "Возможно неполадки с сервером или же интеренет соединением");
        }else {
            return version;
        }
    }

    /**Проверка актуальной версии. Производится запрос на сервре и получаем JSON
     * данные об актуальной версии. Если действующая врсия устарела возвращается
     * true, если мы используем актуальную версию то false.*/
    public void checkVersion(CallBack callBack){

        Call<Version> data = api.getVersionParams();
        data.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(Call<Version> call, Response<Version> response) {
                if (response.isSuccessful()){
                    Log.i("UpdateAap", "Соединение с сервером установлено исправно");
                    version = response.body();

                    if (version.getVersionCode() > BuildConfig.VERSION_CODE)
                        Log.i("UpdateAap", "На сервере обнаружена новая версия приложения. Установлено на устройстве: " + BuildConfig.VERSION_CODE + ". Версия на сервере: " + version.getVersionCode());
                        callBack.run(true, version);

                }else{
                    Log.i("UpdateAap", "Соединение с сервером провалено. Код ошибка: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {
                Log.i("ServerAnswer", "We have error");
            }
        });
    }

    public void updateApp(Context context) throws NoMemoryException{
        checkFreeMemory();

        final File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "update.apk");

        //TODO Сейчас мы удаляем установщик прошлой версии, перед установкой новой.
        // Надо сделать это после первого запука приложения
        if (file.exists())
            file.delete();

        Call<ResponseBody> apk = api.downloadAPKFile(version.getNameOfPack());

        Log.i("UpdateAap", "Скачивание apk файла");
        apk.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    stream.write(response.body().bytes());
                    stream.close();

                    Log.i("UpdateAap", "Скачивание apk файла завершено");
                    installAPK(context);
                }catch (Exception e){
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    private void checkFreeMemory() throws NoMemoryException{
        Log.i("UpdateAap", "Проверка свободного мместа");
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long freeMemory = stat.getBlockSizeLong() * stat.getBlockCountLong();
        Log.i("UpdateAap", "Всего свободного места: " + freeMemory);

        if (freeMemory < version.getMemory()){
            throw new NoMemoryException(
                    "Недостаточно места для скачивания установщика",
                    version.getMemory() - freeMemory);
        }
    }

    private void installAPK(Context context){
        Log.i("UpdateAap", "Установка обновления");
        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "update.apk");
        if (file.exists()){
            Log.i("UpdateAap", "APK готово к установке");
            Uri uri = Uri.fromFile(file);
            InstallHelper installHelper = new InstallHelper();
            Log.i("UpdateAap", "Старт установки");
            installHelper.update(context, uri);
        }else{
            Log.i("UpdateAap", "APK не обнаружено по следующему пути:" + file.toString());
        }

        Log.i("UpdateAap", "Конец обновления");
    }
}

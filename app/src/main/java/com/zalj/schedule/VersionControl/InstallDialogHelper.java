package com.zalj.schedule.VersionControl;

import android.widget.ProgressBar;
import android.widget.TextView;

public class InstallDialogHelper {
    private static InstallDialogHelper helper;

    private static final int SERVER_CONNECTION_PROCESS = 0;
    private static final int CHECKING_THE_FREE_MEMORY = 1;
    private static final int DOWNLOAD_INSTALLER = 3;
    private static final int START_OF_INSTALLATION = 4;


    private TextView status;
    private ProgressBar progressBar;

    public static InstallDialogHelper getInstance(TextView status, ProgressBar progressBar){
        if (helper == null){
            helper = new InstallDialogHelper(status, progressBar);
            return helper;
        }else {
            return helper;
        }
    }

    private InstallDialogHelper(TextView status, ProgressBar progressBar){
        this.status = status;
        this.progressBar = progressBar;
    }

    public void listenStatusChange(int status){
        switch (status){
            case SERVER_CONNECTION_PROCESS:
                this.status.setText("Процесс подключения к серверу");
                progressBar.setProgress(10);
                break;

            case CHECKING_THE_FREE_MEMORY:
                this.status.setText("Проверка свободного пространства");
                progressBar.setProgress(10);
                break;

            case DOWNLOAD_INSTALLER:
                this.status.setText("Скачивание установщика");
                break;

            case START_OF_INSTALLATION:
                this.status.setText("Начало установки");
                break;
        }
    }

    private void changeStatusText(int status){

    }

    public void statusBarChanger(){

    }
}

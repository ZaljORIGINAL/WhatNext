package com.zalj.schedule.VersionControl.Exceptions;

import androidx.annotation.Nullable;

public class VersionNotReceivedException extends Exception{

    public VersionNotReceivedException(String message){
        super(message);
    }

    @Nullable
    @Override
    public String getMessage() {
        return "Ошибка: VersionNotReceivedException." +
                " Сообщение: " + super.getMessage();
    }
}

package com.zalj.schedule.VersionControl.Exceptions;

import androidx.annotation.Nullable;

public class NoMemoryException extends Exception {
    private long shortage;

    public NoMemoryException(String message, long shortage){
        super(message);
        this.shortage = shortage;
    }

    @Nullable
    @Override
    public String getMessage() {
        return "Ошибка: NoMemoryException." +
                " Сообщение: " + super.getMessage() +
                " Нехватающий объем: " + shortage;
    }
}

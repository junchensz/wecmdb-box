package com.webank.cmdb.wecmdbbox.utils;

public class RemoteInvokeException extends RuntimeException {
    public RemoteInvokeException(String message){
        super(message);
    }

    public RemoteInvokeException(String message,Throwable cause){
        super(message,cause);
    }
}

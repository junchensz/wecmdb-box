package com.webank.cmdb.wecmdbbox.utils;

import java.util.Collection;

public class AssertUtils {
    public static void haveSameLength(Collection col1,Collection col2, String errorMsg){
        if(col1 == null || col2 == null){
            throw new IllegalArgumentException("Collections should not be null.");
        }

        if(col1.size() != col2.size()){
            throw new IllegalStateException(errorMsg);
        }
    }
}

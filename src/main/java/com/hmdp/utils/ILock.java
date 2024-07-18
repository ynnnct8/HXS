package com.hmdp.utils;

public interface ILock {

    // timeoutSec：超时时间
    boolean tryLock(long timeoutSec);

    void unLock();
}

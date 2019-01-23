package com.supereal.bigfile.utils;

import java.util.Random;

/**
 * Create by tianci
 * 2019/1/10 14:59
 */
public class KeyUtil {

    public static synchronized String genUniqueKey() {
        Random random = new Random();
        Integer num = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(num);
    }
}

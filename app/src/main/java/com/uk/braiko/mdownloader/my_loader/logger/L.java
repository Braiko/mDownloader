package com.uk.braiko.mdownloader.my_loader.logger;

import android.util.Log;

/**
 * Created by yura on 7/15/2014.
 */
public class L {

    public static boolean isNeedLogs() {
        //todo тут я отримую хеш підпсу чи ще щось таке і
        //todo якщо він релізний повертаю фолс... Якби був градл можна було б по іншому
        return true;
    }

    public static void info(String msg, logTag... tag) {
        if (!isNeedLogs())
            return;
        Log.i(generateSingleTag(tag), msg);
    }

    public static void warm(String msg, logTag... tag) {
        if (!isNeedLogs())
            return;
        Log.w(generateSingleTag(tag), msg);
    }

    public static void error(String msg, Throwable error, logTag... tag) {
        if (!isNeedLogs())
            return;
        Log.e(generateSingleTag(tag), msg, error);
    }

    public static void debug(String msg, logTag... tag) {
        if (!isNeedLogs())
            return;
        Log.d(generateSingleTag(tag), msg);
    }

    private static String generateSingleTag(logTag[] tag) {
        String result = "";
        for (logTag t : tag)
            result += String.valueOf(t) + " :: ";
        return result;
    }
}

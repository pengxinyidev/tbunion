package com.example.taobaounion.utils;

import android.util.Log;

public class LogUtils {

    private static final int CURRENT_LEV = 4;
    private static final int DEBUG_LEV = 4;
    private static final int INFO_LEV = 3;
    private static final int WARNING_LEV = 2;
    private static final int ERROR_LEV = 1;

    public static void d(Object tag, String info) {
        if (CURRENT_LEV >= DEBUG_LEV) {
            Log.d(tag.getClass().getSimpleName(), info);
        }
    }

    public static void i(Object tag, String info) {
        if (CURRENT_LEV >= INFO_LEV) {
            Log.i(tag.getClass().getSimpleName(), info);
        }
    }
    public static void w(Object tag, String info) {
        if (CURRENT_LEV >= WARNING_LEV) {
            Log.w(tag.getClass().getSimpleName(), info);
        }
    }

    public static void e(Object tag, String info) {
        if (CURRENT_LEV >= ERROR_LEV) {
            Log.e(tag.getClass().getSimpleName(), info);
        }
    }


}

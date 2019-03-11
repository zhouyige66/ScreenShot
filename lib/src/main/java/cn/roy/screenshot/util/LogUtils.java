package cn.roy.screenshot.util;

import android.content.Context;
import android.util.Log;

/**
 * @Description: 
 * @Author: Roy Z
 * @Date: 2019/2/27 10:19
 * @Version: v1.0
 */
public class LogUtils {
    private static String TAG = "kk20";
    private static boolean DEBUG = true;
    private static boolean SHOW_MORE_INFO = false;

    private LogUtils() {
    }

    public static void init(Context context, String tag, boolean debug) {
        TAG = tag;
        DEBUG = debug;
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        LogUtils.TAG = TAG;
    }

    public static boolean isDEBUG() {
        return DEBUG;
    }

    public static void setDEBUG(boolean DEBUG) {
        LogUtils.DEBUG = DEBUG;
    }

    public static boolean isShowMoreInfo() {
        return SHOW_MORE_INFO;
    }

    public static void setShowMoreInfo(boolean showMoreInfo) {
        SHOW_MORE_INFO = showMoreInfo;
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void v(String msg) {
        if (DEBUG) {
            Log.v(TAG, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (DEBUG) {
            Log.i(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (DEBUG) {
            Log.w(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (DEBUG) {
            Log.e(TAG, msg);
        }
    }

}

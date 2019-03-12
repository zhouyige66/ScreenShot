package cn.roy.screenshot.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Description: 设备尺寸工具
 * @Author: Roy Z
 * @Date: 2019/3/12 09:50
 * @Version: v1.0
 */
public class DeviceSizeUtil {

    /**
     * 获取屏幕高度（包含状态栏、导航栏）
     *
     * @param context
     * @return
     */
    public static int getRealDisplayHeight(@NonNull Context context) {
        int height = 0;
        WindowManager windowManager = (WindowManager)
                context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            Class c = Class.forName("android.view.Display");
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            height = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return height;
    }

    /**
     * 获取应用显示区高度
     *
     * @param activity
     * @return
     */
    public static int getDefaultDisplayHeight(@NonNull Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取应用显示区宽度和高度
     *
     * @param activity
     * @return
     */
    public static int[] getDefaultDisplayParams(@NonNull Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int[] size = {dm.widthPixels, dm.heightPixels};
        return size;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight(@NonNull Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if (statusBarHeight == 0) {
            try {
                Class c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = activity.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    /**
     * 获取虚拟按键导航栏高度
     *
     * @return
     */
    public static int getNavigationBarHeight(Activity activity) {
        int height = getNavigationBarHeight1(activity);
        if (height == -1) {
            height = getNavigationBarHeight2(activity);
        }
        return height;
    }

    /**
     * 获取虚拟按键导航栏高度(方法一)
     *
     * @return
     */
    public static int getNavigationBarHeight1(@NonNull Activity activity) {
        int height = getRealDisplayHeight(activity);
        if (height == 0) {
            return -1;
        }
        int vh = height - getDefaultDisplayHeight(activity);
        return vh;
    }

    /**
     * 获取虚拟按键导航栏高度（方法二）
     *
     * @return
     */
    public static int getNavigationBarHeight2(@NonNull Activity activity) {
        if (!isNavigationBarShow(activity)) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 判断导航栏是否显示
     *
     * @return
     */
    public static boolean isNavigationBarShow(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

}

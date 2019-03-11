package cn.roy.screenshot;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import cn.roy.screenshot.R;
import cn.roy.screenshot.remote.ScreenShotServerUtil;

/**
 * @Description: 截屏请求授权页面
 * @Author: Roy Z
 * @Date: 2019/2/27 10:19
 * @Version: v1.0
 */
public class PermissionGrantActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 10000;
    private ScreenShotServerUtil mScreenShotServerUtil;
    private MediaProjectionManager mMediaProjectionManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (captureScreenAvailable()) {
            mScreenShotServerUtil = ScreenShotServerUtil.getInstance();

            //请求授权
            mMediaProjectionManager = (MediaProjectionManager)
                    getSystemService(MEDIA_PROJECTION_SERVICE);
            Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent, REQUEST_CODE);
            overridePendingTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit);
        } else {
            Toast.makeText(this, "Android 5.0以下版本不支持此方法获取屏幕数据",
                    Toast.LENGTH_SHORT).show();
            close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            if (captureScreenAvailable()) {
                mScreenShotServerUtil.mMediaProjection =
                        mMediaProjectionManager.getMediaProjection(resultCode, data);
                if (mScreenShotServerUtil.mImageReader == null) {
                    initSyncScreenUtil();
                }
                if (mScreenShotServerUtil.mImageReader == null) {
                    return;
                }
                mScreenShotServerUtil.mVirtualDisplay = mScreenShotServerUtil.mMediaProjection
                        .createVirtualDisplay("ScreenRecord",
                                (int) (mScreenShotServerUtil.mDisplayWidth * 1.0),
                                (int) (mScreenShotServerUtil.mRealDisplayHeight * 1.0),
                                mScreenShotServerUtil.mScreenDensityDpi,
                                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                                mScreenShotServerUtil.mImageReader.getSurface(),
                                null, null);
            }
        }

        close();
    }

    private boolean captureScreenAvailable() {
        return Build.VERSION.SDK_INT > 20;
    }

    private void initSyncScreenUtil() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenShotServerUtil.mDisplayWidth = dm.widthPixels;
        mScreenShotServerUtil.mDisplayHeight = dm.heightPixels;
        mScreenShotServerUtil.mScreenDensityDpi = dm.densityDpi;
        mScreenShotServerUtil.mRealDisplayHeight = getRealDisplayHeight();
        mScreenShotServerUtil.mStatusBarHeight = getStatusBarHeight();
        mScreenShotServerUtil.mNavigationBarHeight = getNavigationBarHeight();
        mScreenShotServerUtil.initImageReader();
    }

    private void close() {
        finish();
        overridePendingTransition(R.anim.anim_activity_enter, R.anim.anim_activity_exit);
    }

    /**
     * 获取屏幕高度（包含状态栏、导航栏）
     *
     * @return
     */
    private int getRealDisplayHeight() {
        int height = 0;
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
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
     * @return
     */
    private int getDefaultDisplayHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    private int getStatusBarHeight() {
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        if (statusBarHeight == 0) {
            try {
                Class c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = getResources().getDimensionPixelSize(x);
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
    private int getNavigationBarHeight() {
        int height = getNavigationBarHeight1();
        if (height == -1) {
            height = getNavigationBarHeight2();
        }
        return height;
    }

    /**
     * 获取虚拟按键导航栏高度(方法一)
     *
     * @return
     */
    private int getNavigationBarHeight1() {
        int height = getRealDisplayHeight();
        if (height == 0) {
            return -1;
        }
        int vh = height - getDefaultDisplayHeight();
        return vh;
    }

    /**
     * 获取虚拟按键导航栏高度（方法二）
     *
     * @return
     */
    private int getNavigationBarHeight2() {
        if (!isNavigationBarShow()) {
            return 0;
        }
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen",
                "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    /**
     * 判断导航栏是否显示
     *
     * @return
     */
    private boolean isNavigationBarShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

}

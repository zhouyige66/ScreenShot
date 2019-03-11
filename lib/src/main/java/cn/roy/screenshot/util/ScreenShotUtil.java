package cn.roy.screenshot.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

import cn.roy.screenshot.IScreenShotAidlInterface;
import cn.roy.screenshot.remote.ScreenShotService;

/**
 * @Description: 截图工具
 * @Author: Roy Z
 * @Date: 2019/2/27 09:58
 * @Version: v1.0
 */
public class ScreenShotUtil implements ServiceConnection, IScreenShot,
        Application.ActivityLifecycleCallbacks {
    private IScreenShotAidlInterface screenShotAidlInterface = null;
    private Context applicationContext = null;
    private Activity currentActivity;

    private ScreenShotUtil() {

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LogUtils.i("远程服务已连接：" + service.toString());
        screenShotAidlInterface = IScreenShotAidlInterface.Stub.asInterface(service);
        getScreenBitmap2();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtils.i("远程服务断开");
        screenShotAidlInterface = null;
    }

    /**
     * 静态内部类
     * 因为一个ClassLoader下同一个类只会加载一次，保证了并发时不会得到不同的对象
     */
    public static class SingletonInnerHolder {
        public static ScreenShotUtil mInstance = new ScreenShotUtil();
    }

    public static ScreenShotUtil getInstance() {
        return SingletonInnerHolder.mInstance;
    }

    private boolean isScreenShotServiceAvailable() {
        return screenShotAidlInterface != null;
    }

    private void bindService() {
        if (isScreenShotServiceAvailable()) {
            return;
        }

        if (applicationContext == null) {
            throw new RuntimeException("applicationContext == null," +
                    "please use method bindContext(Context context) at first!");
        }

        Intent intent = new Intent(applicationContext, ScreenShotService.class);
        applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void bindContext(Context context) {
        if (applicationContext == null) {
            applicationContext = context.getApplicationContext();
            ((Application) applicationContext).registerActivityLifecycleCallbacks(this);
        }
    }

    @Override
    public Bitmap getScreenBitmap() {
        View view = currentActivity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        Bitmap bitmapCopy = Bitmap.createBitmap(bitmap);
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmapCopy;
    }

    @Override
    public Bitmap getScreenBitmap2() {
        if (isScreenShotServiceAvailable()) {
            try {
                return screenShotAidlInterface.getScreenBitmap();
            } catch (RemoteException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            bindService();
            return null;
        }
    }

    @Override
    public void stopRecordScreen() {
        if (isScreenShotServiceAvailable()) {
            try {
                screenShotAidlInterface.stopRecordScreen();
                screenShotAidlInterface = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            if (applicationContext != null) {
                applicationContext.unbindService(this);
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        LogUtils.i("onActivityCreated:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        LogUtils.i("onActivityStarted:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        LogUtils.i("onActivityResumed:" + activity.getClass().getSimpleName());
        currentActivity = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        LogUtils.i("onActivityPaused:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        LogUtils.i("onActivityStopped:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        LogUtils.i("onActivitySaveInstanceState:" + activity.getClass().getSimpleName());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        LogUtils.i("onActivityDestroyed:" + activity.getClass().getSimpleName());
    }

}

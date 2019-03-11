package cn.roy.screenshot.remote;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import cn.roy.screenshot.IScreenShotAidlInterface;
import cn.roy.screenshot.util.LogUtils;

/**
 * @Description: 截屏服务（远程调用）
 * @Author: Roy Z
 * @Date: 2019/2/27 10:19
 * @Version: v1.0
 */
public class ScreenShotService extends Service {
    private ScreenShotServerUtil screenShotServerUtil = ScreenShotServerUtil.getInstance();

    private IBinder iBinder = new IScreenShotAidlInterface.Stub() {

        @Override
        public Bitmap getScreenBitmap() throws RemoteException {
            return screenShotServerUtil.getScreenBitmap();
        }

        @Override
        public Bitmap getScreenBitmap2() throws RemoteException {
            return screenShotServerUtil.getScreenBitmap2();
        }

        @Override
        public void stopRecordScreen() throws RemoteException {
            screenShotServerUtil.stopRecordScreen();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("onCreate");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        LogUtils.i("onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i("onBind：" + iBinder.toString());
        screenShotServerUtil.bindContext(this);
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.i("onUnbind");
        // IBinder对象置空
        iBinder = null;

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i("onDestroy");
    }

}

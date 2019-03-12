package cn.roy.screenshot.remote;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;

import java.nio.ByteBuffer;

import cn.roy.screenshot.PermissionGrantActivity;
import cn.roy.screenshot.util.IScreenShot;
import cn.roy.screenshot.util.LogUtils;

/**
 * @Description: 截屏服务远程工具类
 * @Author: Roy Z
 * @Date: 2019/2/27 10:19
 * @Version: v1.0
 */
public class ScreenShotServerUtil implements IScreenShot {
    private Context mApplicationContext = null;
    // 屏幕参数
    public int mDisplayWidth, mDisplayHeight, mScreenDensityDpi, mRealDisplayHeight,
            mStatusBarHeight, mNavigationBarHeight;
    public boolean isNavigationBarShow = false;
    // android 5.0以上获取屏幕需要参数
    public MediaProjection mMediaProjection;
    public ImageReader mImageReader;
    public VirtualDisplay mVirtualDisplay;
    // 保存图片格式
    public Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;
    public int mBitmapFormat = PixelFormat.RGBA_8888;

    private ScreenShotServerUtil() {

    }

    private static ScreenShotServerUtil instance = null;

    public static ScreenShotServerUtil getInstance() {
        if (instance == null) {
            synchronized (ScreenShotServerUtil.class) {
                if (instance == null) {
                    instance = new ScreenShotServerUtil();
                }
            }
        }
        return instance;
    }

    private boolean needGrantPermission() {
        return mMediaProjection == null || mImageReader == null || mVirtualDisplay == null;
    }

    public void initImageReader() {
        if (Build.VERSION.SDK_INT > 18) {
            LogUtils.i("高度：" + mDisplayHeight + "/" + mRealDisplayHeight);
            mImageReader = ImageReader.newInstance(mDisplayWidth,
                    (int) (mRealDisplayHeight * 1.0), mBitmapFormat, 3);
        }
    }

    @Override
    public void bindContext(Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @TargetApi(21)
    @Override
    public Bitmap getScreenBitmap() {
        if (needGrantPermission()) {
            //启动授权页面
            Intent intent = new Intent(mApplicationContext, PermissionGrantActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mApplicationContext.startActivity(intent);
            return null;
        }

        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height,
                mBitmapConfig);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();

        // 找到第一个不透明的像素和最后一个不透明像素之间的内容
//        int[] pixel = new int[width];
//        bitmap.getPixels(pixel, 0, width, 0, 0, width, 1);
//        int leftPadding = 0;
//        int rightPadding = width;
//        for (int i = 0; i < pixel.length; i++) {
//            int color = pixel[i];
//            int red = (color & 0xff0000) >> 16;
//            int green = (color & 0x00ff00) >> 8;
//            int blue = (color & 0x0000ff);
//            if (red != 0 && green != 0 && blue != 0) {
//                leftPadding = i;
//                break;
//            }
//        }
//        for (int i = pixel.length - 1; i >= 0; i--) {
//            int color = pixel[i];
//            int red = (color & 0xff0000) >> 16;
//            int green = (color & 0x00ff00) >> 8;
//            int blue = (color & 0x0000ff);
//            if (red != 0 && green != 0 && blue != 0) {
//                rightPadding = i;
//                break;
//            }
//        }
//        bitmap = Bitmap.createBitmap(bitmap, leftPadding, 0, rightPadding - leftPadding, height);

        return bitmap;
    }

    @Override
    public Bitmap getScreenBitmap2() {
        return null;
    }

    @TargetApi(21)
    @Override
    public void stopRecordScreen() {
        LogUtils.i("停止记录屏幕");
        if (mMediaProjection != null) {
            mMediaProjection.stop();

            mImageReader = null;
            mVirtualDisplay = null;
            mMediaProjection = null;
        }
    }

}

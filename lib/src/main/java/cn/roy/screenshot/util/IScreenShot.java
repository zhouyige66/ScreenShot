package cn.roy.screenshot.util;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * @Description:
 * @Author: Roy Z
 * @Date: 2019/2/27 09:58
 * @Version: v1.0
 */
public interface IScreenShot {

    void bindContext(Context context);

    Bitmap getScreenBitmap();

    Bitmap getScreenBitmap2();

    void stopRecordScreen();

}

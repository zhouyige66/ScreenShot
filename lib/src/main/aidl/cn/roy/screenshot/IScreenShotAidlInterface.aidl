// ISyncScreenAidlInterface.aidl
package cn.roy.screenshot;

// Declare any non-default types here with import statements
import android.graphics.Bitmap;

interface IScreenShotAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    Bitmap getScreenBitmap();

    Bitmap getScreenBitmap2();

    void stopRecordScreen();
}

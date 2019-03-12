# ScreenShot
## 简介
* 为Android应用提供获取当前屏幕图像的截屏工具。

## 原理
* 方式一：获取应用当前显示的Activity，通过Activity.getWindow().getDecorView()获取DecorView，利用View.getDrawingCache()方法获取当前页面图像，该方法不能获取应用外屏幕图片。
* 方式二：Android5.0及以上版本，可通过系统提供的MediaProjection API，获取屏幕图像，前提是要经过用户授权。
* 其他方式：比如调用系统隐藏的API，这个需要查看研究截屏等源码（未实现）。

## 其他特性
* 源码中使用了多进程，可供参考，若想不使用多进程方式，请download源码，进行相应修改。

## 使用方法
### Gradle配置:
```javascript
repositories {
	...
	maven { url 'https://jitpack.io' }
}
dependencies {
    // 其中Tag为release版本
	implementation 'com.github.zhouyige66:ScreenShot:Tag'
}
```

### 代码中使用
* 初始化ScreenShotUtil，调用ScreenShotUtil.bindContext(Context context)方法，建议在Application中初始化。
* 应用内截屏，可使用ScreenShotUtil.getScreenBitmap()，使用该方法不需要用户授权。
* 应用外截屏，使用ScreenShotUtil.getScreenBitmap2()方法，该方法需要用户授权。
* 获取的Bitmap的宽高与当前屏幕的整个宽高一致，所以如果需要除去状态栏或导航栏，请使用Bitmap裁剪方法，自行处理。该库提供了获取状态栏高度、导航栏高度等的工具类DeviceSizeUtil。
```java
// 判空操作是必须的
Bitmap bitmap = ScreenShotUtil.getScreenBitmap();
// 该方法第一次调用获取不到Bitmap数据，因为授权过后才能使用，
Bitmap bitmap2 = ScreenShotUtil.getScreenBitmap2();
```

----
## 关于作者
* Email： <751664206@qq.com>
* 有任何建议或者使用中遇到问题都可以给我发邮件。


package com.cyun.tracker.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.View;

import com.cyun.tracker.base.BaseActivity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;

public class VUtils {

    /**
     * 上次点击时间
     */
    private static long lastClickTime;
    private static long interval_time = 450;

    /**
     * 防止重复点击，需要加：click select itemClick
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < interval_time) {
            // showToast("按太快了");
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void setSwipeColor(SwipeRefreshLayout sLayout) {
        sLayout.setColorSchemeResources(
                android.R.color.holo_blue_dark,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
    }

    /**
     * 设置SwipyLayout的颜色
     */
    public static void setSwipyColor(SwipyRefreshLayout sLayout) {
        sLayout.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
    }


//    /**
//     * TODO 查看已下载的视频
//     */
//    public static void gotoVideo(Context context, String url, boolean isLocal) {
//        TsUtil.showToast(context, "rtsp视频");
//        Intent intent = new Intent(context, VideoActivity.class);
//        intent.putExtra("url", "file://" + path); // TODO url
//        intent.putExtra("isLocal", isLocal);
//        context.startActivity(intent);
//
//    }

//    /**
//     * TODO 查看已下载的大图
//     */
//    public static void gotoImage(Context context, String image_name) {
//        Intent intent = new Intent(context, ImageActivity.class);
//        intent.putExtra("path", image_name);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);
//
//    }

//    /**
//     * 提供ViewPager的ViewList，注意些Adapter的时候必须用view_pager_item
//     */
//    public static List<View> getViewPagerViewList(Context context) {
//        List<View> views = new ArrayList<View>();
//        for (int i = 0; i < 4; i++) {
//            View view = LayoutInflater.from(context.getApplicationContext()).inflate(
//                    R.layout.view_pager_item, null);
//            views.add(view);
//        }
//        return views;
//    }


//
//    private static int screenWidth = 0;
//    private static int screenHeight = 0;
//
//    /**
//     * 获取屏幕宽度
//     */
//    public static int getScreenWidth(Context context) {
//        if (screenWidth == 0) {
//            DisplayMetrics metrics = new DisplayMetrics();
//            ((Activity) context).getWindowManager().getDefaultDisplay()
//                    .getMetrics(metrics);
//            screenWidth = metrics.widthPixels;
//        }
//
//        return screenWidth; // 屏幕的宽
//    }
//
//    /**
//     * 获取屏幕高度
//     */
//    public static int getScreenHeight(Context context) {
//        if (screenHeight == 0) {
//            DisplayMetrics metrics = new DisplayMetrics();
//            ((Activity) context).getWindowManager().getDefaultDisplay()
//                    .getMetrics(metrics);
//            screenHeight = metrics.heightPixels;
//        }
//        return screenHeight;// 屏幕的高
//    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }


    /**
     * dp ---> px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * sp ---> px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px ---> dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(BaseActivity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = activity.getScreenWidth();
        int height = activity.getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;

    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(BaseActivity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = activity.getScreenWidth();
        int height = activity.getScreenHeight();
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return bp;

    }

}

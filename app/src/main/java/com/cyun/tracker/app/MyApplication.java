package com.cyun.tracker.app;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.os.Vibrator;

import com.baidu.mapapi.SDKInitializer;
import com.cyun.tracker.service.LocationService;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyApplication extends Application {

    /**
     * 通知的index
     */
    public static int notyId = 1;
    private static final String TAG = "MyApplication";
    private static MyApplication application;

    public static MyApplication getApplication() {
        if (null == application) {
            application = new MyApplication();
        }
        return application;
    }

    public int screenWidth = 0;
    public int screenheigth = 0;


    /**
     * 全部activity集合
     */
    public List<Activity> activityList; // 全部activity集合

    public LocationService locationService;
    public Vibrator mVibrator;


    @Override
    public void onCreate() {
        super.onCreate();
        application = this;// TODO 初始化一个？
//		refWatcher = LeakCanary.install(this); // bug生成

        activityList = new LinkedList<>();

//        boolean sdCardExist = Environment.getExternalStorageState().equals(
//                android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
//        if (sdCardExist) {
//            File file = new File(Finals.imageCachePath);
//            if (!file.exists()) {
//                boolean isCreated = file.mkdir();
//                if (!isCreated) {
//                    Log.i(TAG, "缓存目录创建失败");
//                    // TsUtil.showTip(getApplicationContext(), "缓存目录创建失败");
//                }
//            }
//        } else {
//            // TsUtil.showTip(getApplicationContext(), "未获取到sd卡");
//        }

        // 初始化OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                .readTimeout(20000L, TimeUnit.MILLISECONDS)
                .writeTimeout(20000l, TimeUnit.MILLISECONDS)
                //其他配置
                .build();

        OkHttpUtils.initClient(okHttpClient);

        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());

    }


    /**
     * 添加Activity到容器中
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 移除Activity到容器中
     */
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    public Activity getCurrentActivity() {
        Activity mAct = activityList.get(activityList.size() - 1);
        return mAct;
    }

    /**
     * 遍历所有Activity并finish
     */
    public void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        exit();
        activityList.clear();
        activityList = null;
    }

}

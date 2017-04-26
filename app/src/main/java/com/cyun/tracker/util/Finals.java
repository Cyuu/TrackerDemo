package com.cyun.tracker.util;

import android.os.Environment;

/**
 * 常量类
 */
public class Finals {


    public static final String TAG = "tracker";

    public static final boolean IS_TEST = true;    // 是否测试
    private static final String Path_TRACKER = Environment.getExternalStorageDirectory().getPath() + "/tracker"; // 文件缓存至磁盘路径
    public static final String FilePath = Path_TRACKER; // 文件缓存至磁盘路径

    public static final String Path_Cache = Path_TRACKER + "/cache";   // 缓存路径
    public static final String Path_Image = Path_TRACKER + "/image/";     // 图片存储路径

    // ------SharedPreferences变量--start-----

    public static final String SP_NAME = "tracker_sp";         // 名称
    public static final String SP_UID = "uid";              // 用户id
    public static final String SP_USERNAME = "username";    // 用户名称
    public static final String SP_PWD = "SP_PWD";
    public static final String SP_IP = "SP_IP";

    // -----SharedPreferences变量--end-----


    // ---------------- 各种url接口----------------
    /**
     * 测试下载apk的url(无秘v5.3)
     */
    public static final String Url_test_update = "http://220.195.19.16/dd.myapp.com/16891/526E19240B32BDF54BE778AB9520A724.apk";

    //
    public static final String Url_Http = "http://";             // 登录

    //
    public static final String Url_PORT = ":8088";          // 接口url的通用端口

    /**
     * TODO url头   ?sCmd=
     */
    public static final String Url_Head = "/WebService.asmx/RailwayForeignInvasionWebService";


    public static final String safeCodeTest = "DB:46:97:62:ED:A9:20:5F:F0:C5:64:28:07:DC:9A:20:FB:46:4A:3E;com.cyun.tracker";
    public static final String safeCodeOfficial = "D6:C4:CE:EB:F5:A1:9C:FE:D0:7F:E8:3C:81:5D:DF:76:27:C2:ED:64;com.cyun.tracker";

    public static final String Baidu_Ak = "ZxXdt4eYenSIeGcpiNhry5KFH3Mn3Fjz";
    // 先纬度，后经度
    public static final String Url_BaiduApi = "http://api.map.baidu.com/geocoder/v2/?callback=renderReverse&output=json&pois=1&mcode=" + safeCodeTest + "&ak=" + Baidu_Ak + "&location=";//39.983424,116.322987";

}

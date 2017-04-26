package com.cyun.tracker.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

public class SpUtil {

    /**
     * 取sp数据
     *
     * @param context
     * @param key
     */
    public static String getData(Context context, String key) {
        SharedPreferences sp = context
                .getSharedPreferences(Finals.SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 取sp数据
     *
     * @param context
     * @param key
     */
    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context
                .getSharedPreferences(Finals.SP_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }


    /**
     * 取uid数据
     */
    public static String getUid(Context context) {
        String value = "";
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(Finals.SP_NAME, Context.MODE_PRIVATE);
        value = sp.getString(Finals.SP_UID, "");
        return value;
    }

    /**
     * 取ip数据
     */
    public static String getIP(Context context) {
        String value = "";
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(Finals.SP_NAME, Context.MODE_PRIVATE);
        value = sp.getString(Finals.SP_IP, "");
        return value;
    }


    /**
     * 保存sp数据
     *
     * @param context
     * @param key
     * @param value
     */
    public static void save(Context context, String key, String value) {
        new SpSaveThread(context, key, value).run();
    }

    /**
     * 保存sp数据
     *
     * @param context
     * @param spMap
     */
    public static void save(Context context, Map<String, String> spMap) {
        Set<String> keySet = spMap.keySet();
        for (String key : keySet) {
            new SpSaveThread(context, key,
                    spMap.get(key)).run();
        }
    }

    /**
     * 保存字符串
     */
    public static class SpSaveThread extends Thread {
        public Context context;
        public String key;
        public String data;

        public SpSaveThread(Context context, String key, String data) {
            super();
            this.context = context;
            this.key = key;
            this.data = data;
        }

        @Override
        public void run() {
            super.run();
            SharedPreferences sp = context.getSharedPreferences(Finals.SP_NAME,
                    Context.MODE_PRIVATE);
            sp.edit().putString(key, data).commit();
        }
    }

}

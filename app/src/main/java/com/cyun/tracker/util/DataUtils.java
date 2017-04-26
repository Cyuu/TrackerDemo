package com.cyun.tracker.util;

import com.cyun.tracker.bean.TrackBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataUtils {

    private static final String TAG = "DataUtils";


    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }


    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param time1 时间参数 时间戳
     * @param time2 时间参数 时间戳
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(long time1, long time2) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }


    public static List<TrackBean> getTrackListTest() {
        List<TrackBean> trackList = new ArrayList<>();
        TrackBean bean = null;
        for (int i = 0; i < 30; i++) {
            bean = new TrackBean();
            bean.setTrackid(i + "");
            bean.setAddr("河北省保定市恒滨路" + (i + 1) + "号");
            bean.setContent("今天去美食街吃了第" + (i + 1) + "顿，邂逅第" + (i + 1) + "个妹子，留了联系方式。");
            bean.setPic("");
            bean.setTimeStamp(((System.currentTimeMillis() / 1000 / 60 / 60 - 2) / 24 - i) * 24 * 60 * 60 * 1000);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -(i + 1 ));
            bean.setDate((cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.DAY_OF_MONTH));
            bean.setTime(cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE));
            bean.setUser("用户名" + (i + 1));
            trackList.add(bean);
        }
        return trackList;
    }


    /**
     * 生成随即请求No，Scope:(1 - 65535)
     */
    public static String getCurrentTm() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

}

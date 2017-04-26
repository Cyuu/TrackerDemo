package com.cyun.tracker.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.cyun.tracker.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 */
public class Utils {

    /**
     * 检验是否是合法的IP地址
     */
    public static boolean ipCheck(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            if (text.matches(regex)) {
                return true;
            }
        }
        return false;
    }


    // 验证IP
    public static Boolean isIP(String str) {
        String regex = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    /**
     * 验证手机号码
     */
    public static boolean isMobile(String phoneNum) {
        String telRegex = "[1][0-9]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(phoneNum))
            return false;
        else
            return phoneNum.matches(telRegex);
    }


    /**
     * 获得系统当前时间，格式 yyyy-MM-dd HH:MM:ss
     */
    public static String getSysNowTime() {
        Date now = new Date();
        java.text.DateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String formatTime = format.format(now);
        return formatTime;
    }

    // 系统时间秒数 转换成当前时间
    public static String paserTime(long time) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String times = format.format(new Date(time * 1000L));
        Log.i("TAG", "日期格式---->" + times);
        return times;
    }


    /**
     * 版本名称
     */
    public static String getVersionName(Context context) {
        PackageInfo info = getPackageInfo(context.getApplicationContext());
        if (info == null) {
            return context.getResources().getString(R.string.version_name);
        }
        return info.versionName;
    }

    /**
     * 版本号
     */
    public static int getVersionCode(Context context) {
        PackageInfo info = getPackageInfo(context.getApplicationContext());
        if (info == null) {
            return 1;
        }
        return info.versionCode;
    }

    /**
     * 获取版本号
     */
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getApplicationContext().getPackageManager();
            pi = pm.getPackageInfo(context.getApplicationContext().getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pi;
    }


    /**
     * 获取版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getApplicationContext().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return context.getString(R.string.version_name);
        }
    }


    public static final void showResultDialog(Context context, String msg, String title) {
        if (msg == null)
            return;
        String rmsg = msg.replace(",", "\n");
        Log.d("Util", rmsg);
        new AlertDialog.Builder(context.getApplicationContext()).setTitle(title).setMessage(rmsg).setNegativeButton("知道了", null).create()
                .show();
    }

//    /**
//     * 根据一个网络连接(String)获取bitmap图像
//     */
//    public static Bitmap getbitmap(String imageUri) {
//        Log.v(TAG, "getbitmap:" + imageUri);
//        // 显示网络上的图片
//        Bitmap bitmap = null;
//        try {
//            URL myFileUrl = new URL(imageUri);
//            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
//            conn.setDoInput(true);
//            conn.connect();
//            InputStream is = conn.getInputStream();
//            bitmap = BitmapFactory.decodeStream(is);
//            is.close();
//            Log.v(TAG, "image download finished." + imageUri);
//        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
//            bitmap = null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.v(TAG, "getbitmap bmp fail---");
//            bitmap = null;
//        }
//        return bitmap;
//    }

//    /**
//     * MD5加密
//     * @param content 加密的内容
//     * @return 加密后返回的结果
//     */
//     public static String MD5(String content) {
//         String result = "";
//         try {
//             MessageDigest md = MessageDigest.getInstance("MD5");
//             md.update(content.getBytes());
//             byte b[] = md.digest();
//             int i;
//             StringBuffer buf = new StringBuffer("");
//             for (int offset = 0; offset < b.length; offset++) {
//                 i = b[offset];
//                 if (i < 0)
//                     i += 256;
//                 if (i < 16)
//                     buf.append("0");
//                 buf.append(Integer.toHexString(i));
//             }
//             result = buf.toString();
//         } catch (NoSuchAlgorithmException e) {
//             Log.i(TAG, e);
//         }
//         return result;
//     }

//    /**
//     * URL编码
//     */
//    public static String URLEncoder(String content) {
//        try {
//            return URLEncoder.encode(content, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * URL解码
//     */
//    public static String URLDecoder(String content) {
//        try {
//            return URLDecoder.decode(content, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    /**
//     * 数据流转换字符串
//     */
//    public static String inputStream2String(InputStream is) throws IOException {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        int i = -1;
//        while ((i = is.read()) != -1) {
//            baos.write(i);
//        }
//        return baos.toString();
//    }


//    /**
//     * 获取设备识别信息<br/>
//     * 已被 BaseActivity getIMEI() 代替
//     */
//    public static String getDeviceInfo(Context context) {
//        try {
//            org.json.JSONObject json = new org.json.JSONObject();
//            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager)
//                    context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//            String device_id = tm.getDeviceId();
//            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager)
//                    context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//            String mac = wifi.getConnectionInfo().getMacAddress();
//            json.put("mac", mac);
//            if (TextUtils.isEmpty(device_id)) {
//                device_id = mac;
//            }
//            if (TextUtils.isEmpty(device_id)) {
//                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
//                        android.provider.Settings.Secure.ANDROID_ID);
//            }
//            json.put("device_id", device_id);
//            return json.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }


//    /**
//     * 隐藏软键盘
//     */
//    public static void hideSoftKeyBoard(Activity activity) {
//        final View v = activity.getWindow().peekDecorView();
//        if (v != null && v.getWindowToken() != null) {
//            try {
//                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
//                        activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    /**
//     * 显示软键盘
//     */
//    public static void showSoftkeyboard(View view, ResultReceiver resultReceiver) {
//        Configuration config = view.getContext().getResources().getConfiguration();
//        if (config.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
//            InputMethodManager imm = (InputMethodManager) view.getContext()
//                    .getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (resultReceiver != null) {
//                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT, resultReceiver);
//            } else {
//                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//            }
//        }
//    }
//


    // // -----------保存问题-----------
    // private static final String FILENAME = "data.json";
    //
    // public void saveData() throws IOException {
    // File sdCard = Environment.getExternalStorageDirectory();
    // // 获取外部存储设备（SD卡）的路径
    // Log.i(TAG, sdCard.getAbsolutePath());
    // // 查看LogCat,获取的sd卡的绝对路径为 /storage/sdcard
    // sdCard = new File(sdCard, "/MyFiles");
    // sdCard.mkdirs();// 创建MyFiles目录(可创建多级目录)
    // sdCard = new File(sdCard, FILENAME);
    // FileOutputStream out = new FileOutputStream(sdCard);
    // Writer writer = new OutputStreamWriter(out);
    // try {
    // String str = "来自保存在内部存储设备的数据";
    // writer.write(str);
    // } finally {
    // writer.close();
    // }
    // }
    //
    // public void loadData() throws FileNotFoundException, IOException {
    // BufferedReader reader = null;
    // StringBuilder data = new StringBuilder();
    // try {
    // File sdCard = Environment.getExternalStorageDirectory();
    // sdCard = new File(sdCard, "/MyFiles/" + FILENAME);
    // FileInputStream in = new FileInputStream(sdCard);
    // reader = new BufferedReader(new InputStreamReader(in));
    // String line = new String();
    // while ((line = reader.readLine()) != null) {
    // data.append(line);
    // }
    // dataView.setText(data);
    // } catch (FileNotFoundException e) {
    // dataView.setText("没有发现保存的数据");
    // } finally {
    // reader.close();
    // }
    // }

//    /**
//     * 打开日志文件并写入日志
//     */
//    public static void writeLogtoFile(String tag, String text) {// 新建或打开日志文件
//        Date nowtime = new Date();
//        SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat myLogSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
//        String needWriteFiel = logfile.format(nowtime);
//        String needWriteMessage = myLogSdf.format(nowtime) + "    "
//                + "    " + tag + "    " + text;
//        File file = new File(Finals.LogStoragePath, needWriteFiel + "log.txt");
//        try {
//            FileWriter filerWriter = new FileWriter(file, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
//            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
//            bufWriter.write(needWriteMessage);
//            bufWriter.newLine();
//            bufWriter.close();
//            filerWriter.close();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}

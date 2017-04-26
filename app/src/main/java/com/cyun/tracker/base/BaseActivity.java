package com.cyun.tracker.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyun.tracker.R;
import com.cyun.tracker.app.MyApplication;
import com.cyun.tracker.util.TsUtil;
import com.cyun.tracker.view.ProgressWheel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Activity框架<br/>
 * (请求接口步骤)<br/>
 * 1 initQueue() <br/>
 * 2 拼接参数 先调用buildCommonHttpParams()，然后构建自己的私有参数buildParams4XX()，返回map <br/>
 * 3 发送请求 volleyPostRequest() <br/>
 * ------------------------- <br/>
 * 公共的loading 需要在每个页面的initView方法里，加上common_null_layout.setOnClickListener(this);
 * 然后再pressEvent()里，添加该控件的点击事件
 */
public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {

//    public final int CODE_RELOAD = 0; // 重新加载
//    public final int CODE_LOAD_NEW = 1; // 加载新内容
//    public final int CODE_LOAD_MORE = 2;// 加载更多

    public final String failTip = "连接服务器失败";
    public final String errorTip = "无相关数据";


    public MyApplication application;

    /**
     * 需要输入密码校验的对话框，里面的密码输入框
     */
    public EditText pEdit;
//    /**
//     * 需要输入密码校验的对话框，里面的处理意见输入框
//     */
//    public EditText opEdit;

//    public Unbinder unbinder;

    protected Context context;
    public ProgressDialog progressDialog = null;
    /**
     * 通过 android:theme 属性应用这个样式到你的 ProgressBar 。
     * <style name="CircularProgress" parent="Theme.AppCompat.Light">
     * <item name="colorAccent">@color/indigo</item>
     * </style>
     * android:theme="@style/CircularProgress"
     */
    public String TAG = this.getClass().getSimpleName();

    public RelativeLayout common_loading; // 整个加载View，RelativeLayout
    public TextView common_null_tv; // 图文提示 common_null_tv
    public ProgressWheel common_bar; // progressbar
    public InputMethodManager imm = null;

    private int screenWidth, screenHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        application = (MyApplication) getApplication();// 获取应用程序全局的实例引用
        application.addActivity(this);

        if (application.screenWidth == 0) {
            application.screenWidth = getScreenWidth();
        }

        if (application.screenWidth == 0) {
            application.screenheigth = getScreenHeight();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView();
        ButterKnife.bind(this);
        initLoadingView();

//        MyApplication.getRefWatcher(context).watch(this);

        if (common_null_tv != null) {
            common_null_tv.setOnClickListener(this);
        }
        initView(savedInstanceState);

    }


    /**
     * 获得屏幕宽度
     */
    public int getScreenWidth() {
        if (screenWidth == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenWidth = dm.widthPixels;
        }
        return screenWidth;

    }

    /**
     * 获得屏幕高度
     */
    public int getScreenHeight() {
        if (screenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            screenHeight = dm.heightPixels;
        }
        return screenHeight;
    }

    /**
     * setContentView
     */
    public abstract void setContentView();


    /**
     * get方式
     *
     * @param url      url
     * @param callback 回调方法
     */
    public void doRequestGet(String url, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(callback);
    }

    /**
     * post方式
     *
     * @param url      url
     * @param params   post参数
     * @param callback 回调方法
     */
    public void doRequestPost(String url, String params, StringCallback callback) {
        Log.i(TAG, "请求地址：" + url);
        OkHttpUtils
                .get()
                .url(url)
                .addParams("sCmd", params)
                .build()
                .execute(callback);
    }


    public void showProgressDialog() {
        showProgressDialog("正在加载...");
    }

    public void showProgressDialog(String txt) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.setMessage(txt);
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    /**
     * init view after setcontentView()
     */
    public abstract void initView(Bundle savedInstanceState);

    // @Subscribe
    @Override
    public abstract void onClick(View v);

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void finish() {
        // 之前是在这里做的清理Activity的操作，现转到ondestroy里
        super.finish();
        overridePendingTransition(0, R.anim.push_right_out);
    }


//    /**
//     * 获取imei
//     */
//    public void getIMEI() {
//        if (TextUtils.isEmpty(Finals.imei)) {
//            Log.i(TAG, "device_id=" + Utils.getDeviceInfo(context));
//            Finals.imei = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE))
//                    .getDeviceId();
//            SpUtil.save(context, Finals.SP_IMEI, Finals.imei);
//        }
//    }

    /**
     * 返回首页,todo,使用intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);即可实现，不需要这个方法。
     */
//    public void goToHome(Context context) {
//        MyApplication app = (MyApplication) getApplication();// 获取应用程序全局的实例引用
//        for (Activity act : app.activityList) {
//            String key = act.getClass().getName();
//            if (act != null && key != null
//                    && !key.equals("com.thdz.ywqx.ui.Activity.MainActivity")) {
//                act.finish();
//            }
//        }
//    }

//    /**
//     * 跳转登录页
//     */
//    public void goToLogin(Context context) {
//        Intent intent = new Intent(context, LoginActivity.class);
//        startActivity(intent);
//    }

    /**
     * 初始化加载时
     */
    public void loadInit() {
        common_loading.setVisibility(View.VISIBLE);
        common_bar.setVisibility(View.VISIBLE);
        common_null_tv.setVisibility(View.GONE);
    }

    /**
     * 加载成功
     */
    public void loadOK() {
        common_loading.setVisibility(View.GONE);
    }

    /**
     * 加载失败
     */
    public void loadFail() {
        common_loading.setVisibility(View.VISIBLE);
        common_bar.setVisibility(View.GONE);
        common_null_tv.setVisibility(View.VISIBLE);
    }

    /**
     * 实例化布局
     */
    private void initLoadingView() {
        common_loading = (RelativeLayout) findViewById(R.id.common_loading);
        common_bar = (ProgressWheel) findViewById(R.id.common_bar);
        common_null_tv = (TextView) findViewById(R.id.common_null_tv);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // MobclickAgent.onPause(context);// umeng统计
        // 统计页面(仅有Activity的应用中SDK自动调用，不�?要单独写)
        // MobclickAgent.onPageStart(context.getClass().getSimpleName()); //
        hideInputMethod();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (unbinder != null) {
//            unbinder.unbind();
//        }
        EventBus.getDefault().unregister(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

//        application.activityList.remove(this); // 把当前Activity从集合中移除
        application.removeActivity(this);

    }


    /**
     * 隐藏输入框
     */
    public void hideInputMethod() {
        if (imm != null) {
            View view = ((Activity) context).getCurrentFocus();
            if (view != null) {
                IBinder mBinder = view.getWindowToken();
                if (mBinder != null) {
                    imm.hideSoftInputFromWindow(mBinder, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (progressDialog != null && progressDialog.isShowing()) {
            hideProgressDialog();
            return;
        }
        super.onBackPressed();
    }


    /**
     * 设置title for Activity
     */
    public void setTitle(String title) {
        TextView titletv = (TextView) findViewById(R.id.title_tv);
        titletv.setText(title);
    }

    /**
     * 隐藏返回箭头 for Activity
     */
    public void setBackGone() {
        ImageView back = (ImageView) findViewById(R.id.left_img);
        back.setVisibility(View.GONE);
    }


    /**
     * 设置返回箭头动作
     */
    public void setBackActive() {
        ImageView back = (ImageView) findViewById(R.id.left_img);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 隐藏title栏右侧按钮 for Activity
     */
    public void setRightTopGone() {
        ImageView back = (ImageView) findViewById(R.id.right_img);
        back.setVisibility(View.INVISIBLE);
    }


    public void showToast(String info) {
        TsUtil.showToast(info);

    }

    public void showSnack(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
//        snackbar.setActionTextColor(Color.WHITE);//click的字体颜色
        View mView = snackbar.getView();
        mView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //内容的字体颜色与大小
        TextView tvSnackbarText = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        tvSnackbarText.setLineSpacing(10, 1);
        tvSnackbarText.setText(msg);
        tvSnackbarText.setTextColor(Color.WHITE);
        snackbar.show();
    }


    public void showSnack4Content(View view, String msg) {
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
//        snackbar.setActionTextColor(Color.WHITE);//click的字体颜色
        View mView = snackbar.getView();
        mView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //内容的字体颜色与大小
        TextView tvSnackbarText = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
        tvSnackbarText.setLineSpacing(10, 1);
        tvSnackbarText.setText(msg);
        tvSnackbarText.setTextColor(Color.WHITE);
        snackbar.show();
    }

}

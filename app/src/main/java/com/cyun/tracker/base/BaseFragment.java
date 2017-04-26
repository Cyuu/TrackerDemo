package com.cyun.tracker.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cyun.tracker.R;
import com.cyun.tracker.app.MyApplication;
import com.cyun.tracker.util.TsUtil;
import com.cyun.tracker.view.ProgressWheel;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Fragment框架<br/>
 * EventBus.getDefault().register(this);
 * EventBus.getDefault().unregister(this);
 */
public abstract class BaseFragment extends Fragment implements OnClickListener {

//    public final int CODE_RELOAD = 0; // 重新加载
//    public final int CODE_LOAD_NEW = 1; // 加载新内容
//    public final int CODE_LOAD_MORE = 2;// 加载更多

    public final String failTip = "连接服务器失败";
    public final String errorTip = "未获取到信息";

    public String TAG = this.getClass().getSimpleName();

    public MyApplication application;

    public Context context;
    public ProgressDialog progressDialog = null;

    public RelativeLayout common_loading; // 整个加载View，RelativeLayout
    public ProgressWheel common_bar; // progressbar
    public TextView common_null_tv; // 图文提示 common_null_tv

    /**
     * 需要输入密码校验的对话框，里面的密码输入框
     */
    public EditText pEdit;

    /**
     * fragment的父view
     */
    public View contentView;

    public InputMethodManager imm;
//    /**
//     * cancelAll使用tag，需要设置的话， 要在
//     */
//    public String request_tag;

//    public Unbinder unbinder;

//    public final int TIME_OUT_MS = 10000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        application = (MyApplication) getActivity().getApplication();
        if (null == contentView) {
            contentView = inflateView(inflater, container, savedInstanceState);
        }
//        unbinder = ButterKnife.bind(this, contentView);
        ButterKnife.bind(this, contentView);
//        MyApplication.getRefWatcher(context).watch(this);
        initLoadingView(contentView);
        // imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (common_null_tv != null) {
            common_null_tv.setOnClickListener(this);
        }
        initView(savedInstanceState, contentView, inflater);
        return contentView;
    }

    /**
     * 获取root view的layout id
     */
    public abstract View inflateView(LayoutInflater inflater, ViewGroup arg1, Bundle arg2);

    /**
     * 初始化Fragment
     */
    public abstract void initView(Bundle savedInstanceState, View view,
                                  LayoutInflater inflater);

    /**
     * 点击事件响应
     */
    @Override
    public abstract void onClick(View v);


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_right_in,
                R.anim.push_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.push_right_in,
                R.anim.push_left_out);
    }

//    /**
//     * 获取imei
//     */
//    public void getIMEI() {
//        if (TextUtils.isEmpty(Finals.imei)) {
//            Finals.imei = ((TelephonyManager) context
//                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
//        }
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
    private void initLoadingView(View view) {
        common_loading = (RelativeLayout) view.findViewById(R.id.common_loading);
        common_bar = (ProgressWheel) view.findViewById(R.id.common_bar);
        common_null_tv = (TextView) view.findViewById(R.id.common_null_tv);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        hideInputMethod();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

    }

    /**
     * 隐藏输入框
     */
    public void hideInputMethod() {
        if (imm != null) {
            if (contentView != null) {
                IBinder mBinder = contentView.getWindowToken();
                if (mBinder != null) {
                    imm.hideSoftInputFromWindow(mBinder,
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
    }

//	/**
//	 * 短显示Snackbar，自定义颜色
//	 */
//	public void ShowSnack(View view, String message, int messageColor, int backgroundColor) {
//		Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
//		View v = snackbar.getView();
//		if (v != null) {
//			v.setBackgroundColor(backgroundColor);
//			((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);
//		}
//		snackbar.show();
//	}
//
//	// 默认颜色的Snack
//	public void ShowSnack(View view, String message) {
//		int messageColor = getResources().getColor(R.color.white);
//		int bgColor = getResources().getColor(R.color.blue_color);
//		ShowSnack(view, message, messageColor, bgColor);
//	}

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

        progressDialog.show();
    }

    public void showProgressDialog(String txt, DialogInterface.OnClickListener sureListener) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.indicator_input_error));
            progressDialog.setMessage(txt);

            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", sureListener);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    hideProgressDialog();
                }
            });
        }

        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    /**
     * 设置title for Fragment
     */
    public void setTitle(View view, String title) {
        TextView titletv = (TextView) view.findViewById(R.id.title_tv);
        titletv.setText(title);
    }


    /**
     * 隐藏返回箭头title for Fragment
     */
    public void setBackGone(View view) {
        ImageView back = (ImageView) view.findViewById(R.id.left_img);
        back.setVisibility(View.GONE);
    }


    /**
     * 隐藏返回箭头title for Fragment
     */
    public void setRightTopGone(View view) {
        ImageView back = (ImageView) view.findViewById(R.id.right_img);
        back.setVisibility(View.INVISIBLE);
    }

    public void showToast(String info) {

        TsUtil.showToast(info);

    }

}

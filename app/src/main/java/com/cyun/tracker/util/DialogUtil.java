package com.cyun.tracker.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cyun.tracker.R;
import com.cyun.tracker.app.MyApplication;


public class DialogUtil {


    private static Dialog dialog = null;


    /**
     * 公共对话框，如果不传值则隐藏
     */
    public static void bottomDialog(final Context context, String title, String addr, String pointStr) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.my_dialog, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        TextView tv_name = getView(dialogView, R.id.tv_name);
        TextView tv_addr = getView(dialogView, R.id.tv_addr);
        TextView tv_point = getView(dialogView, R.id.tv_point);
        tv_name.setText("名称：" + title);
        tv_addr.setText("位置：" + addr);
        tv_point.setText("坐标：" + pointStr);


        Button btn_sure = (Button) dialogView.findViewById(R.id.btn_sure);
        Button btn_tip = (Button) dialogView.findViewById(R.id.btn_tip);
        dialog.show();
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TsUtil.showToast("弹出消息");
            }
        });
    }


    /**
     * 弹出对话框，询问是否XXX?
     */
    public static void sureDialog(final Context context, String title, View.OnClickListener clicker) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_sure, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.BOTTOM);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        TextView tv_title = getView(dialogView, R.id.tv_title);
        tv_title.setText(title);

        Button btn_sure = (Button) dialogView.findViewById(R.id.btn_sure);
        Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        dialog.show();

        btn_sure.setOnClickListener(clicker);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    /**
     * 弹出对话框，填写内容
     */
    public static void editDialog(final Context context, String title,
                                  final EditDialogSureListener editListener) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        //设置窗口宽度为充满全屏
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //设置窗口高度为包裹内容
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //将设置好的属性set回去
        window.setAttributes(lp);
        //将自定义布局加载到dialog上
        dialog.setContentView(dialogView);
        TextView tv_title = getView(dialogView, R.id.tv_title);
        tv_title.setText(title);
        final TextView et_content = getView(dialogView, R.id.et_content);


        Button btn_sure = (Button) dialogView.findViewById(R.id.btn_sure);
        Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        dialog.show();

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListener.getContent(et_content.getText().toString().trim());
                dialog.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    /**
     * 弹出对话框，填写内容
     */
    public static void imageDialog(final Context context, final String path) {
        dialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null);
        //获得dialog的window窗口
        Window window = dialog.getWindow();
        //设置dialog在屏幕底部
        window.setGravity(Gravity.CENTER);
        //设置dialog弹出时的动画效果，从屏幕底部向上弹出
        window.setWindowAnimations(R.style.dialogStyle);
        window.getDecorView().setPadding(0, 0, 0, 0);
        //获得window窗口的属性
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        dialog.setContentView(dialogView);

        final ImageView iv_detail = getView(dialogView, R.id.iv_detail);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bmp = BitmapFactory.decodeFile(path);
                final int width = bmp.getWidth();
                final int height = bmp.getHeight();
                final int screenW = MyApplication.getApplication().screenWidth;
                final int screenH = screenW * height / width;

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_detail.setLayoutParams(new LinearLayout.LayoutParams(
                                screenW, screenH));
                        iv_detail.setImageBitmap(bmp);
                    }
                });

            }
        }).start();

        dialog.show();

    }


    public interface EditDialogSureListener {
        void getContent(String content);
    }


    public static void dismiss() {
        try {
            if (dialog != null) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static <E extends View> E getView(View view, int id) {
        try {
            return (E) view.findViewById(id);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

}

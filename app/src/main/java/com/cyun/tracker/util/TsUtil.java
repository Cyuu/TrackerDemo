package com.cyun.tracker.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cyun.tracker.R;
import com.cyun.tracker.app.MyApplication;


public class TsUtil {
    /**
     * toast提示消息
     */
    public static void showToast( String msg) {
        Toast.makeText(MyApplication.getApplication().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
//        showToast(MyApplication.getApplication().getApplicationContext(), msg);
    }

    /**
     * 自定义Toast展示
     */
    private static void showToast(Context context, String info) {
        LayoutInflater inflater = (LayoutInflater)
                context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View toastView = inflater.inflate(R.layout.layout_toast, null, false);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);

        TextView txt = (TextView) toastView.findViewById(R.id.txt_tips);
        txt.setText(info);

        toast.setView(toastView);
        toast.show();
    }

}

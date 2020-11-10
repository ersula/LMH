package com.example.lmh.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lmh.R;

public class BToast extends Toast {
    public static ImageView toast_img;
    private static final int TYPE_HIDE = -1;
    /**
     * ImageView toast_img
     * 图标状态 显示√
     */
    private static final int TYPE_TRUE = 0;
    /**
     * 图标状态 显示×
     */
    private static final int TYPE_FALSE = 1;

    /**
     * 显示Toast
     *
     * @param context 上下文
     * @param text    显示的文本
     * @param time    显示时长
     * @param imgType 图标状态
     */
    private static void showToast(Context context, String text, int time, int imgType) {
        // 初始化一个新的Toast对象
        initToast(context, text);

        // 设置显示时长
        if (time == Toast.LENGTH_LONG) {
            toast.setDuration(Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
        }

        // 判断图标是否该显示，显示√还是×
        if (imgType == TYPE_HIDE) {
            toast_img.setVisibility(View.GONE);
        } else {
            if (imgType == TYPE_TRUE) {
                toast_img.setBackgroundResource(R.drawable.smile);
            } else {
                toast_img.setBackgroundResource(R.drawable.cry);
            }
            toast_img.setVisibility(View.VISIBLE);

            // 动画
            ObjectAnimator.ofFloat(toast_img, "rotationY", 0, 360).setDuration(1700).start();
        }

        // 显示Toast
        toast.show();
    }

    public static void showText(Context context, String text, int time, boolean isSucceed) {
        showToast(context, text, time, isSucceed ? TYPE_TRUE : TYPE_FALSE);
    }

    /**
     * Toast单例
     */
    private static BToast toast;

    /**
     * 构造
     *
     * @param context
     */
    public BToast(Context context) {
        super(context);
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }

    public void cancel() {
        try {
            super.cancel();
        } catch (Exception e) {

        }
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {

        }
    }

    private static void initToast(Context context, String text) {
        try {
            cancelToast();

            toast = new BToast(context);

            // 获取LayoutInflater对象
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // 由layout文件创建一个View对象
            View layout = inflater.inflate(R.layout.toast_layout, null);

            // 吐司上的图片
            toast_img = (ImageView) layout.findViewById(R.id.toast_img);

            // 吐司上的文字
            TextView toast_text = (TextView) layout.findViewById(R.id.toast_text);
            toast_text.setText(text);
            toast.setView(layout);
            toast.setGravity(Gravity.CENTER, 0, 70);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


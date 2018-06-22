package com.dalimao.mytaxi.common.util;

import android.content.Context;
import android.widget.Toast;

/**
 * @Title:ToastUtil
 * @Package:com.dalimao.mytaxi.splash.common.util
 * @Description:设备相关的工具类
 * @Auther:YJH
 * @Email:yuannunhua@gmail.com
 * @Date:2018/6/209:11
 */
public class ToastUtil {
    public static void show(Context context, String string) {
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
    }
}

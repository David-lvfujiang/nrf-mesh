package com.phyplusinc.android.phymeshprovisioner.utils;

/**
 * @Author: david.lvfujiang
 * @Date: 2019/12/11
 * @Describe:
 */

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * 语言切换
 * Created by 41455 on 2016/10/13.
 */
public class LanguageUtil {
    //弱引用持有Context
    private static WeakReference<Context> weakReference;
    public static String settingLanguage(Context context, ThreadLocal threadLocal) {
        weakReference = new WeakReference<>(context);
        //获得res资源对象
        Resources resources = weakReference.get().getResources();
        //获得设置对象
        Configuration config = resources.getConfiguration();
        //获得屏幕参数：主要是分辨率，像素等。
        DisplayMetrics dm = resources.getDisplayMetrics();
        //使用ThreadLocal获取主线程的数据
        String language = (String) threadLocal.get();
        if ("English".equals(language)) {
            // 设置语言为中文
            config.locale = Locale.CHINESE;
            threadLocal.set("Chinese");
            Log.e("TAG", "Chinese");

        } else {
            //英语（美国）
            config.locale = Locale.US;
            threadLocal.set("English");
            Log.e("TAG", "English");
        }
        //修改配置
        resources.updateConfiguration(config, dm);
        return language;
    }

    //获取ThreadLocal对象
    public static ThreadLocal getInstance() {
        return Holder.threadLocal;
    }

    //在静态内部类中持有singleton的实例，直接初始化
    private static class Holder {
        private static ThreadLocal threadLocal = new ThreadLocal<String>();
    }

}



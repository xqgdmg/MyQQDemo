package com.test.MyQQ.util;

import android.util.Log;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class LogUtil {
    private static boolean showLog = true;
    public static void logD(String tag,String msg){
        if(showLog) {
            Log.d(tag, msg);
        }
    }
}

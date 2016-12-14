package com.test.MyQQ.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}

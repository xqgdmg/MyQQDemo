package com.test.MyQQ;

import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageManager;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.Bmob;
import com.test.MyQQ.ui.activity.BaseActivity;

/**
 * 提供了Activity ，的集合增删
 * 初始化bmob
 * /初始化环信
 */
public class QQApplication extends Application {

    // Activity 集合
    private ArrayList<BaseActivity> activities = new ArrayList<BaseActivity>();

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化bmob
        initBmob();
        //初始化环信
        initHuanXin();
    }

    /**
     * 初始化环信
     */
    private void initHuanXin() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证，一般这样设置，想要直接添加，应该到收到好友申请的时候调用环信API ，直接同意
        options.setAcceptInvitationAlways(false);

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }

        //初始化
        EMClient.getInstance().init(this, options);

        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }

    /**
     * 环信 api复制过来
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
            }
        }
        return processName;
    }

    /**
     * 初始化bmob
     */
    private void initBmob() {
        Bmob.initialize(this, "50c7d6433a557795466e16476c1b9264");
    }

    /**
     * 添加 Activity
     */
    public void insertActivity(BaseActivity clazz) {
        if (!activities.contains(clazz)) {
            activities.add(clazz);
        }
    }

    /**
     * 移除 Activity
     */
    public void deleteActivity(BaseActivity clazz) {
        if (activities.contains(clazz)) {
            activities.remove(clazz);
        }
    }

}

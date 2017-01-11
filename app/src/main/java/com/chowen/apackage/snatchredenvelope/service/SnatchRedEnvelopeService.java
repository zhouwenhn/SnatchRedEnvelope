package com.chowen.apackage.snatchredenvelope.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.chowen.apackage.snatchredenvelope.constants.Constants;
import com.chowen.apackage.snatchredenvelope.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouwen on 2017/1/6.
 * Description:
 */
public class SnatchRedEnvelopeService extends AccessibilityService {

    private List<AccessibilityNodeInfo> mParents;
    private boolean auto = false;
    private int lastbagnum;
    private String pubclassName;
    private boolean WXMAIN = false;

    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    //唤醒屏幕相关
    private PowerManager pm;
    private PowerManager.WakeLock wl = null;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        mParents = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();

        L.e("AccessibilityEvent.typeAllMask>"+event.getEventType()+
                ">ContentDescription>"+event.getText());

        if (auto)
            L.e("有事件" + eventType);
        switch (eventType) {
            //当通知栏发生改变时
            case 2048:
                pubclassName = event.getClassName().toString();

                L.e("有2048事件" + pubclassName + auto);

                if (!auto && pubclassName.equals("android.widget.TextView")) {
                    L.e("有2048事件被识别" + auto + pubclassName);
                    getLastPacket(1);
                }
                if (auto && WXMAIN) {
                    getLastPacket();
                    auto = false;
                }

                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                L.e("AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED>"+event.getEventType()+
                        ">ContentDescription>"+event.getText());
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                L.e("AccessibilityEvent.TYPE_VIEW_FOCUSED>"+event.getEventType()+
                        ">ContentDescription>"+event.getContentDescription());
                break;

            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        if (content.contains("[微信红包]")) {
                            if (event.getParcelableData() != null &&
                                    event.getParcelableData() instanceof Notification) {
                                Notification notification = (Notification) event.getParcelableData();
                                PendingIntent pendingIntent = notification.contentIntent;
                                try {
                                    auto = true;
                                    wakeupUnlock(true);
                                    pendingIntent.send();
                                    L.e("进入微信" + auto + event.getClassName().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
            //当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                L.e("AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED#className>>>"+className);
                if (className.equals(Constants.LAUNCHER_UI)) {
                    //点击最后一个红包
                    L.e("点击红包");
                    if (auto)
                        getLastPacket();
                    auto = false;
                    WXMAIN = true;
                } else if (className.equals(Constants.LUCKY_MONEY_MONEY_RECEIVE_UI)) {
                    L.e("开红包");
                    onClick("com.tencent.mm:id/be_");
                    auto = false;
                    WXMAIN = false;
                } else if (className.equals(Constants.LUCKY_MONEY_MONEY_DETAIL_UI)) {
                    L.e("退出红包");
                    onClick("com.tencent.mm:id/gr");
                    WXMAIN = false;

                } else {
                    WXMAIN = false;
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void onClick(String clickId) {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(clickId);
            for (AccessibilityNodeInfo item : list) {
                item.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getLastPacket() {

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);
        L.e("当前页面红包数老方法" + mParents.size());
        if (mParents.size() > 0) {
            mParents.get(mParents.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            lastbagnum = mParents.size();
            mParents.clear();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void getLastPacket(int c) {

        L.e("新方法" + mParents.size());
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        recycle(rootNode);
        L.e("last++" + lastbagnum + "当前页面红包数" + mParents.size());
        if (mParents.size() > 0 && WXMAIN) {
            L.e("页面大于O且在微信界面");
            if (lastbagnum < mParents.size())
                mParents.get(mParents.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            lastbagnum = mParents.size();
            mParents.clear();
        }
    }

    public void recycle(AccessibilityNodeInfo info) {
        try {
            if (info.getChildCount() == 0) {
                if (info.getText() != null) {
                    if ("领取红包".equals(info.getText().toString())) {
                        if (info.isClickable()) {
                            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                        AccessibilityNodeInfo parent = info.getParent();
                        while (parent != null) {
                            if (parent.isClickable()) {
                                mParents.add(parent);
                                break;
                            }
                            parent = parent.getParent();
                        }
                    }
                }
            } else {
                for (int i = 0; i < info.getChildCount(); i++) {
                    if (info.getChild(i) != null) {
                        recycle(info.getChild(i));
                    }
                }
            }
        } catch (Exception e) {


        }
    }

    private void wakeupUnlock(boolean b) {
        if (b) {
            //获取电源管理器对象
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

            //点亮屏幕
            wl.acquire();

            //得到键盘锁管理器对象
            km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            kl = km.newKeyguardLock("unLock");

            //解锁
            kl.disableKeyguard();
        } else {
            //锁屏
            kl.reenableKeyguard();

            //释放wakeLock，关灯
            wl.release();
        }

    }

    @Override
    public void onInterrupt() {

    }
}

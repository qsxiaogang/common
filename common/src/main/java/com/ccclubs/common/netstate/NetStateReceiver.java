package com.ccclubs.common.netstate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.ccclubs.common.utils.android.LogUtils;
import java.util.ArrayList;

/**
 * 网络状态改变监听
 */
public class NetStateReceiver extends BroadcastReceiver {

  public final static String CUSTOM_ANDROID_NET_CHANGE_ACTION =
      "com.ccclubs.common.library.net.conn.CONNECTIVITY_CHANGE";
  private final static String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
  private final static String TAG = NetStateReceiver.class.getSimpleName();

  private static boolean isNetAvailable = false;
  private static int mNetType;
  private static ArrayList<NetChangeObserver> mNetChangeObservers = new ArrayList<>();
  private static BroadcastReceiver mBroadcastReceiver;

  private static BroadcastReceiver getReceiver() {
    if (null == mBroadcastReceiver) {
      synchronized (NetStateReceiver.class) {
        if (null == mBroadcastReceiver) {
          mBroadcastReceiver = new NetStateReceiver();
        }
      }
    }
    return mBroadcastReceiver;
  }

  @Override public void onReceive(Context context, Intent intent) {
    mBroadcastReceiver = NetStateReceiver.this;
    if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION) || intent.getAction()
        .equalsIgnoreCase(CUSTOM_ANDROID_NET_CHANGE_ACTION)) {
      if (!NetworkUtils.isConnectedByState(context)) {
        LogUtils.i(TAG, "<--- network disconnected --->");
        isNetAvailable = false;
      } else {
        LogUtils.i(TAG, "<--- network connected --->");
        isNetAvailable = true;
        mNetType = NetworkUtils.getCurrentNetworkType(context);
      }
      notifyObserver();
    }
  }

  /**
   * 手动注册网络监听Receiver
   */
  public static void registerNetworkStateReceiver(Context mContext) {
    IntentFilter filter = new IntentFilter();
    filter.addAction(CUSTOM_ANDROID_NET_CHANGE_ACTION);
    filter.addAction(ANDROID_NET_CHANGE_ACTION);
    mContext.getApplicationContext().registerReceiver(getReceiver(), filter);
  }

  /**
   * 检查网络链接状态，并发送{@link #CUSTOM_ANDROID_NET_CHANGE_ACTION} 一个broadcast
   */
  public static void checkNetworkState(Context mContext) {
    Intent intent = new Intent();
    intent.setAction(CUSTOM_ANDROID_NET_CHANGE_ACTION);
    mContext.sendBroadcast(intent);
  }

  /**
   * 注销网络监听broadcastReceiver
   */
  public static void unRegisterNetworkStateReceiver(Context mContext) {
    if (mBroadcastReceiver != null) {
      try {
        mContext.getApplicationContext().unregisterReceiver(mBroadcastReceiver);
      } catch (Exception e) {
        LogUtils.d(TAG, e.getMessage());
      }
    }
  }

  public static boolean isNetworkAvailable() {
    return isNetAvailable;
  }

  public static int getState() {
    return mNetType;
  }

  private void notifyObserver() {
    if (!mNetChangeObservers.isEmpty()) {
      int size = mNetChangeObservers.size();
      for (int i = 0; i < size; i++) {
        NetChangeObserver observer = mNetChangeObservers.get(i);
        if (observer != null) {
          if (isNetworkAvailable()) {
            observer.onNetConnected(mNetType);
          } else {
            observer.onNetDisConnect();
          }
        }
      }
    }
  }

  public static void registerObserver(NetChangeObserver observer) {
    if (mNetChangeObservers == null) {
      mNetChangeObservers = new ArrayList<>();
    }
    mNetChangeObservers.add(observer);
  }

  public static void removeRegisterObserver(NetChangeObserver observer) {
    if (mNetChangeObservers != null) {
      if (mNetChangeObservers.contains(observer)) {
        mNetChangeObservers.remove(observer);
      }
    }
  }
}
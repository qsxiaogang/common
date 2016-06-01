package com.ccclubs.common.utils.android;

import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.ccclubs.common.R;

/**
 * ViewUtils 操作一些Views
 */
public class ViewUtils {
  /** 防止连续点击 */
  private static long lastClickTime;

  public static boolean isFastClick() {
    long time = System.currentTimeMillis();
    long timeD = time - lastClickTime;
    if (timeD > 0 && timeD < 500) {
      return true;
    }
    lastClickTime = time;
    return false;
  }

  /**
   * 设置空白内容图片，文本
   *
   * @param view emptyView
   * @param imgRes empty_img
   * @param emptyText empty_text
   */
  public static void setEmptyMessage(View view, int imgRes, String emptyText) {
    ((ImageView) view.findViewById(R.id.empty_img)).setImageResource(imgRes);
    ((TextView) view.findViewById(R.id.empty_text)).setText(emptyText);
  }

  //public static boolean canChildScrollUp(View view) {
  //  // 如果当前版本小于 14，那就得自己背锅
  //  if (android.os.Build.VERSION.SDK_INT < 14) {
  //    // 这里给出了如果当前 view 是 AbsListView 的实例的检测方法
  //    if (view instanceof AbsListView) {
  //      final AbsListView absListView = (AbsListView) view;
  //      return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
  //          || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
  //    } else {
  //      return view.getScrollY() > 0;
  //    }
  //  } else {
  //    return view.canScrollVertically(-1);
  //  }
  //}

  public boolean canChildScrollUp(View view) {
    if (android.os.Build.VERSION.SDK_INT < 14) {
      if (view instanceof AbsListView) {
        final AbsListView absListView = (AbsListView) view;
        return absListView.getChildCount() > 0
            && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
            .getTop() < absListView.getPaddingTop());
      } else {
        return ViewCompat.canScrollVertically(view, -1) || view.getScrollY() > 0;
      }
    } else {
      return ViewCompat.canScrollVertically(view, -1);
    }
  }

}

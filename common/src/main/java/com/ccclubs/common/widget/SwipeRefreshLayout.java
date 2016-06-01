package com.ccclubs.common.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by qsxiaogang on 2016/5/13 17:46.
 * Description:
 */
public class SwipeRefreshLayout extends android.support.v4.widget.SwipeRefreshLayout{
  public SwipeRefreshLayout(Context context) {
    super(context);
  }

  public SwipeRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean canChildScrollUp() {
    if (isRefreshing()){
      return true;
    }
    return false;
  }
}

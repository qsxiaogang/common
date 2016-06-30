package com.ccclubs.common.base;

import android.app.Application;
import android.widget.Toast;
import com.ccclubs.common.event.ToastEvent;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by qsxiaogang on 2016/6/30 10:09.
 * Description:
 */
public class RxApplication extends Application {

  @Subscribe public void onToastEvent(ToastEvent event) {
    Toast.makeText(this, event.getMsg(),
        event.isLengthShort() ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
  }
}

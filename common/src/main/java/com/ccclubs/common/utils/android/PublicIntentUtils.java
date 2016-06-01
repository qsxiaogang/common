package com.ccclubs.common.utils.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by CCCLUBS on 2016/5/27.
 */
public class PublicIntentUtils {
  public static void startActionIntent(Context context,String action,String urlString) {
    Intent intent = new Intent(action,Uri.parse(urlString));
    context.startActivity(intent);
  }
}

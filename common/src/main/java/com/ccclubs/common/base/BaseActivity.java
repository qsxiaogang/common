package com.ccclubs.common.base;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.ccclubs.common.R;
import com.ccclubs.common.event.ToastEvent;
import com.ccclubs.common.netstate.NetChangeObserver;
import com.ccclubs.common.netstate.NetStateReceiver;
import com.ccclubs.common.support.ActivityManagerHelper;
import com.ccclubs.common.support.ConfigurationHelper;
import com.ccclubs.common.support.EventBusHelper;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Activity基类, 继承自此类的Activity需要实现{@link #getLayoutId},{@link #init}
 * 以及{@link #createPresenter()}, 不需要覆写onCreate方法.
 * <br/>
 * 实现此类需遵循MVP设计, 第一个泛型V需传入一个继承自{@link BaseView}的MVPView,
 * 第二个泛型需传入继承自{@link RxBasePresenter}的MVPPresenter.
 * <br/>
 * Presenter的生命周期已交由此类管理, 子类无需管理. 如果子类要使用多个Presenter, 则需要自行管理生命周期.
 * 此类已经实现了BaseView中的抽象方法, 子类无需再实现, 如需自定可覆写对应的方法.
 * <br/>
 */
public abstract class BaseActivity<V extends BaseView, T extends BasePresenter<V>>
    extends AppCompatActivity implements BaseActivityInterface, BaseView {

  private static long lastClickBackButtonTime; //记录上次点击返回按钮的时间，用来配合实现双击返回按钮退出应用程序的功能
  private int doubleClickSpacingInterval = 2 * 1000; //双击退出程序的间隔时间
  private long activityId = -5l; //当前Activity在ActivityManager中的ID
  //private long createTime; //创建时间
  private boolean enableDoubleClickExitApplication; //是否开启双击退出程序功能
  // Log 的tag
  protected static String TAG_LOG = null;
  // 网络链接状态
  protected NetChangeObserver mNetChangeObserver = null;
  // 一些屏幕参数
  protected int mScreenWidth = 0;
  protected int mScreenHeight = 0;
  protected float mScreenDensity = 0.0f;

  protected T presenter;

  @CallSuper @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //记录创建时间，用于异常终止时判断是否需要等待一段时间再终止，因为时间过短的话体验不好
    //createTime = System.currentTimeMillis();
    //将当前Activity放入ActivityManager中，并获取其ID
    activityId = ActivityManagerHelper.getInstance().putActivity(this);
    //如果需要去掉标题栏
    if (isRemoveTitleBar()) requestWindowFeature(Window.FEATURE_NO_TITLE);
    //如果需要全屏就去掉通知栏
    if (isRemoveStatusBar()) {
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
          WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    setContentView(getLayoutId());
    presenter = createPresenter();
    if (presenter != null) presenter.attachView((V) this);
    ButterKnife.bind(this);

    TAG_LOG = this.getClass().getSimpleName();
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

    mScreenDensity = displayMetrics.density;
    mScreenHeight = displayMetrics.heightPixels;
    mScreenWidth = displayMetrics.widthPixels;

    mNetChangeObserver = new NetChangeObserver() {
      @Override public void onNetConnected(int state) {
        super.onNetConnected(state);
        onNetworkConnected(state);
      }

      @Override public void onNetDisConnect() {
        super.onNetDisConnect();
        onNetworkDisConnected();
      }
    };
    NetStateReceiver.registerObserver(mNetChangeObserver);
    NetStateReceiver.registerNetworkStateReceiver(this);
    EventBusHelper.register(this);
    // 是否开启竖屏显示
    if (ConfigurationHelper.getScreenPortrait()) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    init(savedInstanceState);
  }

  public <T extends View> T $(int id) {
    return (T) super.findViewById(id);
  }

  public Toolbar initToolbar(int title) {
    return initToolbar(getString(title));
  }

  public Toolbar initToolbar(CharSequence title) {
    Toolbar toolbar = $(R.id.toolbar);
    if (null != toolbar) {
      setTitle(title);
      setSupportActionBar(toolbar);
      getSupportActionBar().setHomeButtonEnabled(true);
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    return toolbar;
  }

  /**
   * 指定Activity需加载的布局ID, {@link BaseActivity}
   * 会通过{@link #setContentView}方法来加载布局
   *
   * @return 需加载的布局ID
   */
  protected abstract int getLayoutId();

  /**
   * 初始化方法, 类似OnCreate, 仅在此方法中做初始化操作, findView与事件绑定请使用ButterKnife
   */
  protected abstract void init(Bundle savedInstanceState);

  /**
   * 创建Presenter, 然后通过调用{@link #getPresenter()}来使用生成的Presenter
   *
   * @return Presenter
   */
  protected abstract T createPresenter();

  /**
   * 获取通过{@link #createPresenter()}生成的presenter对象
   *
   * @return Presenter
   */
  public T getPresenter() {
    return presenter;
  }

  @Override public void onBackPressed() {
    if (enableDoubleClickExitApplication) {
      long currentMillisTime = System.currentTimeMillis();
      //两次点击的间隔时间尚未超过规定的间隔时间将执行退出程序
      if (lastClickBackButtonTime != 0
          && (currentMillisTime - lastClickBackButtonTime) < doubleClickSpacingInterval) {
        finishApplication();
      } else {
        onPromptExitApplication();
        lastClickBackButtonTime = currentMillisTime;
      }
    } else {
      finish();
    }
  }

  @Override protected void onDestroy() {
    if (presenter != null) presenter.detachView();
    presenter = null;
    ActivityManagerHelper.getInstance().removeActivity(activityId);
    NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
    NetStateReceiver.unRegisterNetworkStateReceiver(this);
    super.onDestroy();
    EventBusHelper.unregister(this);
  }

  @Override public long getActivityId() {
    return activityId;
  }

  /**
   * 判断是否需要去除标题栏，默认不去除
   *
   * @return 是否需要去除标题栏
   */
  @Override public boolean isRemoveTitleBar() {
    return false;
  }

  /**
   * 判断是否需要全屏，默认不全屏
   *
   * @return 是否需要全屏
   */
  @Override public boolean isRemoveStatusBar() {
    return false;
  }

  /**
   * 网络连接正常
   */
  public void onNetworkConnected(int state) {
    //switch (state) {
    //  case ConnectivityManager.TYPE_MOBILE:
    //    toastS(R.string.network_mobile);
    //    break;
    //  case ConnectivityManager.TYPE_WIFI:
    //    toastS(R.string.network_wifi);
    //    break;
    //}
  }

  /**
   * 网络连接 ，未连接
   */
  public void onNetworkDisConnected() {
    toastS(R.string.network_error);
  }

  @Override public boolean isNetworkAvailable() {
    return NetStateReceiver.isNetworkAvailable();
  }

  @Override public SharedPreferences getDefaultPreferences() {
    return PreferenceManager.getDefaultSharedPreferences(getBaseContext());
  }

  @Override public boolean isFirstUsing() {
    return getDefaultPreferences().getBoolean(PRFERENCES_FIRST_USING, true);
  }

  @Override public void setFirstUsing(boolean firstUsing) {
    SharedPreferences.Editor editor = getDefaultPreferences().edit();
    editor.putBoolean(PRFERENCES_FIRST_USING, firstUsing);
    editor.commit();
  }

  /**
   * 准备退出应用
   *
   * @author qsxiaogang
   * @createtime 2014-11-12 上午10:05:30
   */
  protected void onPromptExitApplication() {
    toastS("再按一次退出程序！");
  }

  /**
   * 是否启用双击退出程序
   */
  public void setEnableDoubleClickExitApplication(boolean enableDoubleClickExitApplication) {
    this.enableDoubleClickExitApplication = enableDoubleClickExitApplication;
  }

  @Override public void finishApplication() {
    ActivityManagerHelper.getInstance().finishApplication();
  }

  @Override public BaseActivity getViewContext() {
    return this;
  }

  /*
      *********************************************** Toast ************************************************
      */
  @Override public void toastL(int resId) {
    //Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show();
    EventBusHelper.post(new ToastEvent(getString(resId), Toast.LENGTH_LONG));
  }

  @Override public void toastS(int resId) {
    EventBusHelper.post(new ToastEvent(getString(resId), Toast.LENGTH_SHORT));
  }

  @Override public void toastL(String content) {
    EventBusHelper.post(new ToastEvent(content, Toast.LENGTH_LONG));
  }

  @Override public void toastS(String content) {
    EventBusHelper.post(new ToastEvent(content, Toast.LENGTH_SHORT));
  }

  /**
   * 处理Toast事件
   */
  @Subscribe(threadMode = ThreadMode.MAIN) public void onToastEvent(ToastEvent event) {
    switch (event.getDuration()) {
      case Toast.LENGTH_SHORT:
        Toast.makeText(this, event.getMsg(), Toast.LENGTH_SHORT).show();
        break;
      case Toast.LENGTH_LONG:
        Toast.makeText(this, event.getMsg(), Toast.LENGTH_LONG).show();
        break;
    }
  }
}

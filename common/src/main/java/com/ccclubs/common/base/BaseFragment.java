package com.ccclubs.common.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.ccclubs.common.support.EventBusHelper;

/**
 * Fragment基类, 继承自此类的Fragment需要实现{@link #getLayoutId}, {@link #init}
 * 以及{@link #createPresenter()}, 不需要覆写onCreate方法.
 * <br/>
 * Fragment基类中{@link #isBindEventBusHere()} 注册EventBus，使用时，需要自行注册、注销
 * <br/>
 * 鉴于FragmentManager的attach与detach会销毁Fragment的视图, 此基类会在onCreate中生成一个
 * parentView, 缓存起来, 并在onCreateView中直接返回该View, 来达到保存Fragment视图状态的目的,
 * 同时避免不停的销毁与创建.
 * <br/>
 * 实现此类需遵循MVP设计, 第一个泛型V需传入一个继承自{@link BaseView}的MVPView,
 * 第二个泛型需传入继承自{@link BasePresenter}的MVPPresenter.
 * <br/>
 * Presenter的生命周期已交由此类管理, 子类无需管理. 如果子类要使用多个Presenter, 则需要自行管理生命周期.
 * 此类已经实现了BaseView中的抽象方法, 子类无需再实现, 如需自定可覆写对应的方法.
 * <br/>
 * 对于多个Presenter，需要重写 onCreate，ondDestroy 方法
 *
 * ==================================================
 * 注1:
 * 如果是与ViewPager一起使用，调用的是setUserVisibleHint。
 *
 * 注2:
 * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
 * 针对初始就show的Fragment 为了触发onHiddenChanged事件 达到lazy效果 需要先hide再show
 * eg:
 * transaction.hide(aFragment);
 * transaction.show(aFragment);
 */
public abstract class BaseFragment<V extends BaseView, T extends BasePresenter<V>> extends Fragment
    implements BaseView {
  // Log 的tag
  protected static String TAG_LOG = null;

  protected View parentView;
  protected T presenter;

  /**
   * 是否可见状态
   */
  protected boolean isVisible;
  /**
   * 标志位，View已经初始化完成。
   */
  protected boolean isPrepared;
  /**
   * 是否第一次加载
   */
  protected boolean isFirstLoad = true;

  @CallSuper @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    parentView = getLayoutInflater(savedInstanceState).inflate(getLayoutId(), null, false);
    presenter = createPresenter();
    if (presenter != null) presenter.attachView((V) this);
    ButterKnife.bind(this, parentView);

    TAG_LOG = this.getClass().getSimpleName();

    if (isBindEventBusHere()) {
      EventBusHelper.register(this);
    }

    // 取消 isFirstLoad = true的注释 , 因为上述的initData本身就是应该执行的
    // onCreateView执行 证明被移出过FragmentManager initData确实要执行.
    isFirstLoad = true;
    isPrepared = true;

    lazyLoad();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return parentView;
  }

  /**
   * 如果是与ViewPager一起使用，调用的是setUserVisibleHint
   *
   * @param isVisibleToUser 是否显示出来了
   */
  @Override public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (getUserVisibleHint()) {
      isVisible = true;
      onVisible();
    } else {
      isVisible = false;
      onInvisible();
    }
  }

  /**
   * 如果是通过FragmentTransaction的show和hide的方法来控制显示，调用的是onHiddenChanged.
   * 若是初始就show的Fragment 为了触发该事件 需要先hide再show
   *
   * @param hidden hidden True if the fragment is now hidden, false if it is not
   * visible.
   */
  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (!hidden) {
      isVisible = true;
      onVisible();
    } else {
      isVisible = false;
      onInvisible();
    }
  }

  protected void onVisible() {
    lazyLoad();
  }

  protected void onInvisible() {
  }

  /**
   * 要实现延迟加载Fragment内容,需要在 onCreate
   * isPrepared = true;
   */
  protected void lazyLoad() {
    if (!isPrepared || !isVisible || !isFirstLoad) {
      return;
    }
    isFirstLoad = false;
    init();
  }

  @Override public void onDestroy() {
    if (presenter != null) presenter.detachView();
    presenter = null;
    ButterKnife.unbind(this);
    if (isBindEventBusHere()) {
      EventBusHelper.unregister(this);
    }
    super.onDestroy();
  }

  public T getPresenter() {
    return presenter;
  }

  public View getParentView() {
    return parentView;
  }

  /**
   * 指定Fragment需加载的布局ID
   *
   * @return 需加载的布局ID
   */
  protected abstract @LayoutRes int getLayoutId();

  protected abstract boolean isBindEventBusHere();

  /**
   * 初始化方法, 类似OnCreate, 仅在此方法中做初始化操作, findView与事件绑定请使用ButterKnife
   */
  protected abstract void init();

  /**
   * 创建Presenter, 然后通过调用{@link #getPresenter()}来使用生成的Presenter
   *
   * @return Presenter
   */
  protected abstract T createPresenter();

  @Override public BaseActivity getViewContext() {
    return getActivity() instanceof BaseActivity ? ((BaseActivity) getActivity()) : null;
  }
}

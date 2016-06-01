package com.ccclubs.common.base;

import android.support.annotation.CallSuper;
import com.ccclubs.common.support.RxHelper;
import rx.subscriptions.CompositeSubscription;

/**
 * 参考{@link BasePresenter}, Activity需使用继承自此类的子类
 */
public abstract class RxBasePresenter<T extends RxBaseView> extends BasePresenter<T> {
  protected CompositeSubscription mSubscriptions = new CompositeSubscription();

  /**
   * 注册到Activity/Fragment生命周期
   */
  @CallSuper public void registerLifeCycle() {
    mSubscriptions = RxHelper.getNewCompositeSubIfUnsubscribed(mSubscriptions);
  }

  /**
   * 解绑到Activity/Fragment生命周期
   */
  @CallSuper public void unRegisterLifeCycle() {
    RxHelper.unsubscribeIfNotNull(mSubscriptions);
  }
}

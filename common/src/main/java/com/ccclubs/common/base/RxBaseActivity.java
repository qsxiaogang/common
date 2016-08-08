package com.ccclubs.common.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ccclubs.common.support.ConfigurationHelper;

/**
 * Activity基类, 继承自此类的Activity需要实现{@link #getLayoutId},{@link #init}
 * 以及{@link #createPresenter()}, 不需要覆写onCreate方法.
 * <br/>
 * 实现此类需遵循MVP设计, 第一个泛型V需传入一个继承自{@link RxBaseView}的MVPView
 * <br/>
 * 第二个泛型P需传入继承自{@link RxBasePresenter}的MVPPresenter.
 * <br/>
 * Presenter的生命周期已交由此类管理, 子类无需管理. 如果子类要使用多个Presenter, 则需要自行管理生命周期.
 * 此类已经实现了BaseView中的抽象方法, 子类无需再实现, 如需自定可覆写对应的方法.
 * <br/>
 */
public abstract class RxBaseActivity<V extends RxBaseView, P extends RxBasePresenter<V>>
    extends BaseActivity<V, P> implements RxBaseView {

  private MaterialDialog mLoadingDialog;

  @CallSuper @Override protected void init(Bundle savedInstanceState) {
    // 绑定到生命周期
    if (getPresenter() != null) getPresenter().registerLifeCycle();
  }

  @Override public void showModalLoading() {
    if (mLoadingDialog == null) {
      mLoadingDialog =
          new MaterialDialog.Builder(this).content(ConfigurationHelper.getModalLoadingText())
              .progress(true, 0)
              .progressIndeterminateStyle(false)
              .show();
    }
    if (!mLoadingDialog.isShowing()) mLoadingDialog.show();
  }

  @Override public void closeModalLoading() {
    if (mLoadingDialog != null) {
      mLoadingDialog.dismiss();
    }
  }

  @Override public RxBaseActivity getRxContext() {
    return this;
  }

  @Override protected void onDestroy() {
    // 解绑
    if (getPresenter() != null) getPresenter().unRegisterLifeCycle();
    super.onDestroy();
  }
}

package com.ccclubs.common.support;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * RxHelper，管理Observable，避免发生持有context而导致内存泄露
 * <br/>
 * CompositeSubscription内部有一个Subscription的Set对象的集合，
 * <br/>
 * 可以调用unsubscription()方法取消集合中所有subcription的订阅。
 */
public class RxHelper {

  public static void unsubscribeIfNotNull(Subscription subscription) {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public static CompositeSubscription getNewCompositeSubIfUnsubscribed(
      CompositeSubscription subscription) {
    if (subscription == null || subscription.isUnsubscribed()) {
      return new CompositeSubscription();
    }

    return subscription;
  }
}

package com.ccclubs.common.support;

import android.app.Activity;
import com.ccclubs.common.base.BaseActivityInterface;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Activity管理器
 * <br/>
 * 模拟Android Activity栈, 方便管理Activity
 */
public class ActivityManagerHelper {
  /**
   * Activity列表
   */
  private Map<Long, Activity> activityMap;
  /**
   * 等待终止的Activity的Id列表
   */
  private Set<Long> waitFinishActivityIds;
  /**
   * Activity管理器的实例
   */
  private static ActivityManagerHelper instance;

  /**
   * 创建Activity管理器
   */
  private ActivityManagerHelper() {
    setActivityMap(new HashMap<Long, Activity>());
    setWaitFinishActivityIds(new HashSet<Long>());
  }

  /**
   * 添加一个Activity到Activity管理器
   *
   * @param activity Activity
   * @return ActivityID
   */
  public long putActivity(Activity activity) {
    long id = System.currentTimeMillis();
    getActivityMap().put(id, activity);
    return id;
  }

  /**
   * 获取给定ID的Activity
   *
   * @param id 要获取的Activity的ID
   * @return 给定ID的Activity
   */
  public Activity getActivity(long id) {
    return getActivityMap().get(id);
  }

  /**
   * 从Activity管理器中移除给定ID的Activity
   *
   * @param id 要移除的Activity的ID
   * @return 给定ID的Activity
   */
  public Activity removeActivity(long id) {
    return getActivityMap().remove(id);
  }

  /**
   * 终止给定ID的Activity
   *
   * @param id 要终止的Activity的ID
   */
  public void finishActivity(long id) {
    Activity activity = getActivityMap().get(id);
    if (activity != null) {
      activity.finish();
    }
  }

  /**
   * 终止给定ID数组中包含的Activity
   *
   * @param ids 要终止的Activity的ID数组
   */
  public void finishActivitys(long[] ids) {
    for (Long lon : ids) {
      finishActivity(lon);
    }
  }

  /**
   * 终止给定ID集合中包含的Activity
   *
   * @param ids 要终止的Activity的ID集合
   */
  public void finishActivitys(Set<Long> ids) {
    for (Long lon : ids) {
      finishActivity(lon);
    }
  }

  /**
   * 终止除给定ID的Activity之外的所有Activity
   *
   * @param id 只有这个Activity不销毁
   */
  public void finishOtherActivitys(long id) {
    Set<Long> ids = getActivityMap().keySet();
    for (Long lon : ids) {
      if (lon != id) {
        finishActivity(lon);
      }
    }
  }

  /**
   * 终止应用程序
   */
  public void finishApplication() {
    for (Activity activity : getActivityMap().values()) {
      activity.finish();
    }
  }

  /**
   * 放进去一个等待终止的Activity
   *
   * @param activityId 等待终止的Activity的ID
   */
  public void putToWaitFinishActivitys(long activityId) {
    getWaitFinishActivityIds().add(activityId);
  }

  /**
   * 放进去一个等待终止的Activity
   *
   * @param baseActivityInterface 等待终止的Activity
   */
  public void putToWaitFinishActivitys(BaseActivityInterface baseActivityInterface) {
    putToWaitFinishActivitys(baseActivityInterface.getActivityId());
  }

  /**
   * 从等待终止的Activity列表中移除给定ID的Activity
   *
   * @param activityId 要移除的Activity的ID
   * @return true：列表中存在并且已经移除
   */
  public boolean removeFromWaitFinishActivitys(long activityId) {
    return getWaitFinishActivityIds().remove(activityId);
  }

  /**
   * 把等待终止的Activity列表清空
   */
  public void clearWaitFinishActivitys() {
    getWaitFinishActivityIds().clear();
  }

  /**
   * 终止所有等待中的Activity，终止之后自动从等待列表中清除
   */
  public void finishAllWaitingActivity() {
    if (!getWaitFinishActivityIds().isEmpty()) {
      finishActivitys(getWaitFinishActivityIds());
      clearWaitFinishActivitys();
    }
  }

  /**
   * 获取ActivityMap
   *
   * @return ActivityMap
   */
  public Map<Long, Activity> getActivityMap() {
    return activityMap;
  }

  /**
   * 设置ActivityMap
   *
   * @param activityMap ActivityMap
   */
  public void setActivityMap(Map<Long, Activity> activityMap) {
    this.activityMap = activityMap;
  }

  /**
   * 获取等待终止的Activity的Id列表
   *
   * @return 等待终止的Activity的Id列表
   */
  public Set<Long> getWaitFinishActivityIds() {
    return waitFinishActivityIds;
  }

  /**
   * 设置等待终止的Activity的Id列表
   *
   * @param waitFinishActivityIds 等待终止的Activity的Id列表
   */
  public void setWaitFinishActivityIds(Set<Long> waitFinishActivityIds) {
    this.waitFinishActivityIds = waitFinishActivityIds;
  }

  /**
   * 获取Activity管理器的实例
   *
   * @return PpActivityManager
   */
  public static ActivityManagerHelper getInstance() {
    if (instance == null) {
      instance = new ActivityManagerHelper();
    }
    return instance;
  }
}

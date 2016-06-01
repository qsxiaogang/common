package com.ccclubs.common.event;

/**
 * 用于Toast的EventBus事件
 */
public class ToastEvent {
  int duration;
  private String msg;

  public ToastEvent(String msg, int duration) {
    this.msg = msg;
    this.duration = duration;
  }

  public String getMsg() {
    return msg;
  }

  public int getDuration() {
    return duration;
  }
}

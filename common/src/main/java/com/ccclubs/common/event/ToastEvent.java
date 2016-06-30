package com.ccclubs.common.event;

/**
 * 用于Toast的EventBus事件
 */
public class ToastEvent {
  private boolean lengthShort = true;
  private String msg;

  public ToastEvent(String msg, boolean lengthShort) {
    this.msg = msg;
    this.lengthShort = lengthShort;
  }

  public String getMsg() {
    return msg;
  }

  public boolean isLengthShort() {
    return lengthShort;
  }
}

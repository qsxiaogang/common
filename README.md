# common 一个简易的MVP框架。
该框架目前集成了`support`，`design`，`retrofit`，`rxjava`，`rxandroid`，`butterknife`，`eventbus`，`nineoldandroids`，`sweetalert`等第三方库。

//TODO



# ProGuard 设置
```
# butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# okhttp3
-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }
-keep interface com.squareup.okhttp3.** { *; }

# rxjava & rxandroid
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# -dontwarn com.ccclubs.common.**
# -keep class com.ccclubs.common.** { *; }
-keep class com.ccclubs.common.** { *; }
-dontwarn com.ccclubs.common.**
-keep public class com.ccclubs.common.R$*{
    public static final int *;
}

# sweet-alert-dialog
-keep class cn.pedant.SweetAlert.Rotate3dAnimation {
   public <init>(...);
}


```

# 感谢以下开源项目
- https://github.com/sockeqwe/mosby
- https://github.com/ray0807/ShareFramework
- https://github.com/Aspsine/IRecyclerView

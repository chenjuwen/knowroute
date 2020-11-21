#指定代码的压缩级别
-optimizationpasses 5

 #优化  不优化输入的类文件
#-dontoptimize

#不做预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保留不混淆:注解、内部类、泛型
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod,Exceptions

#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

# 保留四大组件，自定义的Application等
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

#如果有引用v4包可以添加下面这行
-keep public class * extends android.support.v4.app.Fragment

#如果引用了v4或者v7包
-keep class android.support.** {*;}
-dontwarn android.support.**
-keep interface android.support.** { *; }

#androidx
-keep class androidx.** {*;}
-keep interface androidx.** {*;}
-keep public class * extends androidx.**
-dontwarn androidx.**

#support v4/7库
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

#support design库
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

-keep class com.google.android.material.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#避免layout中onclick方法（android:onclick="onClick"）混淆
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

#避免回调函数 onXXEvent 混淆
-keepclassmembers class * {
    void *(*Event);
}

# 保留自定义View
-keepclassmembers public class * extends android.view.View{
    void set*(***);
    *** get*();
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

#保持 Parcelable 不被混淆
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保留R文件中的成员
-keepclassmembers class **.R$* {
    public static <fields>;
}

#保持 Serializable 不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# webView处理
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
-keepattributes JavascriptInterface

#ButterKnife混淆配置
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}




#EventBus
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#baiduMap
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**

-keep class rx.** {*;}
-dontwarn rx.**

-keep class org.** {*;}
-dontwarn org.**

-keep class com.googlecode.** {*;}
-dontwarn com.googlecode.**

-keep class com.google.** {*;}
-dontwarn com.google.**

#OkHttp3混淆配置
-keep class okhttp3.** {*;}
-dontwarn okhttp3.**

-keep class okio.** {*;}
-dontwarn okio.**

-keep class ch.** {*;}
-dontwarn ch.**

-keep class com.alibaba.** {*;}
-dontwarn com.alibaba.**

-keep class de.** {*;}
-dontwarn de.**

-keep class jsc.kit.** {*;}
-dontwarn jsc.kit.**

#业务代码
#保持包及其子包下的类、方法和成员变量不被混淆
-keep public class com.heasy.knowroute.** {
    public *;
}


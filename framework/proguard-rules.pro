# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\ProgramFiles\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keepattributes *Annotation*
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment



# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class com.sway.android.butterfly.ui.view.TargetDrawable {
    void set*(***);
    *** get*();
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes JavascriptInterface
-keepattributes *Annotation*

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

#------------------  下方是共性的排除项目         ----------------
# 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
# 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除

-keepclasseswithmembers class * {
    ... *JNI*(...);
}

-keepclasseswithmembernames class * {
	... *JRI*(...);
}

-keep class **JNI* {*;}

-keep class **Native* {*;}

-keep class **Bean* {*;}

# ------------------  下方UI层需要排除的项目，UI层的都这里加     ----------------


-keep class com.callme.platform.util.view.** { *; }

-keep class dalvik.system.**{
	*;
}

-keep class com.callme.platform.widget.stickylistheaders.StickyListHeadersListView { *; }

-keepclasseswithmembers class * extends android.webkit.WebChromeClient{
    *;
}

-keep class android.support.multidex.**{*;}

-keep public class com.huwochuxing.foundations.R$*{
    public static final int *;
}


#======================================================
#Glide
-dontwarn com.bumptech.glide.**

#高德
-dontwarn com.amap.api.**
-keep class com.amap.api.**{*;}
-dontwarn com.autonavi.**
-keep class com.autonavi.**{*;}

#gettui
-dontwarn com.igexin.**
-keep class com.igexin.**{*;}

#讯飞语音
-keep class com.iflytek.**{*;}
-keepattributes Signature

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio Okio库是一个由square公司开发的，它补充了java.io和java.nio的不足，
#以便能够更加方便，快速的访问、存储和处理你的数据。而OkHttp的底层也使用该库作为支持。
-dontwarn okio.**
-keep class okio.**{*;}

#Gson
-keep public class com.google.gson.**
-keep public class com.google.gson.** {public private protected *;}
-keepattributes Signature
-keepattributes *Annotation*
-keep public class com.project.mocha_patient.login.SignResponseData { private *; }

#腾讯开发平台
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}
-dontwarn com.tencent.smtt.export.external.**
-keep class com.tencent.smtt.export.external.**{*;}

#腾讯mta
-keep class com.tencent.stat.*{*;}
-keep class com.tencent.mid.*{*;}

 #fastjson
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*;}

#灵云语音
-dontwarn com.sinovoice.hcicloudsdk.**
-keep class com.sinovoice.hcicloudsdk.**{*;}

#灵云混淆
-dontwarn com.sinovoice.hcicloudsdk.**
-keep class com.sinovoice.hcicloudsdk.** {*;}

#友盟
-keep class com.umeng.** {*;}
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

# Retrofit
# Retrofit does reflection on generic parameters and InnerClass is required to use Signature.
-keepattributes Signature, InnerClasses
# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**
# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit
# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.-KotlinExtensions
-keep class retrofit2.** { *; }

#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#沉浸式状态栏
-keep class com.gyf.barlibrary.* {*;}

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#===================================================
-keep class com.callme.platform.widget.**{ *;}
-keep class com.callme.platform.util.**{ *;}
-keep class com.callme.platform.mvvm.**{ *;}
-keep class com.callme.platform.listener.**{ *;}
-keep class com.callme.platform.common.**{ *;}
-keep class com.callme.platform.base.**{ *;}
-keep class com.callme.platform.ApiRequestManager { *;}
-keep class com.callme.platform.ApiRequestManager { *;}
-keep class com.callme.platform.HttpConfigure { *;}
-keep class com.callme.platform.HttpHeader { *;}
-keep class com.callme.platform.RetrofitManager { *;}

-keep class com.callme.platform.listener.*{*;}
-keep class com.callme.platform.api.listener.*{*;}

#baidu统计
-keep class com.baidu.bottom.** { *; }
-keep class com.baidu.mobstat.** { *; }

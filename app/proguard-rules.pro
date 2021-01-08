# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#保留本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#不被混淆
-keep class org.greenrobot.*.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao{
    public static java.lang.String TABLENAME;
}
# 不要混淆导入的包，忽略警告
-dontwarn com.baidu.**
-keep class com.baidu.*.** {*;}
-dontwarn com.alibaba.**
-keep class com.alibaba.*.** {*;}
-dontwarn okhttp3.**
-keep class okhttp3.*.** {*;}
-dontwarn okio.**
-keep class okio.*.** {*;}

-keep class **$Properties
# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**
# ignore packge warnings
#-ignorewarnings


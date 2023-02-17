# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
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
# Serializer for classes with named companion objects are retrieved using `getDeclaredClasses`.
# If you have any, replace classes with those containing named companion objects.
-keepattributes InnerClasses # Needed for `getDeclaredClasses`.

-if @kotlinx.serialization.Serializable class
cn.chitanda.app.imovie.core.model.**, # <-- List serializable classes with named companions.
cn.chitanda.app.imovie.core.model.**2
{
    static **$* *;
}
-keepnames class <1>$$serializer { # -keepnames suffices; class is kept when serializer() is kept.
    static <1>$$serializer INSTANCE;
}

# Keep both serializer and serializable classes to save the attribute InnerClasses
-keepclasseswithmembers, allowshrinking, allowobfuscation, allowaccessmodification class
cn.chitanda.app.imovie.core.model.**, # <-- List serializable classes with named companions.
cn.chitanda.app.imovie.core.model.**2
{
    *;
}
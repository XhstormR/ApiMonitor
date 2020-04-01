-adaptclassstrings
-adaptresourcefilenames
-adaptresourcefilecontents
-allowaccessmodification

-obfuscationdictionary proguard-dict.txt
-classobfuscationdictionary proguard-dict.txt
-packageobfuscationdictionary proguard-dict.txt

-repackageclasses com.example.leo.monitor

-keepclasseswithmembers class com.example.leo.monitor.xposed.Hook

#-keepclassmembernames class com.example.leo.monitor.model.** {
#    <fields>;
#}
#
#-keepclassmembers class com.example.leo.monitor.model.** {
#    public <methods>;
#}

-keepclassmembers enum com.example.leo.monitor.model.** {
    <fields>;
}

# Workaround for https://github.com/square/moshi/issues/1049
-keep class kotlin.Metadata { *; }

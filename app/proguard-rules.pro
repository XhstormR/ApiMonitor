-adaptclassstrings
-adaptresourcefilenames
-adaptresourcefilecontents
-allowaccessmodification

-obfuscationdictionary proguard-dict.txt
-classobfuscationdictionary proguard-dict.txt
-packageobfuscationdictionary proguard-dict.txt

-repackageclasses com.example.leo.monitor

-keepclasseswithmembers class com.example.leo.monitor.xposed.Hook

-keepclassmembernames class com.example.leo.monitor.xposed.HookConfig {
    <fields>;
}

-keepclassmembernames class com.example.leo.monitor.model.response.* {
    <fields>;
}

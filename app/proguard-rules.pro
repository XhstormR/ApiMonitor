-adaptclassstrings
-adaptresourcefilenames
-adaptresourcefilecontents
-allowaccessmodification

-obfuscationdictionary proguard-dict.txt
-classobfuscationdictionary proguard-dict.txt
-packageobfuscationdictionary proguard-dict.txt

-repackageclasses com.example.leo.myapplication

-keepclasseswithmembers class com.example.leo.myapplication.xposed.Hook

-keepclassmembernames class com.example.leo.myapplication.xposed.HookConfig {
    <fields>;
}

-keepclassmembernames class com.example.leo.myapplication.model.response.* {
    <fields>;
}

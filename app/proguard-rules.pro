-adaptclassstrings
-adaptresourcefilenames
-adaptresourcefilecontents
-allowaccessmodification

-obfuscationdictionary proguard-dict.txt
-classobfuscationdictionary proguard-dict.txt
-packageobfuscationdictionary proguard-dict.txt

-repackageclasses com.example.myapplication

-keepclasseswithmembers class com.example.myapplication.Hook

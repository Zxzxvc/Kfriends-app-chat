# Kfriends Chat App - Android

تطبيق محادثة **Kfriends Chat** لنظام Android مبني بـ HTML/CSS/JavaScript و Kotlin.

## المميزات

- 💬 عرض المحادثات من ملفات JSON
- 🌙 واجهة داكنة أنيقة
- 🇸🇦 دعم كامل للغة العربية (RTL)
- 📱 يعمل بدون إنترنت (Offline-first)
- 💾 حفظ المحادثات في LocalStorage
- 📂 استيراد ملفات JSON متعددة

## المتطلبات

- Android SDK 24 أو أحدث
- Android Studio (أو أي IDE يدعم Gradle)
- Java 11 أو أحدث
- Gradle 8.0 أو أحدث

## البناء والتشغيل

### باستخدام Android Studio

1. افتح Android Studio
2. اختر `File → Open` واختر مجلد `KfriendsChatApp`
3. انتظر حتى ينتهي Gradle من التحميل
4. اضغط على `Build → Build Bundle(s) / APK(s) → Build APK(s)`
5. انتظر حتى ينتهي البناء
6. سيظهر الملف APK في: `app/build/outputs/apk/debug/app-debug.apk`

### باستخدام سطر الأوامر (Gradle)

```bash
cd KfriendsChatApp

# بناء APK (Debug)
./gradlew assembleDebug

# بناء APK (Release)
./gradlew assembleRelease

# تثبيت على جهاز متصل
./gradlew installDebug
```

## هيكل المشروع

```
KfriendsChatApp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/kfriends/chat/
│   │       │   └── MainActivity.kt          # النشاط الرئيسي
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml    # تخطيط الواجهة
│   │       │   ├── values/
│   │       │   │   ├── strings.xml          # النصوص
│   │       │   │   ├── colors.xml           # الألوان
│   │       │   │   └── styles.xml           # الأنماط
│   │       │   └── drawable/
│   │       └── AndroidManifest.xml          # ملف البيان
│   ├── build.gradle                         # إعدادات البناء
│   └── proguard-rules.pro                   # قواعد ProGuard
├── build.gradle                             # إعدادات المشروع الرئيسية
├── settings.gradle                          # إعدادات Gradle
└── gradle.properties                        # خصائص Gradle
```

## الملفات الرئيسية

### MainActivity.kt
- يحتوي على النشاط الرئيسي للتطبيق
- يدير WebView وتحميل الكود HTML/CSS/JS
- يتعامل مع صلاحيات الملفات والكاميرا
- يوفر واجهة لاختيار الملفات

### activity_main.xml
- تخطيط الواجهة الرئيسية
- يحتوي على WebView لعرض المحتوى

### strings.xml, colors.xml, styles.xml
- موارد التطبيق (النصوص والألوان والأنماط)

## الصلاحيات المطلوبة

```xml
- android.permission.INTERNET
- android.permission.READ_EXTERNAL_STORAGE
- android.permission.WRITE_EXTERNAL_STORAGE
- android.permission.CAMERA (اختياري)
```

## إنشاء ملف APK للإصدار

```bash
# بناء APK للإصدار (Release)
./gradlew assembleRelease

# سيكون الملف في:
# app/build/outputs/apk/release/app-release-unsigned.apk

# لتوقيع الملف (يتطلب keystore):
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 \
  -keystore my-release-key.keystore \
  app-release-unsigned.apk alias_name

# محاذاة الملف (اختياري لكن موصى به):
zipalign -v 4 app-release-unsigned.apk app-release.apk
```

## استكشاف الأخطاء

### الخطأ: "Build failed"
- تأكد من تثبيت Android SDK بشكل صحيح
- قم بتحديث Gradle: `./gradlew wrapper --gradle-version=8.0.0`

### الخطأ: "WebView not loading"
- تأكد من تفعيل JavaScript في الإعدادات
- تحقق من أن ملف HTML صحيح

### الخطأ: "Permission denied"
- تأكد من منح الصلاحيات المطلوبة
- على Android 6.0+، يتم طلب الصلاحيات في وقت التشغيل

## الترخيص

هذا المشروع مفتوح المصدر.

## الدعم

للمزيد من المعلومات، راجع:
- [توثيق Android](https://developer.android.com/)
- [توثيق Gradle](https://gradle.org/docs/)
- [توثيق Kotlin](https://kotlinlang.org/docs/)

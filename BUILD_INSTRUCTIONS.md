# تعليمات بناء تطبيق Kfriends Chat APK

## الطريقة الأولى: استخدام Android Studio (الموصى به)

### المتطلبات:
- Android Studio (تحميل من https://developer.android.com/studio)
- Android SDK 34 (سيتم تحميله تلقائياً)
- Java 11 أو أحدث

### خطوات البناء:

1. **فتح المشروع:**
   - افتح Android Studio
   - اختر `File → Open`
   - اختر مجلد `KfriendsChatApp`
   - انتظر حتى ينتهي Gradle من التحميل

2. **بناء APK:**
   - من القائمة العلوية: `Build → Build Bundle(s) / APK(s) → Build APK(s)`
   - أو استخدم الاختصار: `Ctrl+Shift+B` (Windows/Linux) أو `Cmd+Shift+B` (Mac)

3. **انتظر النتيجة:**
   - سيظهر إشعار عند انتهاء البناء
   - اضغط على `Locate` لفتح مجلد الملف

4. **موقع الملف:**
   - `app/build/outputs/apk/debug/app-debug.apk`

---

## الطريقة الثانية: استخدام سطر الأوامر (Gradle)

### المتطلبات:
- Java 11 أو أحدث
- Gradle 8.0 أو أحدث (سيتم تحميله تلقائياً عبر Gradle Wrapper)

### خطوات البناء:

```bash
# 1. الانتقال إلى مجلد المشروع
cd KfriendsChatApp

# 2. بناء APK (Debug)
./gradlew assembleDebug

# أو على Windows:
gradlew.bat assembleDebug

# 3. انتظر النتيجة
# سيكون الملف في: app/build/outputs/apk/debug/app-debug.apk
```

### بناء APK للإصدار (Release):

```bash
./gradlew assembleRelease

# الملف سيكون في: app/build/outputs/apk/release/app-release-unsigned.apk
```

---

## الطريقة الثالثة: استخدام Docker (متقدم)

إذا كنت تريد بناء APK في بيئة معزولة:

```bash
# بناء صورة Docker
docker build -t kfriends-chat-builder .

# بناء APK
docker run --rm -v $(pwd):/app kfriends-chat-builder \
  /app/gradlew -p /app assembleDebug
```

---

## استكشاف الأخطاء الشائعة

### ❌ خطأ: "Build failed"
**الحل:**
```bash
# نظف البناء السابق
./gradlew clean

# حاول البناء مرة أخرى
./gradlew assembleDebug
```

### ❌ خطأ: "Android SDK not found"
**الحل:**
- في Android Studio: `File → Settings → Appearance & Behavior → System Settings → Android SDK`
- تأكد من تثبيت SDK Platform 34
- اضغط على `Apply` و `OK`

### ❌ خطأ: "Java version not compatible"
**الحل:**
```bash
# تحقق من إصدار Java
java -version

# يجب أن يكون 11 أو أحدث
# إذا لم يكن، قم بتثبيت Java 11:
# Ubuntu/Debian:
sudo apt-get install openjdk-11-jdk

# macOS:
brew install openjdk@11
```

### ❌ خطأ: "Gradle wrapper not found"
**الحل:**
```bash
# قم بتحميل Gradle wrapper
gradle wrapper --gradle-version=8.0.0
```

---

## التحقق من البناء

بعد البناء الناجح، تحقق من وجود الملف:

```bash
# Linux/Mac:
ls -lh app/build/outputs/apk/debug/app-debug.apk

# Windows:
dir app\build\outputs\apk\debug\app-debug.apk
```

---

## تثبيت APK على جهاز

### باستخدام Android Studio:
1. وصّل جهاز Android بالكمبيوتر
2. اضغط على `Run → Run 'app'` أو `Shift+F10`

### باستخدام ADB (Android Debug Bridge):
```bash
# تثبيت APK
adb install app/build/outputs/apk/debug/app-debug.apk

# أو إعادة التثبيت (إذا كان مثبتاً):
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## معلومات إضافية

### حجم APK:
- Debug APK: ~15-20 MB
- Release APK: ~10-15 MB

### الحد الأدنى من متطلبات النظام:
- Android 7.0 (API 24) أو أحدث
- 50 MB من مساحة التخزين

### المميزات المدعومة:
- ✅ عرض المحادثات من ملفات JSON
- ✅ دعم اللغة العربية (RTL)
- ✅ يعمل بدون إنترنت
- ✅ حفظ المحادثات في LocalStorage
- ✅ استيراد ملفات متعددة

---

## الدعم والمساعدة

للمزيد من المعلومات:
- [توثيق Android](https://developer.android.com/docs)
- [توثيق Gradle](https://gradle.org/docs/)
- [توثيق Kotlin](https://kotlinlang.org/docs/)


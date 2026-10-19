# PKJ KIMPUL (APK)
Website: https://eduspacedashboardsekolah.vercel.app/

## Cara 1 - Android Studio
1. Buka folder ini di Android Studio (versi terbaru), tunggu Gradle sync selesai.
2. Menu Build > Build APK(s).
3. Hasil: app/build/outputs/apk/debug/app-debug.apk

## Cara 2 - Tanpa install apa pun (GitHub Actions)
1. Upload isi folder ini ke repository GitHub baru.
2. Buka tab Actions > "Build APK" > tunggu selesai.
3. Download artifact "PKJ-KIMPUL-apk" (berisi debug dan release).

## Release APK dengan signing sendiri
keytool -genkeypair -v -keystore pkj-kimpul.jks -alias pkj -keyalg RSA -keysize 2048 -validity 10000
Buat file keystore.properties di folder root:
  storeFile=pkj-kimpul.jks
  storePassword=PASSWORD_ANDA
  keyAlias=pkj
  keyPassword=PASSWORD_ANDA
Lalu jalankan: gradle assembleRelease
Hasil: app/build/outputs/apk/release/app-release.apk
Simpan file .jks dan password di tempat aman. Jangan hilang dan jangan di-upload ke publik.

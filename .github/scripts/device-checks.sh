#!/usr/bin/env bash
# Runs on a booted emulator: instrumented UI tests (which save screenshots),
# then installs the minified release build and checks that it starts without crashing.
set -euo pipefail

OUT=device-results
PKG=com.sopnolikhi.booksmyfriend
mkdir -p "$OUT/screenshots"

# A freshly booted emulator can still show the lock screen or a system dialog that steals
# window focus from the app under test; settle it first.
adb shell wm dismiss-keyguard || true
adb shell input keyevent 82 || true
adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS || true
sleep 10
adb shell am broadcast -a android.intent.action.CLOSE_SYSTEM_DIALOGS || true

TESTS_OK=true
./gradlew connectedDebugAndroidTest -Pandroid.injected.androidTest.leaveApksInstalledAfterRun=true || TESTS_OK=false
if [ "$TESTS_OK" = false ]; then
  echo "::group::Instrumented test failures"
  python3 - <<'PY'
import glob, re
for f in glob.glob("app/build/outputs/androidTest-results/connected/**/*.xml", recursive=True):
    for name, body in re.findall(r'<testcase name="([^"]+)"[^>]*>\s*<failure[^>]*>(.*?)</failure>', open(f).read(), re.S):
        print(f"FAILED {name}:\n" + "\n".join(body.splitlines()[:40]))
PY
  echo "::endgroup::"
fi

adb pull "/sdcard/Android/data/$PKG/files/screenshots/." "$OUT/screenshots/" || true
adb uninstall "$PKG" || true

# Release smoke test: sign the R8-minified release APK with the debug key and launch it.
BUILD_TOOLS=$(ls -d "$ANDROID_HOME"/build-tools/* | sort -V | tail -1)
UNSIGNED=$(ls app/build/outputs/apk/release/*-unsigned.apk | head -1)
"$BUILD_TOOLS/zipalign" -f -p 4 "$UNSIGNED" "$OUT/release-aligned.apk"
"$BUILD_TOOLS/apksigner" sign --ks "$HOME/.android/debug.keystore" --ks-pass pass:android \
  --key-pass pass:android --ks-key-alias androiddebugkey --out "$OUT/app-release-smoke.apk" "$OUT/release-aligned.apk"
adb install -r "$OUT/app-release-smoke.apk"

adb logcat -c
adb shell monkey -p "$PKG" -c android.intent.category.LAUNCHER 1
sleep 8
adb shell input tap 540 1200 || true
sleep 2
adb exec-out screencap -p > "$OUT/release-launch.png"
adb logcat -d > "$OUT/release-logcat.txt"

if grep -E "FATAL EXCEPTION|AndroidRuntime: Process: $PKG" "$OUT/release-logcat.txt"; then
  echo "Release build crashed on launch"
  exit 1
fi
adb shell pidof "$PKG" || { echo "Release app is not running"; exit 1; }
echo "Release smoke test passed"
[ "$TESTS_OK" = true ] || { echo "Instrumented tests failed"; exit 1; }

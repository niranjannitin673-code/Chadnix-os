# Chadnix

A floating window manager for Android — a single, non-root Android app that
draws a taskbar over whatever app is running (using the `SYSTEM_ALERT_WINDOW`
overlay permission), with a draggable handle and a floating app drawer.

Launcher/Home-replacement mode has been removed. Chadnix is now overlay-only:
it never touches your default home screen.

No root required.

## Project layout

```
Chadnix/
├── app/
│   ├── src/main/java/com/chadnix/os/
│   │   ├── core/    -> ChadnixApp, AppInfo, AppRepository
│   │   ├── ui/      -> MainActivity (start/stop screen)
│   │   └── overlay/ -> ChadnixOverlayService + OverlayAppListAdapter
│   ├── src/main/res/    -> layouts, icon, theme
│   └── src/main/AndroidManifest.xml
├── .github/workflows/build.yml   -> builds an APK on GitHub Actions
├── build.gradle.kts
└── settings.gradle.kts
```

## How to build

### Option A — No computer needed: build via GitHub Actions (from your phone)

1. Create a free GitHub account, then a new repository (e.g. `chadnix`).
2. Upload this `Chadnix` folder's contents into that repo.
3. Go to the **Actions** tab → "Build Chadnix APK" runs automatically on push
   (or tap **Run workflow** to trigger it manually).
4. When it finishes, open the run → **Artifacts** → download
   `chadnix-debug-apk`.
5. Download that APK to your phone and install it (allow "install unknown
   apps" once if asked).

### Option B — With a computer: Android Studio

1. Install Android Studio (Koala or newer).
2. `File > Open` and select the `Chadnix/` folder.
3. Let Gradle sync (needs internet the first time).
4. Run on a device or emulator (`minSdk 26`, i.e. Android 8.0+).

## Using it

1. Open Chadnix, tap **Start Chadnix**.
2. Grant "Display over other apps" when prompted.
   - On Android 13+, if the APK was sideloaded, Android may show
     "Access denied for this app" the first time. Go to
     **App info → ⋮ (three-dot menu) → Allow restricted settings**, then
     retry granting the overlay permission.
3. Go back into Chadnix and tap **Start Chadnix** again — a small floating
   taskbar with a drag handle appears over your current app.
4. Tap **Apps** on the floating taskbar for a floating app drawer; tap an
   app icon to launch it. Tap **X** to stop Chadnix.

## What's in this skeleton vs. what's next

Working now:
- Real installed-app enumeration (`PackageManager`)
- Draggable floating taskbar (`WindowManager` overlay), toggleable app drawer
- Runs as a proper Android 8+ foreground service (won't get killed immediately)

Natural next steps (not yet built):
- Real window/task switching (currently just launches apps full-screen; true
  floating *resizable* app windows need per-app overlay surfaces — a bigger
  follow-up)
- Theming system (swappable color/icon themes)
- Settings screen

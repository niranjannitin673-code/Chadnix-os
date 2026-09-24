# Chadnix

An IceWM-inspired window manager for Android — built as a single, non-root Android app
with **two modes** the user can choose between:

1. **Launcher Mode** — registers as a selectable Home app. Replaces the stock home
   screen with a Chadnix desktop: full app grid + a bottom taskbar (start button, clock).
2. **Overlay Mode** — a floating taskbar drawn on top of whatever app is running,
   using the `SYSTEM_ALERT_WINDOW` overlay permission. Doesn't touch the home screen;
   drag it anywhere, tap "Apps" for a floating app drawer, tap "X" to stop it.

No root required for either mode.

## Project layout

```
Chadnix/
├── app/
│   ├── src/main/java/com/chadnix/os/
│   │   ├── core/       -> ChadnixApp (mode storage), AppInfo, AppRepository
│   │   ├── ui/          -> ModeChooserActivity (first screen)
│   │   ├── launcher/    -> LauncherActivity + AppGridAdapter (Launcher mode)
│   │   └── overlay/     -> ChadnixOverlayService + OverlayAppListAdapter (Overlay mode)
│   ├── src/main/res/    -> layouts, icon, theme (dark, IceWM-ish palette)
│   └── src/main/AndroidManifest.xml
├── build.gradle.kts
└── settings.gradle.kts
```

## How to build

This skeleton was written by hand (Kotlin + Android SDK APIs only — no external
window-manager library, no Termux/Linux dependency).

### Option A — No computer needed: build via GitHub Actions (from your phone)

A ready-made workflow (`.github/workflows/build.yml`) is already included. It
builds a debug APK in the cloud every time you push, or whenever you trigger
it manually — no Android Studio, no local machine required.

1. On github.com (works fine from a phone browser or the GitHub app), create a
   **free account** if you don't have one, then create a **new repository**
   (e.g. `chadnix`), set to Public or Private, no need to add a README (this
   project already has one).
2. Upload this whole `Chadnix` folder's contents into that repo. Easiest way
   on mobile: on the repo page tap **Add file → Upload files**, then upload
   everything (you may need to do it a few sub-folders at a time, since the
   web uploader doesn't accept a raw folder — a file manager app that can
   extract this zip and re-upload piece by piece works, or use the GitHub
   mobile app which also supports file upload per-folder).
3. Once pushed, go to the **Actions** tab in your repo → you'll see "Build
   Chadnix APK" running automatically (triggered by the push). If it doesn't
   start, open it and tap **Run workflow**.
4. When it finishes (green check), open that run → under **Artifacts** →
   download **chadnix-debug-apk**. That's your installable `.apk`.
5. Download the APK to your Android phone, tap it to install (you'll need to
   allow "install unknown apps" for your browser/file manager once), and open
   Chadnix.

No computer touched at any point — GitHub's servers do the actual compiling.

### Option B — With a computer: Android Studio

1. Install **Android Studio** (Koala or newer).
2. `File > Open` and select the `Chadnix/` folder — Android Studio will generate
   the missing Gradle wrapper files automatically on first sync.
3. Let Gradle sync (needs internet the first time, to fetch AndroidX/Material
   dependencies and the Gradle/Kotlin plugins listed in `build.gradle.kts`).
4. Run on a device or emulator (`minSdk 26`, i.e. Android 8.0+).

## Trying each mode

- **Launcher Mode**: tap it in the chooser screen → Android's "Select Home app"
  settings page opens → pick Chadnix. Press the device Home button afterward to
  see the Chadnix desktop.
- **Overlay Mode**: tap it in the chooser screen → grant the "Display over other
  apps" permission when asked → go back into Chadnix and tap Overlay Mode again
  → a small floating taskbar with a drag-handle appears over your current app.

## What's in this skeleton vs. what's next

Working now:
- Mode chooser with persisted choice (SharedPreferences)
- Real installed-app enumeration (`PackageManager`) for both modes
- Launcher: app grid (RecyclerView/GridLayoutManager) + taskbar with start-menu toggle
- Overlay: draggable floating taskbar (WindowManager overlay) + toggleable app drawer,
  running as a proper Android 8+ foreground service (won't get killed immediately)

Natural next steps (not yet built):
- Real window/task switching in Overlay mode (currently just launches apps full-screen;
  true floating *resizable* app windows need `TYPE_APPLICATION_OVERLAY` per-window
  surfaces, which is a bigger follow-up)
- IceWM-style theming system (swappable color/icon themes, like `.icewm/theme` files)
- Widgets support, wallpaper support in Launcher mode
- Settings screen (currently mode switch is the only setting)

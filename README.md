# GAC1_com.yftech.multimedia.apk

> Decompiled source repository for `com.yftech.multimedia.apk`.

## Summary

- **APK file name**: `com.yftech.multimedia.apk`
- **Package name**: `com.yftech.multimedia`
- **Version**: `?` (versionCode `?`)
- **Min SDK**: `27`  |  **Target SDK**: `28`
- **Total decompiled classes**: `7532`
- **Decompiled on**: 2026-10-18 12:25:39 UTC
- **Source repo**: [ammisaidkaidi/yftech-apks](https://github.com/ammisaidkaidi/yftech-apks)

## What this app does (inferred)

- Media playback / management
- Multimedia playback / management
- Bluetooth access
- External storage access
- Overlay / system alert window
- Filesystem mount/unmount

Inference is based on the APK file name, package name, declared permissions, registered services/receivers, and top-level Java package structure. Manual review of the source under `jadx_out/sources/` is recommended for definitive behavior.

## Permissions

- `android.permission.BLUETOOTH`  (BLUETOOTH)
- `android.permission.MOUNT_UNMOUNT_FILESYSTEMS`  (MOUNT_UNMOUNT_FILESYSTEMS)
- `android.permission.READ_EXTERNAL_STORAGE`  (READ_EXTERNAL_STORAGE)
- `android.permission.RECEIVE_BOOT_COMPLETED`  (RECEIVE_BOOT_COMPLETED)
- `android.permission.SYSTEM_ALERT_WINDOW`  (SYSTEM_ALERT_WINDOW)
- `android.permission.WRITE_EXTERNAL_STORAGE`  (WRITE_EXTERNAL_STORAGE)
- `android.permission.WRITE_SECURE_SETTINGS`  (WRITE_SECURE_SETTINGS)
- `com.yftech.permission.media.service`
- `com.yftech.permission.service.bluetooth`
- `com.yftech.permission.service.car`
- `com.yftech.permission.service.main.audio`
- `com.yftech.permission.service.main.audio.effect`
- `com.yftech.permission.service.main.input`
- `com.yftech.permission.service.main.screen`
- `com.yftech.permission.service.transfer.phonelink`
- `com.yftech.permission.service.upgrade.upgrade`
- `com.yftech.permission.service.vehicle.COMMON`
- `com.yftech.permission.test`
- `com.yftech.persmission.CAR_READ_STATUS_INFOR`
- `com.yftech.persmission.READ_SYSTEM_STATUS_INFOR`

## App components (from AndroidManifest.xml)

**Application class**: `com.yftech.multimedia.MediaApplication`
**Launcher activity**: `com.yftech.multimedia.MainActivity`

### Activities (13)

- `com.yftech.multimedia.MainActivity`
- `com.yftech.libgallery.ui.FullScreenImageActivity`
- `com.yftech.libgallery.ui.BrowseImageActivity`
- `com.yftech.libmedia.player.PlayActivity`
- `com.yftech.libmusic.MusicMainActivity`
- `com.yftech.libmusic.ui.MusicListFromHardDiskActivity`
- `com.yftech.libmusic.ui.MusicSearchFromHardDiskActivity`
- `com.yftech.libradio.RadioMainActivity`
- `com.yftech.libradio.dabui.DabSortListActivity`
- `com.yftech.libradio.dabui.DabFavorListActivity`
- `com.yftech.libradio.dabui.epg.EpgActivity`
- `com.yftech.libradio.RadioListActivity`
- `com.yftech.libradio.RadioFavorActivity`

### Services (0)

- (none)

### Broadcast receivers (1)

- `com.yftech.libradio.receiver.AnnounceReceiver`

### Content providers (0)

- (none)

## Java package structure

**Top-level packages**: `android`, `androidx`, `bolts`, `com`, `javax`, `kotlin`, `org`

**Detected third-party libraries**:

- Android framework (system)  (1 top-level entries)
- Android Jetpack (AndroidX libraries)  (1 top-level entries)
- JDK standard library (extensions)  (1 top-level entries)
- Kotlin stdlib  (1 top-level entries)

**Custom (non-library) packages — depth 2**:

- `com.alibaba`
- `com.alibaba.android`
- `com.alibaba.android.vlayout`
- `com.android`
- `com.android.framework`
- `com.android.framework.protobuf`
- `com.android.ims`
- `com.android.ims.internal`
- `com.android.incident`
- `com.android.internal`
- `com.android.internal.accessibility`
- `com.android.internal.alsa`
- `com.android.internal.annotations`
- `com.android.internal.app`
- `com.android.internal.appwidget`
- `com.android.internal.backup`
- `com.android.internal.colorextraction`
- `com.android.internal.content`
- `com.android.internal.database`
- `com.android.internal.globalactions`
- `com.android.internal.graphics`
- `com.android.internal.hardware`
- `com.android.internal.http`
- `com.android.internal.inputmethod`
- `com.android.internal.location`
- `com.android.internal.logging`
- `com.android.internal.messages`
- `com.android.internal.midi`
- `com.android.internal.ml`
- `com.android.internal.net`
- `com.android.internal.notification`
- `com.android.internal.os`
- `com.android.internal.policy`
- `com.android.internal.preference`
- `com.android.internal.print`
- `com.android.internal.statusbar`
- `com.android.internal.telecom`
- `com.android.internal.telephony`
- `com.android.internal.textservice`
- `com.android.internal.transition`
- `com.android.internal.usb`
- `com.android.internal.util`
- `com.android.internal.view`
- `com.android.internal.widget`
- `com.android.net`
- `com.android.server`
- `com.android.server.am`
- `com.android.server.backup`
- `com.android.server.connectivity`
- `com.android.server.fingerprint`
- ... (185 more)

## Key classes & method signatures

Per custom package, the top-level classes (up to 8 each) and their first few public method signatures.

### `com.alibaba`  (0 class files in this folder)

(no top-level classes in this package)

### `com.alibaba.android`  (0 class files in this folder)

(no top-level classes in this package)

### `com.alibaba.android.vlayout`  (18 class files in this folder)

- **class `BuildConfig`**  (`BuildConfig.java`, ~12 lines)

- **class `Cantor`**  (`Cantor.java`, ~20 lines)
  - `long getCantor(...)`
  - `void reverseCantor(...)`

- **class `DelegateAdapter`**  (`DelegateAdapter.java`, ~508 lines)
  - `abstract LayoutHelper onCreateLayoutHelper(...)`
  - `int getIndex(...)`
  - `int getStartPosition(...)`
  - `void onChanged(...)`
  - `void onItemRangeChanged(...)`
  - `void onItemRangeChanged(...)`
  - `void onItemRangeInserted(...)`
  - `void onItemRangeMoved(...)`
  - `void onItemRangeRemoved(...)`
  - `void updateStartPositionAndIndex(...)`
  - `int getItemCount(...)`
  - `void onBindViewHolder(...)`
  - `LayoutHelper onCreateLayoutHelper(...)`
  - `void addAdapter(...)`
  - `void addAdapter(...)`

- **class `ExposeLinearLayoutManagerEx`**  (`ExposeLinearLayoutManagerEx.java`, ~1320 lines)
  - `void assignFromView(...)`
  - `boolean assignFromViewIfValid(...)`
  - `String toString(...)`
  - `boolean hasMore(...)`
  - `View next(...)`
  - `void setFlags(...)`
  - `boolean requireUpdated(...)`
  - `void assertNotInLayoutOrScroll(...)`
  - `PointF computeScrollVectorForPosition(...)`
  - `int findFirstVisibleItemPosition(...)`
  - `int findLastVisibleItemPosition(...)`
  - `void hideView(...)`
  - `boolean isEnableMarginOverLap(...)`
  - `void onAnchorReady(...)`
  - `void onAttachedToWindow(...)`

- **class `LayoutHelper`**  (`LayoutHelper.java`, ~132 lines)
  - `abstract void adjustLayout(...)`
  - `abstract void afterLayout(...)`
  - `abstract void beforeLayout(...)`
  - `abstract void bindLayoutView(...)`
  - `void checkAnchorInfo(...)`
  - `abstract void clear(...)`
  - `abstract int computeAlignOffset(...)`
  - `abstract int computeMarginEnd(...)`
  - `abstract int computeMarginStart(...)`
  - `abstract int computePaddingEnd(...)`
  - `abstract int computePaddingStart(...)`
  - `abstract void doLayout(...)`
  - `View getFixedView(...)`
  - `abstract int getItemCount(...)`
  - `List<View> getOffFlowViews(...)`

- **class `LayoutHelperFinder`**  (`LayoutHelperFinder.java`, ~19 lines)
  - `abstract LayoutHelper getLayoutHelper(...)`

- **interface `LayoutManagerHelper`**  (`LayoutManagerHelper.java`, ~95 lines)

- **class `LayoutView`**  (`LayoutView.java`, ~27 lines)

### `com.android`  (0 class files in this folder)

(no top-level classes in this package)

### `com.android.framework`  (0 class files in this folder)

(no top-level classes in this package)

### `com.android.framework.protobuf`  (0 class files in this folder)

(no top-level classes in this package)

### `com.android.ims`  (4 class files in this folder)

- **class `ImsConfig`**  (`ImsConfig.java`, ~233 lines)
  - `void addConfigCallback(...)`
  - `int getConfigInt(...)`
  - `String getConfigString(...)`
  - `String getProvisionedStringValue(...)`
  - `int getProvisionedValue(...)`
  - `boolean isBinderAlive(...)`
  - `void removeConfigCallback(...)`
  - `int setConfig(...)`
  - `int setConfig(...)`
  - `int setProvisionedStringValue(...)`
  - `int setProvisionedValue(...)`

- **interface `ImsConfigListener`**  (`ImsConfigListener.java`, ~145 lines)
  - `IBinder asBinder(...)`
  - `String getInterfaceDescriptor(...)`
  - `void onGetFeatureResponse(...)`
  - `void onGetVideoQuality(...)`
  - `void onSetFeatureResponse(...)`
  - `void onSetVideoQuality(...)`
  - `ImsConfigListener asInterface(...)`
  - `IBinder asBinder(...)`
  - `boolean onTransact(...)`

- **class `ImsException`**  (`ImsException.java`, ~24 lines)
  - `int getCode(...)`

- **interface `ImsUtInterface`**  (`ImsUtInterface.java`, ~71 lines)

### `com.android.ims.internal`  (19 class files in this folder)

- **interface `IImsCallSession`**  (`IImsCallSession.java`, ~868 lines)
  - `void accept(...)`
  - `IBinder asBinder(...)`
  - `void close(...)`
  - `void deflect(...)`
  - `void extendToConference(...)`
  - `String getCallId(...)`
  - `ImsCallProfile getCallProfile(...)`
  - `String getInterfaceDescriptor(...)`
  - `ImsCallProfile getLocalCallProfile(...)`
  - `String getProperty(...)`
  - `ImsCallProfile getRemoteCallProfile(...)`
  - `int getState(...)`
  - `IImsVideoCallProvider getVideoCallProvider(...)`
  - `void hold(...)`
  - `void inviteParticipants(...)`

- **interface `IImsCallSessionListener`**  (`IImsCallSessionListener.java`, ~876 lines)
  - `IBinder asBinder(...)`
  - `void callSessionConferenceExtendFailed(...)`
  - `void callSessionConferenceExtendReceived(...)`
  - `void callSessionConferenceExtended(...)`
  - `void callSessionConferenceStateUpdated(...)`
  - `void callSessionHandover(...)`
  - `void callSessionHandoverFailed(...)`
  - `void callSessionHeld(...)`
  - `void callSessionHoldFailed(...)`
  - `void callSessionHoldReceived(...)`
  - `void callSessionInviteParticipantsRequestDelivered(...)`
  - `void callSessionInviteParticipantsRequestFailed(...)`
  - `void callSessionMayHandover(...)`
  - `void callSessionMergeComplete(...)`
  - `void callSessionMergeFailed(...)`

- **interface `IImsConfig`**  (`IImsConfig.java`, ~271 lines)
  - `IBinder asBinder(...)`
  - `void getFeatureValue(...)`
  - `String getInterfaceDescriptor(...)`
  - `String getProvisionedStringValue(...)`
  - `int getProvisionedValue(...)`
  - `void getVideoQuality(...)`
  - `boolean getVolteProvisioned(...)`
  - `void setFeatureValue(...)`
  - `int setProvisionedStringValue(...)`
  - `int setProvisionedValue(...)`
  - `void setVideoQuality(...)`
  - `IImsConfig asInterface(...)`
  - `IBinder asBinder(...)`
  - `boolean onTransact(...)`

- **interface `IImsEcbm`**  (`IImsEcbm.java`, ~107 lines)
  - `IBinder asBinder(...)`
  - `void exitEmergencyCallbackMode(...)`
  - `String getInterfaceDescriptor(...)`
  - `void setListener(...)`
  - `IImsEcbm asInterface(...)`
  - `IBinder asBinder(...)`
  - `boolean onTransact(...)`

- **interface `IImsEcbmListener`**  (`IImsEcbmListener.java`, ~98 lines)
  - `IBinder asBinder(...)`
  - `void enteredECBM(...)`
  - `void exitedECBM(...)`
  - `String getInterfaceDescriptor(...)`
  - `IImsEcbmListener asInterface(...)`
  - `IBinder asBinder(...)`
  - `boolean onTransact(...)`

- **interface `IImsExternalCallStateListener`**  (`IImsExternalCallStateListener.java`, ~81 lines)
  - `IBinder asBinder(...)`
  - `String getInterfaceDescriptor(...)`
  - `void onImsExternalCallStateUpdate(...)`
  - `IImsExternalCallStateListener asInterface(...)`
  - `IBinder asBinder(...)`
  - `boolean onTransact(...)`

- **interface `IImsFeatureStatusCallback`**  (`IImsFeatureStatusCallback.java`, ~79 lines)
  - `IBinder asBinder(...)`
  - `String getInterfaceDescriptor(...)`
  - `void notifyImsFeatureStatus(...)`
  - `IImsFeatureStatusCallback asInterface(...)`
  - `IBinder asBinder(...)`
  - `boolean onTransact(...)`

- **interface `IImsMMTelFeature`**  (`IImsMMTelFeature.java`, ~497 lines)
  - `void addRegistrationListener(...)`
  - `IBinder asBinder(...)`
  - `ImsCallProfile createCallProfile(...)`
  - `IImsCallSession createCallSession(...)`
  - `void endSession(...)`
  - `IImsConfig getConfigInterface(...)`
  - `IImsEcbm getEcbmInterface(...)`
  - `int getFeatureStatus(...)`
  - `String getInterfaceDescriptor(...)`
  - `IImsMultiEndpoint getMultiEndpointInterface(...)`
  - `IImsCallSession getPendingCallSession(...)`
  - `IImsUt getUtInterface(...)`
  - `boolean isConnected(...)`
  - `boolean isOpened(...)`
  - `void removeRegistrationListener(...)`

### `com.android.incident`  (2 class files in this folder)

- **class `Privacy`**  (`Privacy.java`, ~10 lines)

- **class `PrivacyFlags`**  (`PrivacyFlags.java`, ~8 lines)

### `com.android.internal`  (1 class file in this folder)

- **class `R`**  (`R.java`, ~8488 lines)

### `com.android.internal.accessibility`  (1 class file in this folder)

- **class `AccessibilityShortcutController`**  (`AccessibilityShortcutController.java`, ~246 lines)
  - `AccessibilityManager getAccessibilityManagerInstance(...)`
  - `Context getSystemUiContext(...)`
  - `Toast makeToastFromText(...)`
  - `String getLabel(...)`
  - `String getSettingKey(...)`
  - `String getSettingOffValue(...)`
  - `String getSettingOnValue(...)`
  - `void onChange(...)`
  - `void onClick(...)`
  - `void onCancel(...)`
  - `Map<ComponentName, ToggleableFrameworkFeatureInfo> getFrameworkShortcutFeaturesMap(...)`
  - `String getTargetServiceComponentNameString(...)`
  - `boolean isAccessibilityShortcutAvailable(...)`
  - `void onSettingsChanged(...)`
  - `void performAccessibilityShortcut(...)`

### `com.android.internal.alsa`  (3 class files in this folder)

- **class `AlsaCardsParser`**  (`AlsaCardsParser.java`, ~151 lines)
  - `boolean parse(...)`
  - `String getCardDescription(...)`
  - `String getCardName(...)`
  - `int getCardNum(...)`
  - `void log(...)`
  - `void setDeviceAddress(...)`
  - `String textFormat(...)`
  - `AlsaCardRecord findCardNumFor(...)`
  - `int getScanStatus(...)`
  - `int scan(...)`

- **class `AlsaDevicesParser`**  (`AlsaDevicesParser.java`, ~216 lines)
  - `boolean parse(...)`
  - `String textFormat(...)`
  - `int getDefaultDeviceNum(...)`
  - `int getScanStatus(...)`
  - `boolean hasCaptureDevices(...)`
  - `boolean hasMIDIDevices(...)`
  - `boolean hasPlaybackDevices(...)`
  - `int scan(...)`

- **class `LineTokenizer`**  (`LineTokenizer.java`, ~34 lines)

### `com.android.internal.annotations`  (3 class files in this folder)

- **@interface `GuardedBy`**  (`GuardedBy.java`, ~14 lines)

- **@interface `Immutable`**  (`Immutable.java`, ~13 lines)

- **@interface `VisibleForTesting`**  (`VisibleForTesting.java`, ~18 lines)

### `com.android.internal.app`  (50 class files in this folder)

- **class `AccessibilityButtonChooserActivity`**  (`AccessibilityButtonChooserActivity.java`, ~149 lines)
  - `Drawable getDrawable(...)`
  - `String getId(...)`
  - `CharSequence getLabel(...)`
  - `int getCount(...)`
  - `Object getItem(...)`
  - `long getItemId(...)`
  - `View getView(...)`
  - `void onTargetSelected(...)`
  - `void onDismissed(...)`
  - `void onItemClick(...)`

- **class `AlertActivity`**  (`AlertActivity.java`, ~69 lines)
  - `boolean dispatchPopulateAccessibilityEvent(...)`
  - `void cancel(...)`
  - `void dismiss(...)`
  - `boolean dispatchPopulateAccessibilityEvent(...)`
  - `boolean onKeyDown(...)`
  - `boolean onKeyUp(...)`

- **class `AlertController`**  (`AlertController.java`, ~895 lines)
  - `void onClick(...)`
  - `View getView(...)`
  - `void bindView(...)`
  - `View newView(...)`
  - `void onItemClick(...)`
  - `void onItemClick(...)`
  - `void apply(...)`
  - `void handleMessage(...)`
  - `long getItemId(...)`
  - `boolean hasStableIds(...)`
  - `void setHasDecor(...)`
  - `AlertController create(...)`
  - `Button getButton(...)`
  - `int getIconAttributeResId(...)`
  - `ListView getListView(...)`

- **class `AssistUtils`**  (`AssistUtils.java`, ~161 lines)
  - `boolean allowDisablingAssistDisclosure(...)`
  - `boolean isPreinstalledAssistant(...)`
  - `boolean shouldDisclose(...)`
  - `boolean activeServiceSupportsAssistGesture(...)`
  - `boolean activeServiceSupportsLaunchFromKeyguard(...)`
  - `ComponentName getActiveServiceComponentName(...)`
  - `ComponentName getAssistComponentForUser(...)`
  - `void hideCurrentSession(...)`
  - `boolean isSessionRunning(...)`
  - `void launchVoiceAssistFromKeyguard(...)`
  - `void onLockscreenShown(...)`
  - `void registerVoiceInteractionSessionListener(...)`
  - `boolean showSessionForActiveService(...)`

- **class `ChooserActivity`**  (`ChooserActivity.java`, ~1296 lines)
  - `void handleMessage(...)`
  - `int compare(...)`
  - `void addServiceResults(...)`
  - `int getCallerTargetCount(...)`
  - `int getCount(...)`
  - `int getPositionTargetType(...)`
  - `int getServiceTargetCount(...)`
  - `int getStandardTargetCount(...)`
  - `int getUnfilteredCount(...)`
  - `boolean isComponentPinned(...)`
  - `View onCreateView(...)`
  - `void onListRebuilt(...)`
  - `void setShowServiceTargets(...)`
  - `boolean shouldGetResolvedFilter(...)`
  - `boolean showsExtendedInfo(...)`

- **class `ColorDisplayController`**  (`ColorDisplayController.java`, ~332 lines)
  - `void onChange(...)`
  - `boolean isAvailable(...)`
  - `void onSettingChanged(...)`
  - `boolean getAccessibilityTransformActivated(...)`
  - `int getAutoMode(...)`
  - `int getAutoModeRaw(...)`
  - `int getColorMode(...)`
  - `int getColorTemperature(...)`
  - `LocalTime getCustomEndTime(...)`
  - `LocalTime getCustomStartTime(...)`
  - `int getDefaultColorTemperature(...)`
  - `LocalDateTime getLastActivatedTime(...)`
  - `int getMaximumColorTemperature(...)`
  - `int getMinimumColorTemperature(...)`
  - `boolean isActivated(...)`

- **class `ConfirmUserCreationActivity`**  (`ConfirmUserCreationActivity.java`, ~95 lines)
  - `void onClick(...)`
  - `void onCreate(...)`

- **class `DisableCarModeActivity`**  (`DisableCarModeActivity.java`, ~26 lines)

### `com.android.internal.appwidget`  (2 class files in this folder)

- **interface `IAppWidgetHost`**  (`IAppWidgetHost.java`, ~152 lines)
  - `IBinder asBinder(...)`
  - `String getInterfaceDescriptor(...)`
  - `void providerChanged(...)`
  - `void providersChanged(...)`
  - `void updateAppWidget(...)`
  - `void viewDataChanged(...)`
  - `IAppWidgetHost asInterface(...)`
  - `IBinder asBinder(...)`
  - `boolean onTransact(...)`

- **interface `IAppWidgetService`**  (`IAppWidgetService.java`, ~834 lines)
  - `int allocateAppWidgetId(...)`
  - `IBinder asBinder(...)`
  - `boolean bindAppWidgetId(...)`
  - `boolean bindRemoteViewsService(...)`
  - `IntentSender createAppWidgetConfigIntentSender(...)`
  - `void deleteAllHosts(...)`
  - `void deleteAppWidgetId(...)`
  - `void deleteHost(...)`
  - `int[] getAppWidgetIds(...)`
  - `int[] getAppWidgetIdsForHost(...)`
  - `AppWidgetProviderInfo getAppWidgetInfo(...)`
  - `Bundle getAppWidgetOptions(...)`
  - `RemoteViews getAppWidgetViews(...)`
  - `ParceledListSlice getInstalledProvidersForProfile(...)`
  - `String getInterfaceDescriptor(...)`

## Decompilation methodology

This repo was produced by the following pipeline:

1. **APK download** — raw file fetched via the GitHub raw URL (no repo clone).
2. **apktool d** — decoded `AndroidManifest.xml`, resources (`res/`), `assets/`, native libs (`lib/`), and produced `apktool.yml` metadata.
3. **jadx -d** — produced Java-readable decompiled sources under `jadx_out/sources/`.
4. **Static analysis pass** — parsed manifest + walked `jadx_out/sources/` to enumerate packages, classes, methods, and detect known third-party libraries.
5. **README auto-generation** — produced this file with the static analysis results.

### Reproducing

```bash
# 1) download the APK (raw, no clone)
curl -L -o com.yftech.multimedia.apk \
  'https://raw.githubusercontent.com/ammisaidkaidi/yftech-apks/main/com.yftech.multimedia.apk'

# 2) decode resources + manifest
apktool d -f -o apktool_out com.yftech.multimedia.apk

# 3) decompile to Java-readable sources
jadx -d jadx_out --no-res com.yftech.multimedia.apk
```

> Note: `--no-res` is passed to jadx because apktool already decoded resources.

## Directory layout

``
GAC1_com.yftech.multimedia.apk/
├── README.md                  <- this file
├── com.yftech.multimedia.apk                 <- original APK binary
├── apktool_out/               <- apktool output (resources, manifest, smali)
└── jadx_out/                  <- jadx output (Java-readable sources)
```

### apktool_out/
```
├── AndroidManifest.xml
├── apktool.yml
├── lib/
│   ├── arm64-v8a/
│   ├── armeabi-v7a/
│   ├── x86/
│   └── x86_64/
├── original/
│   ├── AndroidManifest.xml
│   └── META-INF/
├── res/
│   ├── anim/
│   ├── animator/
│   ├── color/
│   ├── drawable/
│   ├── drawable-hdpi/
│   ├── drawable-ldrtl-hdpi/
│   ├── drawable-ldrtl-mdpi/
│   ├── drawable-ldrtl-xhdpi/
│   ├── drawable-ldrtl-xxhdpi/
│   ├── drawable-ldrtl-xxxhdpi/
│   ├── drawable-mdpi/
│   ├── drawable-watch/
│   ├── drawable-xhdpi/
│   ├── drawable-xxhdpi/
│   ├── drawable-xxxhdpi/
│   ├── font/
│   ├── interpolator/
│   ├── layout/
│   ├── layout-ar/
│   └── layout-es/
│   └── ... (115 more entries)
├── smali/
│   ├── android/
│   └── com/
├── smali_classes2/
│   ├── android/
│   ├── androidx/
│   └── com/
├── smali_classes3/
│   ├── androidx/
│   ├── bolts/
│   └── com/
├── smali_classes4/
│   ├── com/
│   ├── javax/
│   ├── kotlin/
│   └── org/
└── unknown/
    └── META-INF/
```

### jadx_out/
```
├── resources/
└── sources/
    ├── android/
    ├── androidx/
    ├── bolts/
    ├── com/
    ├── javax/
    ├── kotlin/
    └── org/
```

## Disclaimer

This repository contains decompiled (reverse-engineered) Java source code and resources derived from a binary APK. Decompilation was performed for research, interoperability, and educational purposes.

- The decompiled code is **not** the original source — variable names, comments, generics, and some control flow may have been lost or simplified during compilation/decompilation.
- All trademarks, copyrights, and IP belong to their respective owners (YF Tech / 御驾 (YF) Automotive).
- The author of this repo does not host the original APK on this repo's remote — only the decompiled artifacts. The original APK is publicly listed at the source repository cited above.
- This repository is for **analysis only**. Do not use it to redistribute or rebuild the original application.
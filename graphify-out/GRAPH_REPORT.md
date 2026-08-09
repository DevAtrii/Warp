# Graph Report - .  (2026-08-09)

## Corpus Check
- 249 files · ~65,259 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 1372 nodes · 3081 edges · 117 communities (103 shown, 14 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 89 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Appwidgetmanager / Contentprovider
- Jsonobject / Kserializer
- App / Mainactivity
- Todowarpwidget / Emptytodobody
- Todowidget / Cgfloat
- Preferences / Glanceinternalstate
- Buttoncolors / Colorprovider
- Edgeinsets / Float
- Warpwidget / Renderwarpwidget
- Visibility / Warpalphaelement
- Warpcomposescope / Currentcontainer
- Warpactionid / Warpmodifier
- Equatable / Sendable
- Android / Currentwidgetplatform
- Broadcastframeclock / Recomposer
- Warpnodeholders / Warpboxholder
- Comparable / Warpborderelement
- Iconbutton / Warpimage
- Appintents / Todowidgetbundle
- Warplazycolumncomposable / Warplazyrowcomposable
- Warpglanceclickprepare / Setwarpglanceclickpreparehandler
- Appentity / Entityquery
- Warplazycolumn / Warplazyrow
- Android / Context
- Root / Any
- Appintent / Content
- Addsample / Clear
- Uiview / Bool
- Tojson / Counteractions
- Warpwidget / Clickhandlers
- Counterwidgetstate / Platformcontext
- Anyhashable / Environment
- Foundation / Nsobject
- Widgetenvironment / Warplayoutdirection
- Warpadaptive / Adaptivesize
- Warpwidgetclickid / Embedwarpwidgetidinrootjson
- Warplogger / Warplogger
- Warpwidgetandroidreload / Bundle
- Viewmodifier / Warpimagescalemodifier
- Ios / Nsuserdefaults
- Warpwidgetkitenv / Colorscheme
- Bool / Intentresult
- Warpcontentalignment / Warpcontentalignment
- Warpnodekind / Box
- S / Warpwidget
- Widget / Todowidgetbundle
- Todowidgetview / Composewidgetjson
- Modifier / App
- Android / Widgetupdater
- Warpclicksregistry / Warpclicksregistry
- Android / T
- Towarpwidgetfamily / Warpwidgetfamily
- Kmplibrarypublishplugin / Kmplibrarypublishplugin
- Padding / Padding
- Visibility / Resolvedvisibility
- Isreadytoprint / Counterwidget
- Horizontalalignment / Warpparsedhorizontalalignment
- Counterwidgetclickintent / Init
- Counteractions / Counterwarpclickhandler
- Warpglanceactionkeys / Actionparameters
- Warpregistryactioncallback / Actioncallback
- Glanceappwidgetsize / Bundle
- Warpglanceclickprepare / Context
- Ios / Warpswiftuiview
- Android / Fromappwidgetid
- Android / Ios
- Warpwidgetkitmapping / Fieldsinstanceid
- Mainactivity / Bundle
- Android / Kmpdatastore
- Ios / Kmpdatastore
- Android / Decodeclickparameters
- Warpwidgettheme / Dark
- Gradlew / Entry
- Gradlew / Entry
- Package / Packagedescription
- Kmpdatastore / Kmpdatastore
- Android / A
- Size / Size
- Width / Width
- Ios / A
- Jvm / A
- Warpparsedvisibility / Gone
- Contentscale / Warpglancecontentscale
- Widgetmode / Counter
- Widgetupdater / Widgetupdater
- Warptextstyle / Color
- Ios / Decodeclickparameters
- Android / Warpswiftuiview
- Warpswiftuiview / Warpswiftuiview

## God Nodes (most connected - your core abstractions)
1. `WarpModifier` - 86 edges
2. `WarpNode` - 47 edges
3. `WarpWidgetHostApi` - 45 edges
4. `WarpWidgetId` - 40 edges
5. `Dp` - 33 edges
6. `WarpColor` - 31 edges
7. `WidgetEnvironment` - 29 edges
8. `WarpClickHandler` - 27 edges
9. `WarpTextStyle` - 26 edges
10. `WarpParsedNode` - 26 edges

## Surprising Connections (you probably didn't know these)
- `.body` --calls--> `WarpSwiftUIRootView`  [INFERRED]
  examples/todo-widget/iosApp/TodoWidget/TodoWidget.swift → warpWidgetKit/Sources/warpWidgetKit/WarpSwiftUIRenderer.swift
- `.body` --calls--> `WarpSwiftUIRootView`  [INFERRED]
  iosApp/CounterWidget/CounterWidget.swift → warpWidgetKit/Sources/warpWidgetKit/WarpSwiftUIRenderer.swift
- `platformRegisterClickHandlers()` --references--> `WarpClickHandler`  [EXTRACTED]
  warp-widget/src/androidMain/kotlin/com/atriidev/warp_widget/WarpWidgetHostPlatform.android.kt → warp-ui/src/commonMain/kotlin/com/atriidev/warp_ui/WarpClickHandler.kt
- `.body` --calls--> `TodoHomeWidget`  [INFERRED]
  examples/todo-widget/iosApp/TodoWidget/TodoWidgetBundle.swift → examples/todo-widget/iosApp/TodoWidget/TodoWidget.swift
- `TodoWidgetClickIntent` --implements--> `WarpClickAppIntent`  [EXTRACTED]
  examples/todo-widget/iosApp/TodoWidget/TodoWidgetClickIntent.swift → warpWidgetKit/Sources/warpWidgetKit/WarpSwiftUIRenderer.swift

## Import Cycles
- None detected.

## Communities (117 total, 14 thin omitted)

### Community 0 - "Appwidgetmanager / Contentprovider"
Cohesion: 0.06
Nodes (28): AppWidgetManager, ContentProvider, ContentValues, Cursor, TodoWarpGlanceWidget, TodoWarpGlanceWidgetReceiver, GlanceAppWidgetReceiver, GlanceStateDefinition (+20 more)

### Community 1 - "Jsonobject / Kserializer"
Cohesion: 0.06
Nodes (34): JsonObject, KSerializer, actionClick(), actionIdAs(), asClickAction(), ClickAction, clickActionIdOrNull(), decodeActionId() (+26 more)

### Community 2 - "App / Mainactivity"
Cohesion: 0.06
Nodes (30): App, AppAndroidPreview(), Bundle, ComponentActivity, MainActivity, ContentView, .body, ContentView_Previews (+22 more)

### Community 3 - "Todowarpwidget / Emptytodobody"
Cohesion: 0.17
Nodes (34): EmptyTodoBody(), TodoAssets, TodoBody(), TodoItem, TodoRow(), TodoWarpWidget, TodoWidgetContent(), TodoWidgetState (+26 more)

### Community 4 - "Todowidget / Cgfloat"
Cohesion: 0.09
Nodes (29): CGFloat, Context, Date, String, Timeline, Void, WidgetConfiguration, TodoHomeWidget (+21 more)

### Community 5 - "Preferences / Glanceinternalstate"
Cohesion: 0.09
Nodes (21): Preferences, GlanceInternalState, Bundle, Configuration, Context, GlanceId, applyWarpPreferences(), toWarpPreferences() (+13 more)

### Community 6 - "Buttoncolors / Colorprovider"
Cohesion: 0.10
Nodes (27): ButtonColors, ColorProvider, FontWeight, TextAlign, TextStyle, WarpFontWeight, Bold, Medium (+19 more)

### Community 7 - "Edgeinsets / Float"
Cohesion: 0.11
Nodes (31): EdgeInsets, Float, Font, Int, VerticalAlignment, Alignment, CGFloat, Color (+23 more)

### Community 8 - "Warpwidget / Renderwarpwidget"
Cohesion: 0.10
Nodes (16): renderWarpWidget(), toWarpNode(), S, WarpComposition, WarpBox, WarpColumn, WarpNode, WarpSwiftUIView (+8 more)

### Community 9 - "Visibility / Warpalphaelement"
Cohesion: 0.09
Nodes (18): Visibility, WarpAlphaElement, WarpBackgroundElement, WarpClickableElement, WarpFillMaxHeightElement, WarpFillMaxSizeElement, WarpFillMaxWidthElement, WarpModifierElement (+10 more)

### Community 10 - "Warpcomposescope / Currentcontainer"
Cohesion: 0.16
Nodes (24): currentContainer(), WarpAction, WarpBoxComposable(), WarpButtonComposable(), WarpButtonContainerComposable(), WarpColumnComposable(), WarpContainer(), WarpDividerComposable() (+16 more)

### Community 11 - "Warpactionid / Warpmodifier"
Cohesion: 0.14
Nodes (14): WarpActionId, alpha(), background(), clickable(), fillMaxHeight(), fillMaxSize(), fillMaxWidth(), A (+6 more)

### Community 12 - "Equatable / Sendable"
Cohesion: 0.15
Nodes (23): Equatable, Sendable, String, Family, systemExtraLarge, systemLarge, systemMedium, systemSmall (+15 more)

### Community 13 - "Android / Currentwidgetplatform"
Cohesion: 0.13
Nodes (20): currentWidgetPlatform(), getAppInfo(), Context, PlatformContext, Android, AppInfo, currentWidgetPlatform(), Ios (+12 more)

### Community 14 - "Broadcastframeclock / Recomposer"
Cohesion: 0.15
Nodes (13): BroadcastFrameClock, Recomposer, composeWarp(), ComposeWarpInternals, composeWarpToJson(), S, WarpCompositionRoot, WarpRootContent() (+5 more)

### Community 15 - "Warpnodeholders / Warpboxholder"
Cohesion: 0.14
Nodes (18): WarpBoxHolder, WarpButtonHolder, WarpColumnHolder, WarpContainerHolder, WarpContainerNodeHolder, WarpDividerHolder, WarpImageHolder, WarpLazyColumnHolder (+10 more)

### Community 16 - "Comparable / Warpborderelement"
Cohesion: 0.13
Nodes (9): Comparable, WarpBorderElement, WarpCornerRadiusElement, WarpHeightElement, border(), cornerRadius(), height(), Dp (+1 more)

### Community 17 - "Iconbutton / Warpimage"
Cohesion: 0.12
Nodes (12): IconButton(), WarpImage(), Id, System, WarpAsset, WarpAssetId, WarpContentScale, Crop (+4 more)

### Community 18 - "Appintents / Todowidgetbundle"
Cohesion: 0.13
Nodes (15): AppIntents, .body, composeWidgetJson(), composeWidgetPlaceholderJson(), Bool, CGSize, ColorScheme, String (+7 more)

### Community 19 - "Warplazycolumncomposable / Warplazyrowcomposable"
Cohesion: 0.15
Nodes (14): WarpLazyColumnComposable(), WarpLazyRowComposable(), WarpLazyColumn(), WarpLazyRow(), WarpHorizontalAlignment, Center, End, Start (+6 more)

### Community 20 - "Warpglanceclickprepare / Setwarpglanceclickpreparehandler"
Cohesion: 0.20
Nodes (7): setWarpGlanceClickPrepareHandler(), Entry, Context, GlanceAppWidget, GlanceId, PlatformContext, WarpWidgetAndroidRegistry

### Community 21 - "Appentity / Entityquery"
Cohesion: 0.16
Nodes (12): AppEntity, EntityQuery, DisplayRepresentation, String, TodoInstanceEntity, .displayRepresentation, TodoInstanceQuery, CounterInstanceEntity (+4 more)

### Community 22 - "Warplazycolumn / Warplazyrow"
Cohesion: 0.32
Nodes (19): WarpLazyColumn, WarpLazyRow, Alignment, GlanceModifier, RenderButton(), RenderColumn(), RenderDivider(), RenderImage() (+11 more)

### Community 23 - "Android / Context"
Cohesion: 0.38
Nodes (5): Context, GlanceAppWidget, GlanceId, PlatformContext, WarpWidgetStateStore

### Community 24 - "Root / Any"
Cohesion: 0.33
Nodes (4): Root, Any, String, WarpNodeParser

### Community 25 - "Appintent / Content"
Cohesion: 0.15
Nodes (11): AppIntent, Content, I, Label, View, mergeWarpWidgetId(), open(), AnyView (+3 more)

### Community 26 - "Addsample / Clear"
Cohesion: 0.16
Nodes (12): AddSample, Clear, TodoActions, TodoClickHandler, Toggle, A, WarpClickHandler, registerWarpClicks() (+4 more)

### Community 27 - "Uiview / Bool"
Cohesion: 0.16
Nodes (12): UIView, Bool, WarpClickModifier, WarpNodeView, .body, WarpSwiftUIRootView, .body, AnyView (+4 more)

### Community 28 - "Tojson / Counteractions"
Cohesion: 0.12
Nodes (10): toJson(), CounterActions, Decrement, Increment, Reset, ClickActionTest, ParamCounterActions, SetStep (+2 more)

### Community 29 - "Warpwidget / Clickhandlers"
Cohesion: 0.29
Nodes (7): WarpWidgetHost, WarpWidgetHostApi, WarpWidgetSession, WarpWidgetSnapshot, platformDispatchClick(), platformInstallPrepareHandler(), platformRegisterClickHandlers()

### Community 30 - "Counterwidgetstate / Platformcontext"
Cohesion: 0.27
Nodes (11): PlatformContext, readCounterWidgetState(), updateAllCounterWidgetInstances(), PlatformContext, S, listWarpWidgetIds(), readWarpWidgetState(), reloadWarpWidget() (+3 more)

### Community 31 - "Anyhashable / Environment"
Cohesion: 0.20
Nodes (8): AnyHashable, Environment, NSDictionary, Session, Any, Bool, String, WarpWidgetKitShared

### Community 32 - "Foundation / Nsobject"
Cohesion: 0.17
Nodes (7): Foundation, NSObject, WarpBridgeAnchor, WarpBridgeAnchor, String, Void, WarpClickBridge

### Community 33 - "Widgetenvironment / Warplayoutdirection"
Cohesion: 0.15
Nodes (12): WarpLayoutDirection, LTR, RTL, WarpWidgetPadding, WarpWidgetRenderingMode, ACCENTED, FULL_COLOR, VIBRANT (+4 more)

### Community 34 - "Warpadaptive / Adaptivesize"
Cohesion: 0.22
Nodes (15): adaptiveSize(), adaptiveSizeFrom(), adaptiveValue(), isLargeAdaptive(), isMediumAdaptive(), isSmallAdaptive(), T, rememberWarpAdaptiveSize() (+7 more)

### Community 35 - "Warpwidgetclickid / Embedwarpwidgetidinrootjson"
Cohesion: 0.22
Nodes (9): embedWarpWidgetIdInRootJson(), extractWarpWidgetIdFromParametersJson(), WarpWidgetClickScope, android(), ios(), ofKind(), resolveSessionWidgetId(), stateScopeOrShared() (+1 more)

### Community 36 - "Warplogger / Warplogger"
Cohesion: 0.15
Nodes (8): WarpLogger, WarpLoggerLevel, Debug, Error, Info, Off, Verbose, Warn

### Community 37 - "Warpwidgetandroidreload / Bundle"
Cohesion: 0.30
Nodes (7): Bundle, Configuration, Context, Intent, WarpWidgetAndroidReload, BroadcastReceiver, ComponentCallbacks

### Community 38 - "Viewmodifier / Warpimagescalemodifier"
Cohesion: 0.19
Nodes (12): ViewModifier, WarpImageScaleModifier, WarpImageView, .body, WarpParsedAsset, id, system, uri (+4 more)

### Community 39 - "Ios / Nsuserdefaults"
Cohesion: 0.36
Nodes (3): NSUserDefaults, PlatformContext, WarpWidgetStateStore

### Community 40 - "Warpwidgetkitenv / Colorscheme"
Cohesion: 0.17
Nodes (7): ColorScheme, TimelineProvider, WidgetFamily, WidgetRenderingMode, WarpWidgetKitEnv.Family, WarpWidgetKitEnv.RenderingMode, WarpWidgetKitEnv.Theme

### Community 41 - "Bool / Intentresult"
Cohesion: 0.20
Nodes (7): Bool, IntentResult, LocalizedStringResource, String, TodoWidgetClickIntent, String, WarpWidgetBridge

### Community 42 - "Warpcontentalignment / Warpcontentalignment"
Cohesion: 0.17
Nodes (10): WarpContentAlignment, BottomCenter, BottomEnd, BottomStart, Center, CenterEnd, CenterStart, TopCenter (+2 more)

### Community 43 - "Warpnodekind / Box"
Cohesion: 0.17
Nodes (12): WarpNodeKind, box, button, column, divider, image, lazyColumn, lazyRow (+4 more)

### Community 44 - "S / Warpwidget"
Cohesion: 0.33
Nodes (5): S, WarpWidget, T, PlatformContext, rememberPlatformContext()

### Community 45 - "Widget / Todowidgetbundle"
Cohesion: 0.24
Nodes (6): Widget, TodoWidgetBundle, .body, CounterWidgetBundle, Widget, WidgetBundle

### Community 46 - "Todowidgetview / Composewidgetjson"
Cohesion: 0.24
Nodes (9): composeWidgetJson(), composeWidgetPlaceholderJson(), Bool, CGSize, ColorScheme, String, TimelineProviderContext, WidgetFamily (+1 more)

### Community 47 - "Modifier / App"
Cohesion: 0.33
Nodes (7): Modifier, App(), PlatformContext, PlatformContext, MainViewController(), WarpUiKitPreview(), WarpWidgetPreferences

### Community 48 - "Android / Widgetupdater"
Cohesion: 0.20
Nodes (5): WidgetUpdater, WidgetUpdater, PlatformContext, T, rememberPlatformContext()

### Community 50 - "Android / T"
Cohesion: 0.38
Nodes (8): T, PlatformContext, rememberPlatformContext(), glanceWidgetEnvironment(), Context, DpSize, rememberGlanceWidgetSession(), WarpWidgetConfiguration

### Community 51 - "Towarpwidgetfamily / Warpwidgetfamily"
Cohesion: 0.22
Nodes (9): toWarpWidgetFamily(), WarpWidgetFamily, SYSTEM_EXTRA_LARGE, SYSTEM_LARGE, SYSTEM_MEDIUM, SYSTEM_SMALL, iosSession(), toKitFamilyString() (+1 more)

### Community 52 - "Kmplibrarypublishplugin / Kmplibrarypublishplugin"
Cohesion: 0.46
Nodes (4): KmpLibraryPublishPlugin, mavenArtifactId(), Plugin, Project

### Community 53 - "Padding / Padding"
Cohesion: 0.29
Nodes (3): padding(), WarpPadding, WarpPaddingElement

### Community 54 - "Visibility / Resolvedvisibility"
Cohesion: 0.29
Nodes (5): visibility(), WarpVisibility, Gone, Invisible, Visible

### Community 55 - "Isreadytoprint / Counterwidget"
Cohesion: 0.32
Nodes (4): CounterWidget, sampleCounterWidgetJson(), State, ComposeWarpJvmTest

### Community 56 - "Horizontalalignment / Warpparsedhorizontalalignment"
Cohesion: 0.29
Nodes (7): HorizontalAlignment, WarpParsedHorizontalAlignment, center, end, .frameAlignment, .stackAlignment, start

### Community 57 - "Counterwidgetclickintent / Init"
Cohesion: 0.33
Nodes (5): CounterWidgetClickIntent, Bool, IntentResult, LocalizedStringResource, String

### Community 58 - "Counteractions / Counterwarpclickhandler"
Cohesion: 0.33
Nodes (6): CounterActions, CounterWarpClickHandler, Decrement, Increment, Reset, SwitchMode

### Community 59 - "Warpglanceactionkeys / Actionparameters"
Cohesion: 0.52
Nodes (3): ActionParameters, WarpActionParameters, WarpGlanceActionKeys

### Community 60 - "Warpregistryactioncallback / Actioncallback"
Cohesion: 0.48
Nodes (5): ActionCallback, ActionParameters, Context, GlanceId, WarpRegistryActionCallback

### Community 61 - "Glanceappwidgetsize / Bundle"
Cohesion: 0.52
Nodes (6): Bundle, DpSize, resolveGlanceWidgetSize(), resolveGlanceWidgetSizeOrNull(), toWarpWidgetSize(), WarpWidgetSize

### Community 62 - "Warpglanceclickprepare / Context"
Cohesion: 0.60
Nodes (3): Context, GlanceId, WarpGlanceClickPrepare

### Community 63 - "Ios / Warpswiftuiview"
Cohesion: 0.40
Nodes (3): WarpSwiftUIView, previewViewController(), warpWidgetPreviewViewController()

### Community 64 - "Android / Fromappwidgetid"
Cohesion: 0.53
Nodes (4): fromAppWidgetId(), fromGlanceId(), Context, GlanceId

### Community 65 - "Android / Ios"
Cohesion: 0.33
Nodes (5): Android, Ios, WidgetPlatformEnvironment, PlatformContext, makeWidgetEnvironment()

### Community 67 - "Mainactivity / Bundle"
Cohesion: 0.60
Nodes (3): Bundle, ComponentActivity, MainActivity

### Community 70 - "Android / Decodeclickparameters"
Cohesion: 0.50
Nodes (3): decodeClickParameters(), platformDispatchClick(), platformRegisterClickHandlers()

### Community 71 - "Warpwidgettheme / Dark"
Cohesion: 0.40
Nodes (5): WarpWidgetTheme, DARK, LIGHT, UNSPECIFIED, toWarpTheme()

### Community 72 - "Gradlew / Entry"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 73 - "Gradlew / Entry"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 76 - "Android / A"
Cohesion: 0.67
Nodes (3): A, KClass, platformEnumConstants()

### Community 79 - "Ios / A"
Cohesion: 0.67
Nodes (3): A, KClass, platformEnumConstants()

### Community 80 - "Jvm / A"
Cohesion: 0.67
Nodes (3): A, KClass, platformEnumConstants()

### Community 81 - "Warpparsedvisibility / Gone"
Cohesion: 0.50
Nodes (4): WarpParsedVisibility, gone, invisible, visible

### Community 83 - "Widgetmode / Counter"
Cohesion: 0.67
Nodes (3): WidgetMode, Counter, Todo

## Knowledge Gaps
- **130 isolated node(s):** `TodoAssets`, `Clear`, `AddSample`, `Increment`, `Decrement` (+125 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **14 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `WarpWidgetSession` connect `Warpwidget / Clickhandlers` to `Warpwidgetkitmapping / Fieldsinstanceid`, `Todowarpwidget / Emptytodobody`, `Modifier / App`, `Android / T`, `Towarpwidgetfamily / Warpwidgetfamily`, `Warpglanceclickprepare / Setwarpglanceclickpreparehandler`, `Addsample / Clear`, `Counterwidgetstate / Platformcontext`?**
  _High betweenness centrality (0.107) - this node is a cross-community bridge._
- **Why does `WarpModifier` connect `Warpactionid / Warpmodifier` to `Todowarpwidget / Emptytodobody`, `Visibility / Warpalphaelement`, `Warpcomposescope / Currentcontainer`, `Warpcontentalignment / Warpcontentalignment`, `Size / Size`, `Width / Width`, `Warpnodeholders / Warpboxholder`, `Comparable / Warpborderelement`, `Iconbutton / Warpimage`, `Warplazycolumncomposable / Warplazyrowcomposable`, `Padding / Padding`, `Visibility / Resolvedvisibility`?**
  _High betweenness centrality (0.104) - this node is a cross-community bridge._
- **Why does `WidgetEnvironment` connect `Todowarpwidget / Emptytodobody` to `Android / Ios`, `Widgetenvironment / Warplayoutdirection`, `Warpadaptive / Adaptivesize`, `Warpwidgetkitmapping / Fieldsinstanceid`, `S / Warpwidget`, `Android / Currentwidgetplatform`, `Modifier / App`, `Android / T`, `Towarpwidgetfamily / Warpwidgetfamily`, `Warpwidget / Clickhandlers`?**
  _High betweenness centrality (0.098) - this node is a cross-community bridge._
- **What connects `TodoAssets`, `Clear`, `AddSample` to the rest of the system?**
  _130 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Appwidgetmanager / Contentprovider` be split into smaller, more focused modules?**
  _Cohesion score 0.059395801331285206 - nodes in this community are weakly interconnected._
- **Should `Jsonobject / Kserializer` be split into smaller, more focused modules?**
  _Cohesion score 0.062040816326530614 - nodes in this community are weakly interconnected._
- **Should `App / Mainactivity` be split into smaller, more focused modules?**
  _Cohesion score 0.06448202959830866 - nodes in this community are weakly interconnected._
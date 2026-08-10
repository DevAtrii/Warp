package com.atriidev.warp_widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.atriidev.warp_runtime.compose.composeWarp
import com.atriidev.warp_runtime.compose.toJson
import com.atriidev.warp_runtime.log.WarpLogger
import com.atriidev.warp_runtime.nodes.WarpNode
import com.atriidev.warp_ui.WarpActionHandler
import com.atriidev.warp_widget.WarpWidgetHost.compose
import com.atriidev.warp_widget.api.PlatformContext
import com.atriidev.warp_widget.api.WidgetEnvironment
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * One render / click pass from a platform host.
 *
 * Hosts **must** supply [environment] and [context] (never invented by [WarpWidgetHost]):
 *
 * - **Android:** [rememberGlanceWidgetSession] / [glanceWidgetEnvironment]
 * - **iOS:** [WarpWidgetHost.iosSession] with Kit `asKitFields`
 */
@Stable
data class WarpWidgetSession(
    val context: PlatformContext,
    val environment: WidgetEnvironment,
    val preferences: WarpWidgetPreferences? = null,
    /** This instance (or [WarpWidgetId.ofKind] when [WarpWidget.stateScope] is [WarpWidgetStateScope.Shared]). */
    val widgetId: WarpWidgetId,
)

/**
 * Type-erased widget surface for platform hosts.
 *
 * Swift cannot accept Kotlin `WarpWidget<*>` (`WarpWidget<AnyObject>` is invariant).
 * Host APIs take this interface; app widgets still extend [WarpWidget].
 */
interface WarpWidgetHostApi {
    val id: String
    val iosGroupId: String

    fun clickHandlers(session: WarpWidgetSession): List<WarpActionHandler<*>>

    @Composable
    fun ComposeContent(session: WarpWidgetSession, preferences: WarpWidgetPreferences)

    /**
     * Called when the widget is refreshed by system timeline / update interval.
     *
     * - **Android:** Triggered when system update interval (`android:updatePeriodMillis`) fires.
     * - **iOS:** Triggered when WidgetKit requests a timeline update (`getTimeline`).
     *
     * @param previous [Duration] timestamp of the previous update pass (or [Duration.ZERO] on initial run).
     * @param current [Duration] timestamp of the current update pass.
     * @param session Active [WarpWidgetSession] for the widget being updated.
     */
    fun onUpdate(
        previous: Duration,
        current: Duration,
        session: WarpWidgetSession,
    ) {}
}

/**
 * Shared widget definition for Glance and WidgetKit — typed serializable [S] state.
 *
 * State is JSON-encoded and stored under prefs key = [id].
 *
 * ```
 * @Serializable
 * data class CounterState(val count: Int = 0)
 *
 * object CounterWarpWidget : WarpWidget<CounterState>(CounterState.serializer()) {
 *     override val id = "CounterWidget"
 *     override val iosGroupId = APP_GROUP_ID
 *     override suspend fun defaultState() = CounterState()
 *
 *     @Composable
 *     override fun Content(env: WidgetEnvironment, state: CounterState) {
 *         WarpText("${state.count}")
 *     }
 * }
 *
 * // update (Glance-style — always pass WarpWidgetId):
 * updateWarpWidgetState(session, CounterWarpWidget) { it.copy(count = it.count + 1) }
 * ```
 */
@Stable
abstract class WarpWidget<S : Any>(
    private val stateSerializer: KSerializer<S>,
) : WarpWidgetHostApi {
    /**
     * Stable widget kind id.
     *
     * Prefs JSON key, timeline kind, Glance registry, WidgetKit `kind`.
     */
    abstract override val id: String

    /**
     * iOS App Group suite id (`group.*`).
     * Ignored on Android.
     */
    abstract override val iosGroupId: String

    /**
     * Used when prefs are empty or decode fails.
     *
     * This will be used for the first render of the widget.
     */
    abstract suspend fun defaultState(): S

    /**
     * Shared vs per-instance state.
     *
     * - [WarpWidgetStateScope.Shared] — all home-screen instances mirror the same JSON (default).
     * - [WarpWidgetStateScope.Instance] — each [WarpWidgetId] has its own prefs blob.
     */
    open val stateScope: WarpWidgetStateScope get() = WarpWidgetStateScope.Shared

    /**
     * Declarative UI for [session] + decoded [state].
     */
    @Composable
    abstract fun Content(session: WarpWidgetSession, state: S)

    /**
     * Click handlers for wire `actionId`s used in [Content].
     *
     * Prefer [updateWarpWidgetState] with a `(S) -> S` transform.
     */
    override fun clickHandlers(session: WarpWidgetSession): List<WarpActionHandler<*>> = emptyList()

    /**
     * Optional override called when the widget is refreshed by system timeline / update interval.
     *
     * Override this method in your [WarpWidget] subclass to perform periodic data fetching or
     * state updates when the host system refreshes the widget.
     *
     * @param previous [Duration] timestamp of the previous update pass (or [Duration.ZERO] on initial run).
     * @param current [Duration] timestamp of the current update pass.
     * @param session Active [WarpWidgetSession] for the widget being updated.
     */
    open override fun onUpdate(
        previous: Duration,
        current: Duration,
        session: WarpWidgetSession,
    ) {}


    /** Decode [S] from prefs (key = [id]); falls back to [defaultState]. */
    suspend fun decodeState(preferences: WarpWidgetPreferences): S {
        val json = preferences.values[id] ?: return defaultState()
        return runCatching { stateJson.decodeFromString(stateSerializer, json) }
            .getOrElse { defaultState() }
    }

    /** Encode [state] for prefs storage. */
    fun encodeState(state: S): String =
        stateJson.encodeToString(stateSerializer, state)

    /**
     * Host entry: resolve [state] from [preferences] and call [Content].
     */
    @Composable
    final override fun ComposeContent(
        session: WarpWidgetSession,
        preferences: WarpWidgetPreferences,
    ) {
        val state = runBlocking { decodeState(preferences) }
        Content(session, state)
    }

    companion object {
        internal val stateJson = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }
}

/**
 * Serializable payload for timeline entries or debugging.
 */
@Serializable
data class WarpWidgetSnapshot(
    val widgetId: String,
    /** Pretty-printed [WarpNode] JSON for SwiftUI / debug. */
    val nodeJson: String,
    val environment: WidgetEnvironment,
    val preferences: WarpWidgetPreferences = WarpWidgetPreferences(),
)

/**
 * Platform consumption surface for a [WarpWidgetHostApi].
 */
object WarpWidgetHost {
    private var lastWidget: WarpWidgetHostApi? = null
    private var lastSession: WarpWidgetSession? = null

    /**
     * Resolve prefs for a compose pass: [WarpWidgetSession.preferences] if set, else
     * [WarpWidgetStateStore.read].
     */
    fun preferences(widget: WarpWidgetHostApi, session: WarpWidgetSession): WarpWidgetPreferences =
        session.preferences
            ?: runBlocking {
                WarpWidgetStateStore.read(
                    context = session.context,
                    widget = widget,
                    id = session.widgetId,
                )
            }

    /**
     * Run [WarpWidgetHostApi.ComposeContent] under [ProvideWarpWidgetPreferences] → [WarpNode].
     */
    fun compose(widget: WarpWidgetHostApi, session: WarpWidgetSession): WarpNode {
        WarpLogger.d("WarpWidgetHost", "compose: starting render pass for widget ${widget.id}")
        val prefs = preferences(widget, session)
        return composeWarp {
            ProvideWarpWidgetPreferences(prefs) {
                widget.ComposeContent(session, prefs)
            }
        }
    }

    /**
     * [compose] then serialize to JSON for WidgetKit.
     *
     * Embeds [session.widgetId] at the JSON root (`__warpWidgetId`) so warpWidgetKit
     * can attach it to AppIntent parameters — no per-click Swift plumbing in the app.
     */
    fun composeJson(widget: WarpWidgetHostApi, session: WarpWidgetSession): String =
        embedWarpWidgetIdInRootJson(compose(widget, session).toJson(), session.widgetId)

    fun handlers(
        widget: WarpWidgetHostApi,
        session: WarpWidgetSession,
    ): List<WarpActionHandler<*>> = widget.clickHandlers(session)

    fun prepare(widget: WarpWidgetHostApi, session: WarpWidgetSession) {
        WarpLogger.d("WarpWidgetHost", "prepare: registering handlers for widget ${widget.id}")
        lastWidget = widget
        lastSession = session
        platformRegisterClickHandlers(handlers(widget, session))
        platformInstallPrepareHandler { reprepare() }
    }

    fun reprepare() {
        val widget = lastWidget ?: return
        val session = lastSession ?: return
        platformRegisterClickHandlers(handlers(widget, session))
    }

    fun dispatchClick(
        widget: WarpWidgetHostApi,
        session: WarpWidgetSession,
        actionId: String,
        parametersJson: String,
    ) {
        val resolvedId = extractWarpWidgetIdFromParametersJson(parametersJson) ?: session.widgetId
        val resolved = if (resolvedId == session.widgetId) {
            session
        } else {
            session.copy(widgetId = resolvedId)
        }
        WarpLogger.d(
            "WarpWidgetHost",
            "WARP_CLICK: dispatch kind=${widget.id} actionId=$actionId " +
                    "widgetId=$resolvedId params=$parametersJson"
        )
        WarpWidgetClickScope.withWidgetId(resolvedId) {
            prepare(widget, resolved)
            platformDispatchClick(actionId, parametersJson)
        }
    }

    fun dispatchClick(actionId: String, parametersJson: String) {
        val widget = lastWidget
        val session = lastSession
        if (widget != null && session != null) {
            dispatchClick(widget, session, actionId, parametersJson)
        } else {
            val fromParams = extractWarpWidgetIdFromParametersJson(parametersJson)
            if (fromParams != null) {
                WarpWidgetClickScope.withWidgetId(fromParams) {
                    reprepare()
                    platformDispatchClick(actionId, parametersJson)
                }
            } else {
                reprepare()
                platformDispatchClick(actionId, parametersJson)
            }
        }
    }

    fun snapshot(widget: WarpWidgetHostApi, session: WarpWidgetSession): WarpWidgetSnapshot {
        val prefs = preferences(widget, session)
        return WarpWidgetSnapshot(
            widgetId = widget.id,
            nodeJson = composeJson(widget, session),
            environment = session.environment,
            preferences = prefs,
        )
    }

    /**
     * Dispatch system timeline / periodic update event to [widget].
     *
     * Calculates `previous` timestamp from preferences and `current` timestamp from [nowMillis].
     * To prevent accidental invocations during rapid UI re-renders or state-change timeline reloads,
     * updates occurring within [minIntervalMillis] of the previous update are skipped unless [force] is true.
     *
     * @param widget Target widget definition.
     * @param session Active session.
     * @param nowMillis Epoch milliseconds of the current update pass.
     * @param minIntervalMillis Minimum elapsed milliseconds required between periodic updates (default 60,000ms / 1 min).
     * @param force Set to true to bypass interval throttling (e.g. manual ADB test broadcasts).
     */
    fun dispatchOnUpdate(
        widget: WarpWidgetHostApi,
        session: WarpWidgetSession,
        nowMillis: Long = currentTimeMillis(),
        minIntervalMillis: Long = 60_000L,
        force: Boolean = false,
    ) {
        val prefs = preferences(widget, session)
        val lastUpdateKey = WarpStateKey.long("__warp_last_update_millis")
        val previousMillis = prefs[lastUpdateKey] ?: 0L
        val currentMillis = nowMillis

        if (!force && previousMillis > 0L && (currentMillis - previousMillis) < minIntervalMillis) {
            WarpLogger.d(
                "WarpWidgetHost",
                "dispatchOnUpdate kind=${widget.id} skipped (elapsed ${currentMillis - previousMillis}ms < ${minIntervalMillis}ms threshold)",
            )
            return
        }

        val previous = previousMillis.milliseconds
        val current = currentMillis.milliseconds

        WarpLogger.d(
            "WarpWidgetHost",
            "dispatchOnUpdate kind=${widget.id} previous=$previous current=$current",
        )

        runBlocking {
            WarpWidgetStateStore.update(session.context, widget, session.widgetId) {
                set(lastUpdateKey, currentMillis)
            }
        }

        widget.onUpdate(previous, current, session)
    }
}

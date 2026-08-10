package com.atriidev.warp_runtime.nodes

import com.atriidev.warp_runtime.nodes.modifiers.WarpModifier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Type-safe URL wrapper for WARP deeplink targets.
 *
 * @property value The raw URL or deeplink URI string.
 */
@JvmInline
@Serializable
value class WarpUrl(val value: String)

/**
 * Clickable container node that opens a deeplink URI when tapped.
 *
 * JSON `"type"` value: `"link"`.
 *
 * @property deeplink Target URL or deeplink to open.
 * @property androidIntentFlags Android Intent launch flags applied when opening the deeplink.
 * @property modifier Layout styling.
 * @property children Nested nodes placed inside this link container.
 */
@Serializable
@SerialName("link")
data class WarpLinkNode(
    val deeplink: WarpUrl,
    val androidIntentFlags: List<WarpIntentFlags> = listOf(
        WarpIntentFlags.NEW_TASK,
        WarpIntentFlags.CLEAR_TOP,
        WarpIntentFlags.SINGLE_TOP
    ),
    val modifier: WarpModifier = WarpModifier(),
    val children: List<WarpNode> = emptyList(),
) : WarpNode

/**
 * Android Intent launch flags for controlling activity launch behavior when opening a deeplink.
 */
@Serializable
enum class WarpIntentFlags {
    @SerialName("new_task")
    NEW_TASK,

    @SerialName("clear_top")
    CLEAR_TOP,

    @SerialName("single_top")
    SINGLE_TOP,

    @SerialName("single_task")
    SINGLE_TASK,

    @SerialName("clear_task")
    CLEAR_TASK,

    @SerialName("no_history")
    NO_HISTORY,

    @SerialName("no_animation")
    NO_ANIMATION,

    @SerialName("reorder_to_front")
    REORDER_TO_FRONT,

    @SerialName("multiple_task")
    MULTIPLE_TASK,

    @SerialName("exclude_from_recents")
    EXCLUDE_FROM_RECENTS,

    @SerialName("forward_result")
    FORWARD_RESULT,

    @SerialName("new_document")
    NEW_DOCUMENT,

    @SerialName("no_user_action")
    NO_USER_ACTION,

    @SerialName("task_on_home")
    TASK_ON_HOME,
}

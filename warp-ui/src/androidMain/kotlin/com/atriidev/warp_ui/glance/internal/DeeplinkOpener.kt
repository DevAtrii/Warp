package com.atriidev.warp_ui.glance.internal

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.atriidev.warp_runtime.nodes.WarpIntentFlags

/**
 * Internal helper for constructing and launching deeplink [Intent]s on Android Glance widgets.
 */
internal object DeeplinkOpener {
    /**
     * Creates a deeplink [Intent] and opens it using the provided [context].
     *
     * @param context Host application or Glance context.
     * @param deeplink The target URL or URI string to open.
     * @param flags List of [WarpIntentFlags] to apply to the launch Intent.
     */
    fun openDeeplink(
        context: Context,
        deeplink: String,
        flags: List<WarpIntentFlags> = listOf(
            WarpIntentFlags.NEW_TASK,
            WarpIntentFlags.CLEAR_TOP,
            WarpIntentFlags.SINGLE_TOP
        )
    ) {
        val intent = createIntent(context, deeplink, flags)
        context.startActivity(intent)
    }

    /**
     * Creates an [Intent.ACTION_VIEW] intent configured with [flags]
     * and package name targeting the host application.
     *
     * @param context Host application context used to set the package name.
     * @param deeplink The target URL or URI string to parse.
     * @param flags List of [WarpIntentFlags] converted into Android Intent flag masks.
     * @return Configured [Intent] suitable for launching via [Context.startActivity] or Glance `actionStartActivity`.
     */
    fun createIntent(
        context: Context,
        deeplink: String,
        flags: List<WarpIntentFlags> = listOf(
            WarpIntentFlags.NEW_TASK,
            WarpIntentFlags.CLEAR_TOP,
            WarpIntentFlags.SINGLE_TOP
        )
    ): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(deeplink)).apply {
            var flagMask = 0
            for (flag in flags) {
                flagMask = flagMask or flag.toAndroidFlag()
            }
            if (flagMask != 0) {
                addFlags(flagMask)
            }
            setPackage(context.packageName)
        }
    }

    /**
     * Maps each [WarpIntentFlags] enum constant to its corresponding Android [Intent] flag integer.
     */
    private fun WarpIntentFlags.toAndroidFlag(): Int = when (this) {
        WarpIntentFlags.NEW_TASK -> Intent.FLAG_ACTIVITY_NEW_TASK
        WarpIntentFlags.CLEAR_TOP -> Intent.FLAG_ACTIVITY_CLEAR_TOP
        WarpIntentFlags.SINGLE_TOP -> Intent.FLAG_ACTIVITY_SINGLE_TOP
        WarpIntentFlags.SINGLE_TASK -> Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        WarpIntentFlags.CLEAR_TASK -> Intent.FLAG_ACTIVITY_CLEAR_TASK
        WarpIntentFlags.NO_HISTORY -> Intent.FLAG_ACTIVITY_NO_HISTORY
        WarpIntentFlags.NO_ANIMATION -> Intent.FLAG_ACTIVITY_NO_ANIMATION
        WarpIntentFlags.REORDER_TO_FRONT -> Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        WarpIntentFlags.MULTIPLE_TASK -> Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        WarpIntentFlags.EXCLUDE_FROM_RECENTS -> Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        WarpIntentFlags.FORWARD_RESULT -> Intent.FLAG_ACTIVITY_FORWARD_RESULT
        WarpIntentFlags.NEW_DOCUMENT -> Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        WarpIntentFlags.NO_USER_ACTION -> Intent.FLAG_ACTIVITY_NO_USER_ACTION
        WarpIntentFlags.TASK_ON_HOME -> Intent.FLAG_ACTIVITY_TASK_ON_HOME
    }
}

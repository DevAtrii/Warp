package com.atriidev.warp_ui.glance.internal

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.ProgressIndicatorDefaults
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ColumnScope
import androidx.glance.layout.Row
import androidx.glance.layout.RowScope
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import com.atriidev.warp_runtime.nodes.WarpBoxNode
import com.atriidev.warp_runtime.nodes.WarpButtonNode
import com.atriidev.warp_runtime.nodes.WarpColumnNode
import com.atriidev.warp_runtime.nodes.WarpDividerNode
import com.atriidev.warp_runtime.nodes.WarpImageNode
import com.atriidev.warp_runtime.nodes.WarpLazyColumnNode
import com.atriidev.warp_runtime.nodes.WarpLazyRowNode
import com.atriidev.warp_runtime.nodes.WarpLinkNode
import com.atriidev.warp_runtime.nodes.WarpNode
import com.atriidev.warp_runtime.nodes.WarpProgressIndicatorNode
import com.atriidev.warp_runtime.nodes.WarpRowNode
import com.atriidev.warp_runtime.nodes.WarpSpacerNode
import com.atriidev.warp_runtime.nodes.WarpTextNode
import com.atriidev.warp_runtime.nodes.actions.ClickAction
import com.atriidev.warp_runtime.nodes.style.WarpProgressIndicatorStyle
import com.atriidev.warp_runtime.nodes.style.WarpTextAlign
import com.atriidev.warp_ui.glance.WarpAndroidAssets

/** Glance / RemoteViews: max direct children per Column or Row. */
private const val GlanceMaxChildrenPerContainer = 10

@PublishedApi
@Composable
internal fun RenderWarpNode(
    node: WarpNode,
    clickAction: (ClickAction) -> Action,
) {
    when (node) {
        is WarpColumnNode -> RenderColumn(node, clickAction)
        is WarpLazyColumnNode -> RenderLazyColumn(node, clickAction)
        is WarpRowNode -> RenderRow(node, clickAction)
        is WarpLazyRowNode -> RenderLazyRow(node, clickAction)

        is WarpBoxNode -> Box(
            modifier = node.modifier.toGlanceModifier(clickAction),
            contentAlignment = node.contentAlignment.toGlance(),
        ) {
            node.children.forEach { child ->
                RenderWarpNode(child, clickAction)
            }
        }

        is WarpLinkNode -> RenderLink(node, clickAction)

        is WarpTextNode -> RenderText(node, clickAction)
        is WarpButtonNode -> RenderButton(node, clickAction)
        is WarpSpacerNode -> Spacer(modifier = node.modifier.toGlanceModifier(clickAction))
        is WarpDividerNode -> RenderDivider(node, clickAction)
        is WarpProgressIndicatorNode -> RenderProgressIndicator(node, clickAction)
        is WarpImageNode -> RenderImage(node, clickAction)
    }
}

@Composable
private fun RenderColumn(
    node: WarpColumnNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val modifier = node.modifier.toGlanceModifier(clickAction).then(extraModifier)
    val vAlign = node.verticalAlignment.toGlance()
    val hAlign = node.horizontalAlignment.toGlance()
    val chunks = node.children.chunked(GlanceMaxChildrenPerContainer)
    Column(
        modifier = modifier,
        verticalAlignment = vAlign,
        horizontalAlignment = hAlign,
    ) {
        if (chunks.size <= 1) {
            node.children.forEach { child ->
                RenderScopedChild(child, clickAction)
            }
        } else {
            // Nest chunks so each Glance Column stays ≤ 10 children.
            chunks.forEach { chunk ->
                Column(
                    verticalAlignment = vAlign,
                    horizontalAlignment = hAlign,
                ) {
                    chunk.forEach { child ->
                        RenderScopedChild(child, clickAction)
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderRow(
    node: WarpRowNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val modifier = node.modifier.toGlanceModifier(clickAction).then(extraModifier)
    val hAlign = node.horizontalAlignment.toGlance()
    val vAlign = node.verticalAlignment.toGlance()
    val chunks = node.children.chunked(GlanceMaxChildrenPerContainer)
    Row(
        modifier = modifier,
        horizontalAlignment = hAlign,
        verticalAlignment = vAlign,
    ) {
        if (chunks.size <= 1) {
            node.children.forEach { child ->
                RenderScopedChild(child, clickAction)
            }
        } else {
            chunks.forEach { chunk ->
                Row(
                    horizontalAlignment = hAlign,
                    verticalAlignment = vAlign,
                ) {
                    chunk.forEach { child ->
                        RenderScopedChild(child, clickAction)
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderLazyColumn(
    node: WarpLazyColumnNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val modifier = node.modifier.toGlanceModifier(clickAction).then(extraModifier)
    val hAlign = node.horizontalAlignment.toGlance()
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = hAlign,
    ) {
        items(node.children) { child ->
            RenderWarpNode(child, clickAction)
        }
    }
}

@Composable
private fun RenderLazyRow(
    node: WarpLazyRowNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val modifier = node.modifier.toGlanceModifier(clickAction).then(extraModifier)
    val hAlign = node.horizontalAlignment.toGlance()
    val vAlign = node.verticalAlignment.toGlance()
    Row(
        modifier = modifier,
        horizontalAlignment = hAlign,
        verticalAlignment = vAlign,
    ) {
        node.children.forEach { child ->
            RenderScopedChild(child, clickAction)
        }
    }
}

@Composable
private fun RenderText(
    node: WarpTextNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val modifier = node.modifier.toGlanceModifier(clickAction).then(extraModifier)
    val style = node.style?.toGlanceTextStyle()
    if (style != null) {
        Text(
            text = node.text,
            modifier = modifier,
            style = style,
            maxLines = node.maxLines,
        )
    } else {
        Text(
            text = node.text,
            modifier = modifier,
            maxLines = node.maxLines,
        )
    }
}

@Composable
private fun RenderButton(
    node: WarpButtonNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val action = node.modifier.resolveClickAction(node.onClick)
        ?: return
    if (node.children.isNotEmpty()) {
        val baseModifier = node.modifier
            .toGlanceModifier(clickAction, applyClickable = false)
            .then(extraModifier)
        val buttonModifier = if (node.enabled) {
            baseModifier.clickable(clickAction(action))
        } else {
            baseModifier
        }

        Box(
            modifier = buttonModifier,
            contentAlignment = Alignment.Center,
        ) {
            node.children.forEach { child ->
                RenderWarpNode(child, clickAction)
            }
        }
    } else {
        Button(
            text = node.text ?: "",
            onClick = clickAction(action),
            modifier = node.modifier
                .toGlanceModifier(clickAction, applyClickable = false)
                .then(extraModifier),
            enabled = node.enabled,
            style = node.style?.toGlanceTextStyle(),
            colors = node.colors.toGlanceButtonColors(),
            maxLines = node.maxLines,
        )
    }
}

@Composable
private fun RenderDivider(
    node: WarpDividerNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val color = node.color?.toComposeColor() ?: Color.Gray
    Spacer(
        modifier = node.modifier
            .toGlanceModifier(clickAction)
            .then(extraModifier)
            .fillMaxWidth()
            .height(node.thickness.value.dp)
            .background(color),
    )
}

@SuppressLint("RestrictedApi")
@Composable
private fun RenderProgressIndicator(
    node: WarpProgressIndicatorNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val modifier = node.modifier.toGlanceModifier(clickAction).then(extraModifier)
    val color = node.color?.let { ColorProvider(it.toComposeColor()) }
        ?: ProgressIndicatorDefaults.IndicatorColorProvider
    when (node.style) {
        WarpProgressIndicatorStyle.Circular -> CircularProgressIndicator(
            modifier = modifier,
            color = color,
        )

        WarpProgressIndicatorStyle.Linear -> {
            val background = node.backgroundColor?.let { ColorProvider(it.toComposeColor()) }
                ?: ProgressIndicatorDefaults.BackgroundColorProvider
            val progress = node.progress
            if (progress != null) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = modifier,
                    color = color,
                    backgroundColor = background,
                )
            } else {
                LinearProgressIndicator(
                    modifier = modifier,
                    color = color,
                    backgroundColor = background,
                )
            }
        }
    }
}

@Composable
private fun RenderImage(
    node: WarpImageNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val modifier = node.modifier.toGlanceModifier(clickAction).then(extraModifier)
    val provider = WarpAndroidAssets.resolve(node.asset, LocalContext.current)
    if (provider == null) {
        Spacer(modifier = modifier)
        return
    }
    val tintFilter = node.tint?.let { ColorFilter.tint(ColorProvider(it.toComposeColor())) }
    Image(
        provider = provider,
        contentDescription = node.contentDescription,
        modifier = modifier,
        contentScale = node.contentScale.toGlance(),
        colorFilter = tintFilter,
    )
}

@Composable
private fun RowScope.RenderScopedChild(
    child: WarpNode,
    clickAction: (ClickAction) -> Action,
) {
    if (child is WarpTextNode && child.modifier.hasWeight()) {
        RenderWeightedText(child, clickAction, GlanceModifier.defaultWeight())
        return
    }

    val extra = if (child.warpModifier().hasWeight()) {
        GlanceModifier.defaultWeight()
    } else {
        GlanceModifier
    }
    RenderNodeWithExtra(child, clickAction, extra)
}

@Composable
private fun ColumnScope.RenderScopedChild(
    child: WarpNode,
    clickAction: (ClickAction) -> Action,
) {
    if (child is WarpTextNode && child.modifier.hasWeight()) {
        RenderWeightedText(child, clickAction, GlanceModifier.defaultWeight())
        return
    }

    val extra = if (child.warpModifier().hasWeight()) {
        GlanceModifier.defaultWeight()
    } else {
        GlanceModifier
    }
    RenderNodeWithExtra(child, clickAction, extra)
}

@Composable
private fun RenderWeightedText(
    node: WarpTextNode,
    clickAction: (ClickAction) -> Action,
    weightModifier: GlanceModifier,
) {
    val textAlign = node.style?.textAlign
    if (textAlign != null) {
        Box(
            modifier = weightModifier,
            contentAlignment = textAlign.toBoxAlignment(),
        ) {
            RenderText(node, clickAction)
        }
    } else {
        RenderText(node, clickAction, weightModifier)
    }
}

private fun WarpTextAlign.toBoxAlignment(): Alignment = when (this) {
    WarpTextAlign.Center -> Alignment.Center
    WarpTextAlign.End -> Alignment.CenterEnd
    WarpTextAlign.Start -> Alignment.CenterStart
}

private fun WarpNode.warpModifier() = when (this) {
    is WarpColumnNode -> modifier
    is WarpLazyColumnNode -> modifier
    is WarpRowNode -> modifier
    is WarpLazyRowNode -> modifier
    is WarpBoxNode -> modifier
    is WarpLinkNode -> modifier
    is WarpTextNode -> modifier
    is WarpButtonNode -> modifier
    is WarpSpacerNode -> modifier
    is WarpDividerNode -> modifier
    is WarpProgressIndicatorNode -> modifier
    is WarpImageNode -> modifier
}

@Composable
private fun RenderLink(
    node: WarpLinkNode,
    clickAction: (ClickAction) -> Action,
    extraModifier: GlanceModifier = GlanceModifier,
) {
    val context = LocalContext.current
    val intent = DeeplinkOpener.createIntent(context, node.deeplink.value)
    Box(
        modifier = node.modifier
            .toGlanceModifier(clickAction)
            .then(extraModifier)
            .clickable(actionStartActivity(intent)),
    ) {
        node.children.forEach { child ->
            RenderWarpNode(child, clickAction)
        }
    }
}

@Composable
private fun RenderNodeWithExtra(
    node: WarpNode,
    clickAction: (ClickAction) -> Action,
    extra: GlanceModifier,
) {
    when (node) {
        is WarpColumnNode -> RenderColumn(node, clickAction, extra)
        is WarpLazyColumnNode -> RenderLazyColumn(node, clickAction, extra)
        is WarpRowNode -> RenderRow(node, clickAction, extra)
        is WarpLazyRowNode -> RenderLazyRow(node, clickAction, extra)

        is WarpBoxNode -> Box(
            modifier = node.modifier.toGlanceModifier(clickAction).then(extra),
            contentAlignment = node.contentAlignment.toGlance(),
        ) {
            node.children.forEach { child ->
                RenderWarpNode(child, clickAction)
            }
        }

        is WarpLinkNode -> RenderLink(node, clickAction, extra)
        is WarpTextNode -> RenderText(node, clickAction, extra)
        is WarpButtonNode -> RenderButton(node, clickAction, extra)
        is WarpSpacerNode -> Spacer(modifier = node.modifier.toGlanceModifier(clickAction).then(extra))
        is WarpDividerNode -> RenderDivider(node, clickAction, extra)
        is WarpProgressIndicatorNode -> RenderProgressIndicator(node, clickAction, extra)
        is WarpImageNode -> RenderImage(node, clickAction, extra)
    }
}

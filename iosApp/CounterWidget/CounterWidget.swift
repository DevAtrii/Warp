//
//  CounterWidget.swift
//  CounterWidget
//

import Shared
import SwiftUI
import WidgetKit
import warpWidgetKit

/// Timeline entry — state lives in App Group prefs; [CounterWidgetEntryView] composes live JSON.
struct CounterWidgetEntry: TimelineEntry {
    let date: Date
    let displayWidth: CGFloat
    let displayHeight: CGFloat
}

private let UPDATE_SAFETY_MARGIN_MILLIS: Int64 = 60 * 1000 // 1 minute
private let UPDATE_HOUR: Int = 1

/// Refreshes timeline schedule only. UI JSON is built in [CounterWidgetEntryView] from
/// `@Environment(\.colorScheme)` so theme matches what WidgetKit is drawing (including
/// light/dark pre-render passes).
struct CounterWidgetProvider: TimelineProvider {
    func placeholder(in context: Context) -> CounterWidgetEntry {
        entry(from: context)
    }

    func getSnapshot(in context: Context, completion: @escaping (CounterWidgetEntry) -> Void) {
        completion(entry(from: context))
    }

    func getTimeline(
        in context: Context,
        completion: @escaping (Timeline<CounterWidgetEntry>) -> Void
    ) {
        let now = Date()
        let calendar = Calendar.current

        let currentHour = calendar.component(.hour, from: now)

        let nextUpdateHour =
            ((currentHour / UPDATE_HOUR) + 1) * UPDATE_HOUR

        let nextHour: Date

        if nextUpdateHour >= 24 {
            nextHour = calendar.date(
                byAdding: .day,
                value: 1,
                to: calendar.startOfDay(for: now)
            )!
        } else {
            nextHour = calendar.date(
                bySettingHour: nextUpdateHour,
                minute: 0,
                second: 0,
                of: now
            )!
        }

        let intervalMillis = max(
            0,
            Int64(nextHour.timeIntervalSince(now) * 1000)
                - UPDATE_SAFETY_MARGIN_MILLIS
        )
        
        print("CounterWidgetProvider: 🔥 WidgetKit requested new timeline:", now)
        print("CounterWidgetProvider: 🔥 Update hour:", UPDATE_HOUR)
        print("CounterWidgetProvider: 🔥 Next update:", nextHour)
        print("CounterWidgetProvider: 🔥 Min interval:", intervalMillis, "ms")

        let session = WarpWidgetHost.shared.iosSession(
            widget: CounterWarpWidget.shared,
            kitFields: WarpWidgetKitEnv.from(context: context)
                .asKitFields(
                    appGroupId: CounterWarpWidget.shared.iosGroupId
                )
        )

        WarpWidgetHost.shared.dispatchOnUpdate(
            widget: CounterWarpWidget.shared,
            session: session,
            nowMillis: Int64(now.timeIntervalSince1970 * 1000),
            minIntervalMillis: intervalMillis,
            force: false
        )

        let size = context.displaySize

        let entry = CounterWidgetEntry(
            date: now,
            displayWidth: size.width,
            displayHeight: size.height
        )

        completion(
            Timeline(
                entries: [entry],
                policy: .after(nextHour)
            )
        )
    }


    private func entry(from context: Context) -> CounterWidgetEntry {
        let size = context.displaySize
        return CounterWidgetEntry(
            date: Date(),
            displayWidth: size.width,
            displayHeight: size.height
        )
    }
}

/// Composes WARP JSON at render time — [EnvironmentValues.colorScheme] is the source of truth.
struct CounterWidgetEntryView: View {
    @Environment(\.colorScheme) private var colorScheme
    @Environment(\.widgetFamily) private var widgetFamily
    @Environment(\.widgetRenderingMode) private var widgetRenderingMode

    var entry: CounterWidgetProvider.Entry

    var body: some View {
        WarpSwiftUIRootView(
            json: composeWidgetJson(
                colorScheme: colorScheme,
                widgetFamily: widgetFamily,
                widgetRenderingMode: widgetRenderingMode,
                displaySize: CGSize(width: entry.displayWidth, height: entry.displayHeight)
            ),
            useIntents: true,
            widgetId: CounterWarpWidget.shared.id
        )
    }
}

/// WidgetKit host for shared [CounterWarpWidget].
///
/// Named `CounterHomeWidget` so it does not clash with Kotlin
/// `CounterWidget` exported from Shared (`warp-runtime` example).
/// [kind] stays `"CounterWidget"` (= [CounterWarpWidget.id]) for reloads.
struct CounterHomeWidget: Widget {
    let kind: String = "CounterWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: CounterWidgetProvider()) { entry in
            if #available(iOS 17.0, *) {
                CounterWidgetEntryView(entry: entry)
                    .containerBackground(.fill.tertiary, for: .widget)
            } else {
                CounterWidgetEntryView(entry: entry)
                    .padding()
            }
        }
        .contentMarginsDisabled()
        .configurationDisplayName("Counter")
        .description("WARP counter widget")
        .supportedFamilies([.systemSmall,.systemMedium,.systemLarge])
    }
}

#Preview(as: .systemSmall) {
    CounterHomeWidget()
} timeline: {
    CounterWidgetEntry(date: .now, displayWidth: 155, displayHeight: 155)
}

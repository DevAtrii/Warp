import SwiftUI

@main
struct iOSApp: App {
    @State private var deepLinkMessage: String?

    var body: some Scene {
        WindowGroup {
            ContentView()
            .onOpenURL { url in
                handleDeepLink(url)
            }
            .alert(
                "Widget Deep Link",
                isPresented: Binding(
                    get: { deepLinkMessage != nil },
                    set: { if !$0 { deepLinkMessage = nil } }
                )
            ) {
                Button("OK") {
                    deepLinkMessage = nil
                }
            } message: {
                Text(deepLinkMessage ?? "")
            }
        }
    }

    private func handleDeepLink(_ url: URL) {
        guard url.scheme == "todo-widget",
              url.host == "simple"
        else {
            return
        }

        let components = URLComponents(
            url: url,
            resolvingAgainstBaseURL: false
        )

        let fontScale = components?.queryItems?
            .first(where: { $0.name == "fontScale" })?
            .value ?? "unknown"

        deepLinkMessage = "fontScale = \(fontScale)"
    }
}
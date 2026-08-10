import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    print("Opened via WarpLink: \(url.absoluteString)")
                }
        }
    }
}
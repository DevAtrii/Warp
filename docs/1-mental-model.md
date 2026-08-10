---
icon: lucide/brain
---

# Mental Model - WARP

A common misconception among developers is assuming a widget operates just like a standalone application—expecting to initialize dependency injection modules, perform asynchronous network requests, or execute heavy background logic directly within the widget process. **That is not how widgets work.**

---

## What is a Widget?

A **widget** is a lightweight UI component designed to surface glanceable, high-priority information on the home screen without requiring the user to open the full app.

* **Strict Resource Limits**: Operating systems enforce strict memory and power constraints on widgets to preserve battery life.
* **Data Lifecycle**: Widget data should be loaded following OS guidelines or pre-fetched within the main application before triggering a widget refresh.

!!! warning "Important"
    💡 **Core Rule**: Supply **pre-fetched data** (from a local database or remote API) directly to your widget. The widget's sole responsibility is to render the UI for that state.

!!! note
    *Note: Although you can request periodic background updates, the operating system retains ultimate control over execution scheduling and priority.*

---

## Anatomy of a Widget

Every widget consists of three essential parts:

1. **State**: The data payload powering the view.
2. **UI**: The visual representation rendered from the current state.
3. **Actions / Events**: Touch interactions triggered by the user.

```
[ State ] ──> Renders ──> [ UI ] ──> Triggers ──> [ Actions / Clicks ]
```

The widget accepts the **State**, renders the **UI**, and optionally routes user **Actions** back to the application to trigger business logic (such as launching a service or updating application state).

---

## Why WARP?

**WARP** (**W**idget **A**bstraction **R**endering **P**ipeline) provides a platform-independent abstraction layer to define widgets once and render them using native framework components on each target platform.

### Native Rendering Constraints
Due to strict OS resource boundaries, embedding heavy custom renderers (like Skia) inside widget processes is impossible. Both Android and iOS strictly limit supported layout nodes and composables.

WARP solves this by pairing a unified developer API with native platform execution:
* **Declarative DSL**: Write clean, declarative UI code using a Compose-style DSL.
* **Native Output**: WARP converts your abstract layout tree into true **native platform views** on Android and iOS.

---

Ready to jump in? Let's create our first widget!

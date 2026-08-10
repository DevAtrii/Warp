---
icon: lucide/bot
---

# Building Widgets with AI

You can leverage AI coding assistants—such as **Antigravity**, **Claude**, **Cursor**, **Codex**, and custom LLM tools—to generate complete, production-ready cross-platform WARP widgets automatically.

WARP provides a pre-configured **AI Skill (`SKILL.md`)** that teaches AI agents the exact architecture, composable DSL, theme tokens, Glance/WidgetKit host boilerplate, and best practices.

---

!!! tip "Quick Start: Copy & Paste Prompt for AI"
    Copy and paste this generic prompt directly into your AI assistant (Claude, Cursor, Antigravity, ChatGPT, or Codex) to generate any new widget:

    ```text
    Read the WARP Skill instructions from https://github.com/DevAtrii/Warp/tree/main/.agents/skills/warp-widget/SKILL.md and create a [WIDGET_NAME, e.g. Weather / Habit Tracker / Crypto] home screen widget for Kotlin Multiplatform. Include State, Actions, ActionHandler, shared WarpWidget composable UI, and Android Glance receiver boilerplate.
    ```

---

## 1. How the WARP AI Skill Works

The WARP AI Skill (`warp-widget`) provides AI models with complete context about WARP:

```
┌───────────────────────────────────────────────────────────┐
│                    Developer Prompt                       │
│    "Create a Crypto Price Tracker widget using WARP"      │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              ▼
┌───────────────────────────────────────────────────────────┐
│                 WARP Skill (SKILL.md)                     │
│  · Enforces WARP Compose DSL (WarpColumn, WarpText, etc.) │
│  · State & Actions serialization patterns                │
│  · Auto-generates Android Glance Receiver & AppWidget     │
│  · Generates PlatformContext & Swift WidgetKit bridges    │
└─────────────────────────────┬─────────────────────────────┘
                              │
                              ▼
┌───────────────────────────────────────────────────────────┐
│                    Generated Output                       │
│  1. CryptoWarpWidget.kt (commonMain)                      │
│  2. CryptoWidgetReceiver.kt (androidMain)                 │
│  3. App Group & WidgetKit Integration Guides             │
└───────────────────────────────────────────────────────────┘
```

---

## 2. Using WARP Skill across AI Tools

### 🤖 Antigravity & Agentic AI Tools
Antigravity automatically discovers skills placed in `.agents/skills/`.

- **Automatic Setup**: The skill is already configured in `.agents/skills/warp-widget/SKILL.md`.
- **Generic Prompt**:
  ```text
  "Read the WARP skill and build a Weather Forecast widget with temperature display and a Refresh action button."
  ```

---

### ⚡ Cursor
Cursor uses workspace rules and skills to guide code generation.

- **Option A (Auto-Discovery)**: Cursor automatically indexes `.agents/skills/warp-widget/SKILL.md`.
- **Option B (Direct URL Reference)**: In Cursor Chat or Agent Mode, paste the GitHub link:
  ```text
  "Using https://github.com/DevAtrii/Warp/tree/main/.agents/skills/warp-widget/SKILL.md, create a habit tracker WARP widget with daily check-in buttons."
  ```

---

### 🧠 Claude (Anthropic / Desktop / Web)
You can provide the WARP Skill to Claude via **Projects** or direct prompt attachment.

1. **Claude Projects**: Upload `docs/skills/warp-widget/SKILL.md` to your Claude Project Knowledge.
2. **Claude Code CLI / Desktop**: Place `SKILL.md` inside your project's `.agents/skills/warp-widget/SKILL.md` or `.claude/skills/`.
3. **Generic Prompting**:
   ```text
   "Read https://github.com/DevAtrii/Warp/tree/main/.agents/skills/warp-widget/SKILL.md and generate a music player home screen widget with Play, Pause, and Skip buttons."
   ```

---

### 🚀 OpenAI Codex & Custom LLM Assistants
For ChatGPT, Custom GPTs, or custom LLM scripts:

1. Copy the raw markdown content from [WARP `SKILL.md` on GitHub](https://github.com/DevAtrii/Warp/tree/main/.agents/skills/warp-widget/SKILL.md).
2. Paste it into your System Prompt or Knowledge Files.
3. **Generic Prompt**:
   ```text
   "Act as a Kotlin Multiplatform expert using WARP. Follow the SKILL.md guidelines and build a battery level status widget."
   ```

---

## 3. Example Copy & Paste Prompts

Here are ready-to-use prompt examples you can copy and paste:

=== "Counter & Multi-State"

    ```text
    "Using WARP skill (https://github.com/DevAtrii/Warp/tree/main/.agents/skills/warp-widget/SKILL.md), create a Step Counter widget in commonMain with Increment, Decrement, and Reset actions. Include adaptive layout support for small (2x2) and medium (4x2) sizes."
    ```

=== "Todo & List Widgets"

    ```text
    "Using WARP skill (https://github.com/DevAtrii/Warp/tree/main/.agents/skills/warp-widget/SKILL.md), build a Task Manager widget. Include a list of 3 tasks with checkmark buttons, a linear progress bar showing completion rate, and dark mode support."
    ```

=== "Media & Dashboard"

    ```text
    "Using WARP skill (https://github.com/DevAtrii/Warp/tree/main/.agents/skills/warp-widget/SKILL.md), build a compact Finance Tracker widget displaying account balance, recent transaction row, and a Refresh action handler."
    ```

---

## 4. Skill File Links & Repository Locations

You can view, reference, or download the WARP AI Skill directly:

| Reference | Location | Target AI Tool |
| :--- | :--- | :--- |
| 🔗 **GitHub Repository** | [**`SKILL.md` on GitHub**](https://github.com/DevAtrii/Warp/tree/main/.agents/skills/warp-widget/SKILL.md) | Web AI tools (Claude, Cursor, ChatGPT, Codex) |
| 📁 **Local Agent Skill** | **`.agents/skills/warp-widget/SKILL.md`** | **Antigravity**, **Cursor**, **Claude Code**, and KMP agents |
| 📄 **Documentation Copy** | **`docs/skills/warp-widget/SKILL.md`** | Local project documentation reference |

---

## 5. What the AI Will Generate

When you ask an AI assistant with the WARP Skill enabled to build a widget, it will generate:

1. **State Payload (`@Serializable data class ...`)**: Immutable state payload.
2. **Sealed Action Classes (`@Serializable sealed class ...`)**: Type-safe tap action events.
3. **Action Handler (`WarpActionHandler`)**: Asynchronous state updates via `updateWarpWidgetState`.
4. **Shared Composable UI (`WarpWidget`)**: Cross-platform Compose UI using WARP theme and layout primitives.
5. **Android Glance Host (`androidMain`)**: `WarpGlanceWidgetReceiver` and `WarpGlanceWidget` mapping drawable asset IDs.
6. **iOS Integration Guidance**: App Group configuration and Swift WidgetKit connection details.

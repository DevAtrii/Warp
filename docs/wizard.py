import os
import json

WIDGET_DIR = "iosApp/CounterWidget"
HTML_OUTPUT = "docs/wizard.html"

# Files to copy (excluding Assets.xcassets and CounterWidget.entitlements)
files_to_copy = [
    "CounterInstanceEntity.swift",
    "CounterWidget.swift",
    "CounterWidgetBundle.swift",
    "CounterWidgetClickIntent.swift",
    "CounterWidgetView.swift",
    "Info.plist"
]

files_content = {}
for f in files_to_copy:
    path = os.path.join(WIDGET_DIR, f)
    with open(path, 'r', encoding='utf-8') as file:
        files_content[f] = file.read()

# Generate the HTML
html_content = f"""<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WARP — iOS Widget Wizard</title>
    <link rel="icon" type="image/x-icon" href="assets/favicon/favicon.ico">

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500;600&family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap" rel="stylesheet">

    <!-- Shared Stylesheet -->
    <link rel="stylesheet" href="assets/pages.css">

    <!-- Iconify CDN -->
    <script src="https://code.iconify.design/iconify-icon/1.0.7/iconify-icon.min.js"></script>

    <!-- JSZip & FileSaver -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2.0.5/FileSaver.min.js"></script>
    
    <!-- PrismJS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism-tomorrow.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-swift.min.js"></script>

    <style>
        body {{
            display: flex;
            flex-direction: column;
            height: 100vh;
            overflow: hidden;
        }}

        /* Workspace Main Layout */
        .workspace {{
            display: flex;
            flex: 1;
            overflow: hidden;
            background: var(--bg-dark);
        }}

        /* Left Panel - Settings */
        #sidebar {{
            width: 340px;
            min-width: 280px;
            background: var(--bg-card);
            backdrop-filter: blur(20px);
            border-right: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            overflow-y: auto;
            padding: 1.5rem;
        }}

        .sidebar-header {{
            margin-bottom: 1.5rem;
        }}

        .sidebar-header h2 {{
            font-size: 1.3rem;
            font-weight: 800;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 0.4rem;
        }}

        .sidebar-header p {{
            font-size: 0.85rem;
            color: var(--text-muted);
            line-height: 1.4;
        }}

        .form-group {{
            margin-bottom: 1.25rem;
        }}

        label {{
            display: block;
            margin-bottom: 0.4rem;
            font-weight: 600;
            font-size: 0.85rem;
            color: var(--text-main);
        }}

        input[type="text"] {{
            width: 100%;
            padding: 0.65rem 0.8rem;
            font-size: 0.9rem;
            font-family: 'JetBrains Mono', monospace;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            background: var(--code-bg);
            color: var(--text-main);
            box-sizing: border-box;
            transition: all 0.2s ease;
        }}

        input[type="text"]:focus {{
            outline: none;
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(16, 185, 129, 0.25);
        }}

        small {{
            display: block;
            margin-top: 0.35rem;
            color: var(--text-dim);
            font-size: 0.75rem;
            line-height: 1.3;
        }}

        .instructions-card {{
            margin-top: 1.5rem;
            padding: 1rem;
            background: rgba(16, 185, 129, 0.06);
            border: 1px solid var(--border-highlight);
            border-radius: 10px;
            font-size: 0.8rem;
            color: var(--text-muted);
            line-height: 1.5;
        }}

        .instructions-card strong {{
            color: var(--primary);
            display: flex;
            align-items: center;
            gap: 0.4rem;
            margin-bottom: 0.5rem;
        }}

        .instructions-card ol {{
            margin: 0;
            padding-left: 1.2rem;
        }}

        .instructions-card li {{
            margin-bottom: 0.3rem;
        }}

        /* Middle Panel - File List */
        #file-explorer {{
            width: 260px;
            min-width: 200px;
            background: rgba(11, 15, 25, 0.9);
            border-right: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }}

        .file-list-header {{
            padding: 1rem;
            font-size: 0.75rem;
            text-transform: uppercase;
            font-weight: 700;
            color: var(--text-dim);
            letter-spacing: 0.05em;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            align-items: center;
            gap: 0.4rem;
        }}

        #fileListContainer {{
            flex: 1;
            overflow-y: auto;
            padding: 0.5rem;
        }}

        .file-item {{
            padding: 0.6rem 0.8rem;
            border-radius: 6px;
            cursor: pointer;
            font-size: 0.82rem;
            font-family: 'JetBrains Mono', monospace;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: var(--text-muted);
            transition: all 0.15s ease;
            margin-bottom: 3px;
            width: 100%;
            overflow: hidden;
            box-sizing: border-box;
        }}

        .file-item iconify-icon {{
            font-size: 1.1rem;
            color: #3b82f6;
            flex-shrink: 0;
        }}

        .file-item.xml-icon iconify-icon {{
            color: #f97316;
        }}

        .file-item .file-name {{
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            flex: 1;
            min-width: 0;
        }}

        .file-item:hover {{
            background: rgba(255, 255, 255, 0.05);
            color: var(--text-main);
        }}

        .file-item.active {{
            background: rgba(16, 185, 129, 0.15);
            border: 1px solid var(--border-highlight);
            color: var(--primary);
            font-weight: 600;
        }}

        .file-item.active iconify-icon {{
            color: var(--primary);
        }}

        /* Right Panel - Code Editor */
        #editor {{
            flex: 1;
            display: flex;
            flex-direction: column;
            background: var(--code-bg);
            overflow: hidden;
            min-width: 0;
        }}

        #editor-header {{
            padding: 0.75rem 1.25rem;
            border-bottom: 1px solid var(--border-color);
            background: rgba(255, 255, 255, 0.02);
            font-weight: 600;
            font-family: 'JetBrains Mono', monospace;
            color: var(--text-muted);
            font-size: 0.85rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }}

        #editor-content {{
            flex: 1;
            overflow: auto;
            margin: 0;
            padding: 1.25rem !important;
            background: transparent !important;
            font-family: 'JetBrains Mono', monospace !important;
            font-size: 0.85rem !important;
            line-height: 1.6 !important;
        }}

        /* Prism resets */
        pre[class*="language-"] {{
            margin: 0 !important;
            padding: 0 !important;
            background: transparent !important;
        }}

        /* Mobile Layout */
        @media (max-width: 768px) {{
            body {{
                height: auto !important;
                overflow-y: auto !important;
            }}

            .workspace {{
                flex-direction: column !important;
                overflow: visible !important;
            }}

            #sidebar {{
                width: 100% !important;
                border-right: none !important;
                border-bottom: 1px solid var(--border-color);
            }}

            #file-explorer {{
                width: 100% !important;
                max-height: 220px;
                border-right: none !important;
                border-bottom: 1px solid var(--border-color);
            }}

            #editor {{
                width: 100% !important;
                height: 450px;
            }}
        }}
    </style>
</head>
<body>

    <!-- Header / Navbar -->
    <header>
        <nav>
            <a href="index.html" class="logo">
                <iconify-icon icon="lucide:cpu" style="color: var(--primary); font-size: 1.5rem;"></iconify-icon>
                WARP
                <span class="logo-badge">KMP</span>
            </a>

            <div class="nav-links">
                <a href="docs/2-creating-your-first-widget">First Widget</a>
                <a href="2-how-warp-works">How It Works</a>
                <a href="wizard.html" style="color: var(--primary); font-weight: 600;">Widget Wizard</a>
            </div>

            <div class="nav-actions">
                <a href="https://github.com/DevAtrii/Warp" target="_blank" class="btn btn-outline">
                    <iconify-icon icon="lucide:github"></iconify-icon>
                    GitHub Repo
                </a>
                <a href="docs/2-creating-your-first-widget" class="btn btn-primary">
                    <iconify-icon icon="lucide:rocket"></iconify-icon>
                    Get Started
                </a>
            </div>
        </nav>
    </header>

    <!-- Workspace Layout -->
    <div class="workspace">
        
        <!-- Left Sidebar / Inputs -->
        <div id="sidebar">
            <div class="sidebar-header">
                <h2>
                    <iconify-icon icon="lucide:wand-2" style="color: var(--primary);"></iconify-icon>
                    Widget Wizard
                </h2>
                <p>Generate iOS WidgetKit SwiftUI and AppIntent boilerplate for your KMP widget.</p>
            </div>

            <div class="form-group">
                <label for="widgetName">iOS Widget Name</label>
                <input type="text" id="widgetName" placeholder="e.g. Counter">
                <small>Prefix for generated Swift files (e.g. CounterWidget.swift).</small>
            </div>

            <div class="form-group">
                <label for="warpObjectName">Kotlin WARP Object Name</label>
                <input type="text" id="warpObjectName" placeholder="e.g. CounterWarpWidget">
                <small>The exact name of your shared Kotlin WARP widget object.</small>
            </div>

            <div class="form-group">
                <label for="sharedModuleName">Shared Framework Name</label>
                <input type="text" id="sharedModuleName" placeholder="e.g. Shared">
                <small>The iOS framework name of your KMP shared module.</small>
            </div>

            <button onclick="generateAndDownload()" class="btn btn-primary" style="width: 100%;">
                <iconify-icon icon="lucide:download"></iconify-icon>
                Download Source (.zip)
            </button>

            <div class="instructions-card">
                <strong>
                    <iconify-icon icon="lucide:check-circle-2"></iconify-icon>
                    Next Steps in Xcode
                </strong>
                <ol>
                    <li>Create a Widget Extension target in Xcode.</li>
                    <li>Add a "Compile Kotlin" Run Script step in Build Phases.</li>
                    <li>Extract downloaded source `.zip` into your Widget Extension folder.</li>
                </ol>
            </div>
        </div>

        <!-- Middle Explorer -->
        <div id="file-explorer">
            <div class="file-list-header">
                <iconify-icon icon="lucide:folder-tree"></iconify-icon>
                Generated Files
            </div>
            <div id="fileListContainer"></div>
        </div>

        <!-- Right Code Editor -->
        <div id="editor">
            <div id="editor-header">
                <iconify-icon icon="lucide:file-code-2" style="color: var(--primary);"></iconify-icon>
                <span id="currentFileName">No file selected</span>
            </div>
            <pre id="editor-content" class="language-swift"><code id="codeBlock"></code></pre>
        </div>

    </div>

    <!-- JS Generator Script -->
    <script>
        const rawTemplateFiles = {json.dumps(files_content)};
        let generatedFiles = {{}};
        let currentSelectedFilename = null;

        // Elements
        const elWidgetName = document.getElementById('widgetName');
        const elWarpObjectName = document.getElementById('warpObjectName');
        const elSharedModuleName = document.getElementById('sharedModuleName');
        const elFileListContainer = document.getElementById('fileListContainer');
        const elCurrentFileName = document.getElementById('currentFileName');
        const elCodeBlock = document.getElementById('codeBlock');

        // Load from LocalStorage
        if (localStorage.getItem('warp_widgetName')) elWidgetName.value = localStorage.getItem('warp_widgetName');
        if (localStorage.getItem('warp_warpObjectName')) elWarpObjectName.value = localStorage.getItem('warp_warpObjectName');
        if (localStorage.getItem('warp_sharedModuleName')) elSharedModuleName.value = localStorage.getItem('warp_sharedModuleName');

        // Defaults if empty
        if (!elWidgetName.value) elWidgetName.value = "Counter";
        if (!elWarpObjectName.value) elWarpObjectName.value = "CounterWarpWidget";
        if (!elSharedModuleName.value) elSharedModuleName.value = "Shared";

        // Listen for changes
        elWidgetName.addEventListener('input', updateLivePreview);
        elWarpObjectName.addEventListener('input', updateLivePreview);
        elSharedModuleName.addEventListener('input', updateLivePreview);

        function getReplacements() {{
            const widget = elWidgetName.value.trim() || 'Counter';
            const warpObject = elWarpObjectName.value.trim() || 'CounterWarpWidget';
            const sharedModule = elSharedModuleName.value.trim() || 'Shared';
            
            // Save to LocalStorage
            localStorage.setItem('warp_widgetName', elWidgetName.value.trim());
            localStorage.setItem('warp_warpObjectName', elWarpObjectName.value.trim());
            localStorage.setItem('warp_sharedModuleName', elSharedModuleName.value.trim());

            return {{ widget, warpObject, sharedModule }};
        }}

        function updateLivePreview() {{
            const {{ widget, warpObject, sharedModule }} = getReplacements();
            generatedFiles = {{}};

            for (const [filename, content] of Object.entries(rawTemplateFiles)) {{
                const newFilename = filename.replace("Counter", widget);
                
                let newContent = content;
                newContent = newContent.replaceAll("CounterWarpWidget", warpObject);
                newContent = newContent.replaceAll("Counter", widget);
                
                if (sharedModule !== "Shared") {{
                    newContent = newContent.replace(/^import Shared$/gm, "import " + sharedModule);
                }}

                generatedFiles[newFilename] = newContent;
            }}

            renderFileList();
            
            if (!currentSelectedFilename || !generatedFiles[currentSelectedFilename]) {{
                const fileNames = Object.keys(generatedFiles);
                if (fileNames.length > 0) {{
                    selectFile(fileNames[0]);
                }}
            }} else {{
                selectFile(currentSelectedFilename);
            }}
        }}

        function renderFileList() {{
            elFileListContainer.innerHTML = '';
            for (const filename of Object.keys(generatedFiles).sort()) {{
                const div = document.createElement('div');
                div.className = 'file-item';
                if (filename === currentSelectedFilename) {{
                    div.classList.add('active');
                }}
                if (filename.endsWith('.plist')) {{
                    div.classList.add('xml-icon');
                    div.innerHTML = `<iconify-icon icon="vscode-icons:file-type-xml"></iconify-icon><span class="file-name">${{filename}}</span>`;
                }} else {{
                    div.innerHTML = `<iconify-icon icon="vscode-icons:file-type-swift"></iconify-icon><span class="file-name">${{filename}}</span>`;
                }}
                div.onclick = () => selectFile(filename);
                elFileListContainer.appendChild(div);
            }}
        }}

        function selectFile(filename) {{
            currentSelectedFilename = filename;
            renderFileList();
            
            elCurrentFileName.textContent = filename;
            elCodeBlock.textContent = generatedFiles[filename] || "";
            
            if (filename.endsWith('.plist')) {{
                elCodeBlock.className = 'language-xml';
            }} else {{
                elCodeBlock.className = 'language-swift';
            }}
            
            Prism.highlightElement(elCodeBlock);
        }}

        function generateAndDownload() {{
            const {{ widget }} = getReplacements();
            const zip = new JSZip();
            const folderName = widget + "Widget";
            const folder = zip.folder(folderName);

            for (const [filename, content] of Object.entries(generatedFiles)) {{
                folder.file(filename, content);
            }}

            zip.generateAsync({{ type: "blob" }}).then(function(content) {{
                saveAs(content, folderName + ".zip");
            }});
        }}

        // Initial render
        updateLivePreview();
    </script>
</body>
</html>
"""

# Ensure the output directory exists
os.makedirs(os.path.dirname(HTML_OUTPUT) or '.', exist_ok=True)

with open(HTML_OUTPUT, 'w', encoding='utf-8') as f:
    f.write(html_content)

print(f"Generated {HTML_OUTPUT} successfully.")

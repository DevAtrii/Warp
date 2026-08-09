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
html_content = f"""
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WARP iOS Widget Wizard</title>
    <!-- JSZip & FileSaver -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2.0.5/FileSaver.min.js"></script>
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- PrismJS -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-swift.min.js"></script>
    <style>
        :root {{
            --border-color: #e0e0e0;
            --bg-body: #ffffff;
            --bg-sidebar: #fafafa;
            --bg-files: #f3f3f3;
            --text-main: #333333;
            --text-muted: #666666;
            --accent: #007aff;
            --accent-hover: #005bb5;
        }}
        /* Mobile */
        @media (max-width: 768px) {{
            #file-explorer,
            #editor {{
                display: none!important;
            }}

            #sidebar {{
                width: 100%!important;
                border-right: none!important;
            }}

            body {{
                display: block!important;
                height: auto!important;
                overflow: auto!important;
            }}
        }}
        body {{
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: var(--bg-body);
            color: var(--text-main);
            display: flex;
            height: 100vh;
            overflow: hidden;
        }}
        /* Left Panel - Settings */
        #sidebar {{
            width: 320px;
            background: var(--bg-sidebar);
            border-right: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            overflow-y: auto;
        }}
        .header {{
            padding: 1.5rem 1.5rem 1rem;
        }}
        .header h1 {{
            margin: 0 0 0.5rem 0;
            font-size: 1.25rem;
            color: #111;
        }}
        .header p {{
            margin: 0;
            font-size: 0.85rem;
            color: var(--text-muted);
            line-height: 1.4;
        }}
        .form-section {{
            padding: 0 1.5rem 1rem;
        }}
        .form-group {{
            margin-bottom: 1.25rem;
        }}
        label {{
            display: block;
            margin-bottom: 0.4rem;
            font-weight: 600;
            font-size: 0.85rem;
        }}
        input[type="text"] {{
            width: 100%;
            padding: 0.5rem;
            font-size: 0.9rem;
            border: 1px solid #ccc;
            border-radius: 4px;
            background: #fff;
            box-sizing: border-box;
        }}
        input[type="text"]:focus {{
            outline: none;
            border-color: var(--accent);
            box-shadow: 0 0 0 2px rgba(0,122,255,0.2);
        }}
        small {{
            display: block;
            margin-top: 0.3rem;
            color: var(--text-muted);
            font-size: 0.75rem;
            line-height: 1.3;
        }}
        button {{
            width: 100%;
            padding: 0.75rem;
            font-size: 0.95rem;
            color: white;
            background-color: var(--accent);
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-weight: 600;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
        }}
        button:hover {{
            background-color: var(--accent-hover);
        }}
        .instructions {{
            padding: 1rem 1.5rem;
            background: #eef5ff;
            border-top: 1px solid #d1e4ff;
            border-bottom: 1px solid #d1e4ff;
            font-size: 0.8rem;
            color: #004085;
            line-height: 1.5;
            margin-bottom: 1rem;
        }}
        .instructions strong {{
            display: block;
            margin-bottom: 0.5rem;
        }}
        .instructions ol {{
            margin: 0;
            padding-left: 1.2rem;
        }}
        .instructions li {{
            margin-bottom: 0.3rem;
        }}

        /* Middle Panel - File List */
        #file-explorer {{
            width: 250px;
            background: var(--bg-files);
            border-right: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
        }}
        .file-list-header {{
            padding: 1rem;
            font-size: 0.75rem;
            text-transform: uppercase;
            font-weight: bold;
            color: #888;
            letter-spacing: 0.5px;
        }}
        #fileListContainer {{
            flex: 1;
            overflow-y: auto;
        }}
        .file-item {{
            padding: 0.4rem 1rem;
            cursor: pointer;
            font-size: 0.85rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            color: #444;
        }}
        .file-item i {{
            color: #5c9add; /* subtle blue for swift icons */
            width: 14px;
            text-align: center;
        }}
        .file-item.xml-icon i {{
            color: #d35400; /* orange for xml/plist */
        }}
        .file-item:hover {{
            background: #e8e8e8;
        }}
        .file-item.active {{
            background: var(--accent);
            color: white;
        }}
        .file-item.active i {{
            color: white;
        }}

        /* Right Panel - Code Editor */
        #editor {{
            flex: 1;
            display: flex;
            flex-direction: column;
            background: var(--bg-body);
            overflow: hidden;
        }}
        #editor-header {{
            padding: 0.8rem 1.5rem;
            border-bottom: 1px solid var(--border-color);
            background: #f9f9f9;
            font-weight: 600;
            font-family: monospace;
            color: #333;
            font-size: 0.9rem;
        }}
        #editor-content {{
            flex: 1;
            overflow: auto;
            margin: 0;
            padding: 1.5rem !important;
            background: #fafafa;
            font-size: 0.9rem;
            line-height: 1.5;
        }}
        /* Prism resets */
        pre[class*="language-"] {{
            margin: 0 !important;
            padding: 0 !important;
            background: transparent !important;
        }}
    </style>
</head>
<body>
    <!-- Sidebar / Settings -->
    <div id="sidebar">
        <div class="header">
            <h1>WARP iOS Wizard</h1>
            <p>Generate iOS boilerplate for your KMP WARP widget.</p>
        </div>
        
        <div class="form-section">
            <div class="form-group">
                <label for="widgetName">iOS Widget Name</label>
                <input type="text" id="widgetName" placeholder="e.g. Weather">
                <small>Prefix for the generated Swift files (e.g. WeatherWidget.swift).</small>
            </div>
            <div class="form-group">
                <label for="warpObjectName">Kotlin WARP Object Name</label>
                <input type="text" id="warpObjectName" placeholder="e.g. WeatherWarpWidget">
                <small>The name of your shared Kotlin WARP widget object to bind to.</small>
            </div>
            <div class="form-group">
                <label for="sharedModuleName">Shared Module Name</label>
                <input type="text" id="sharedModuleName" placeholder="e.g. SharedLogic">
                <small>The iOS framework name of your KMP shared module.</small>
            </div>
            <button onclick="generateAndDownload()">
                <i class="fa-solid fa-download"></i> Download Source (.zip)
            </button>
        </div>

        <div class="instructions">
            <strong>Next Steps:</strong>
            <ol>
                <li>Create a Widget Extension target in your iOS app in Xcode.</li>
                <li>Add a "Compile Kotlin" Run Script step to its Build Phases.</li>
                <li>Delete the default files Xcode made, and extract this downloaded zip into your Widget Extension folder.</li>
            </ol>
        </div>
    </div>

    <!-- Middle Panel / File Explorer -->
    <div id="file-explorer">
        <div class="file-list-header">Generated Files</div>
        <div id="fileListContainer"></div>
    </div>

    <!-- Right Panel / Code Editor -->
    <div id="editor">
        <div id="editor-header">No file selected</div>
        <pre id="editor-content" class="language-swift"><code id="codeBlock"></code></pre>
    </div>

    <script>
        const rawTemplateFiles = {json.dumps(files_content)};
        let generatedFiles = {{}};
        let currentSelectedFilename = null;

        // Elements
        const elWidgetName = document.getElementById('widgetName');
        const elWarpObjectName = document.getElementById('warpObjectName');
        const elSharedModuleName = document.getElementById('sharedModuleName');
        const elFileListContainer = document.getElementById('fileListContainer');
        const elEditorHeader = document.getElementById('editor-header');
        const elCodeBlock = document.getElementById('codeBlock');

        // Load from LocalStorage
        if (localStorage.getItem('warp_widgetName')) elWidgetName.value = localStorage.getItem('warp_widgetName');
        if (localStorage.getItem('warp_warpObjectName')) elWarpObjectName.value = localStorage.getItem('warp_warpObjectName');
        if (localStorage.getItem('warp_sharedModuleName')) elSharedModuleName.value = localStorage.getItem('warp_sharedModuleName');

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

            // First, get the list of filenames to determine default selection
            for (const [filename, content] of Object.entries(rawTemplateFiles)) {{
                const newFilename = filename.replace("Counter", widget);
                
                let newContent = content;
                // Replace Warp object name first (so it doesn't get mangled by the generic 'Counter' replace)
                newContent = newContent.replaceAll("CounterWarpWidget", warpObject);
                // Replace remaining 'Counter' instances
                newContent = newContent.replaceAll("Counter", widget);
                
                // Replace import
                if (sharedModule !== "Shared") {{
                    newContent = newContent.replace(/^import Shared$/gm, "import " + sharedModule);
                }}

                generatedFiles[newFilename] = newContent;
            }}

            renderFileList();
            
            // Re-select current file if it exists, otherwise select first
            if (!currentSelectedFilename || !generatedFiles[currentSelectedFilename]) {{
                const fileNames = Object.keys(generatedFiles);
                if (fileNames.length > 0) {{
                    selectFile(fileNames[0]);
                }}
            }} else {{
                // Refresh content of currently selected
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
                    div.innerHTML = `<i class="fa-solid fa-file-code"></i> ${{filename}}`;
                }} else {{
                    div.innerHTML = `<i class="fa-brands fa-swift"></i> ${{filename}}`;
                }}
                div.onclick = () => selectFile(filename);
                elFileListContainer.appendChild(div);
            }}
        }}

        function selectFile(filename) {{
            currentSelectedFilename = filename;
            renderFileList(); // updates active class
            
            elEditorHeader.textContent = filename;
            elCodeBlock.textContent = generatedFiles[filename] || "";
            
            // Determine language for Prism
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

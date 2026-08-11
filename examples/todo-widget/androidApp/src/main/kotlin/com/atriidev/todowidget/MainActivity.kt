package com.atriidev.todowidget

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        handleDeepLink(intent)
        super.onCreate(savedInstanceState)

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val uri = intent?.data ?: return

        if (uri.scheme == "todo-widget" && uri.host == "simple") {
            val fontScale = uri
                .getQueryParameter("fontScale")
                ?.toFloatOrNull()

            Toast.makeText(
                this,
                "DeepLink: fontScale = $fontScale",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
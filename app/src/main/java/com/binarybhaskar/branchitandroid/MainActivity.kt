package com.binarybhaskar.branchitandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.content.SharedPreferences
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.binarybhaskar.branchitandroid.navigation.BranchITNavHost
//import com.binarybhaskar.branchitandroid.notifications.NotificationSettingsManager
import com.binarybhaskar.branchitandroid.ui.theme.BranchITTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
//        NotificationSettingsManager.sync(this, prefs)

        setContent {
            var isLoggedIn by remember { mutableStateOf(prefs.getBoolean("is_logged_in", false)) }
            var dynamicColor by remember {
                mutableStateOf(
                    prefs.getBoolean(
                        "dynamic_color", false
                    )
                )
            }

            DisposableEffect(Unit) {
                val listener = SharedPreferences.OnSharedPreferenceChangeListener { shared, key ->
                    when (key) {
                        "dynamic_color" -> dynamicColor = shared.getBoolean("dynamic_color", false)
//                      TODO: Notifications
//                        "notif_events", "notif_chat_choice" -> NotificationSettingsManager.sync(
//                            this@MainActivity, prefs
//                        )

                        "is_logged_in" -> isLoggedIn = shared.getBoolean("is_logged_in", false)
                    }
                }
                prefs.registerOnSharedPreferenceChangeListener(listener)
                onDispose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
            }
            BranchITTheme(dynamicColor = dynamicColor) {
                BranchITNavHost(isLoggedIn, prefs)
            }
        }
    }
}
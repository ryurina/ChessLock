package app.chesslock

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.chesslock.ui.*

class MainActivity : ComponentActivity() {
    private var guardOn by mutableStateOf(false)

    private fun checkGuard() {
        val s = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        guardOn = s?.contains("$packageName/") == true
    }

    override fun onResume() { super.onResume(); checkGuard() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pm = packageManager
        val apps = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
            .map { it.activityInfo.packageName to it.loadLabel(pm).toString() }
            .filter { it.first != packageName }.distinctBy { it.first }.sortedBy { it.second.lowercase() }

        setContent { BTheme {
            val blocked = remember { mutableStateListOf<String>().apply { addAll(Prefs.blocked(this@MainActivity)) } }
            var minutes by remember { mutableIntStateOf(Prefs.minutes(this@MainActivity)) }
            LazyColumn(
                Modifier.systemBarsPadding().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Box(Modifier.fillMaxWidth().brutal(B.Yellow, 8.dp, 4.dp).padding(18.dp)) {
                        Column {
                            BText("ChessLock", 44)
                            BText("Win a puzzle. Earn your scroll.", 16)
                        }
                    }
                }
                item {
                    Box(Modifier.fillMaxWidth().brutal(if (guardOn) B.Green else B.Pink).padding(14.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            BText(if (guardOn) "Guard is on. Locked apps ask for a puzzle." else "Guard is off. Nothing is locked yet.", 18)
                            if (!guardOn) BButton("Turn on in Accessibility", fill = B.White) {
                                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            }
                        }
                    }
                }
                item {
                    Box(Modifier.fillMaxWidth().brutal(B.Blue).padding(14.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            BText("Unlock time after a solve", 18)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                BButton("−", fill = B.White) { minutes = (minutes - 5).coerceAtLeast(5); Prefs.setMinutes(this@MainActivity, minutes) }
                                BText("$minutes min", 26)
                                BButton("+", fill = B.White) { minutes = (minutes + 5).coerceAtMost(60); Prefs.setMinutes(this@MainActivity, minutes) }
                            }
                        }
                    }
                }
                item { BButton("Try a puzzle", Modifier.fillMaxWidth(), B.Green) { startActivity(Intent(this@MainActivity, PuzzleActivity::class.java)) } }
                item { BText("Choose apps to lock", 24) }
                items(apps, key = { it.first }) { (pkg, name) ->
                    val on = pkg in blocked
                    Row(
                        Modifier.fillMaxWidth().brutal(if (on) B.Pink else B.White, 4.dp)
                            .clickable {
                                if (on) blocked.remove(pkg) else blocked.add(pkg)
                                Prefs.setBlocked(this@MainActivity, blocked.toSet())
                            }.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
                    ) {
                        BText(name, 16, Modifier.weight(1f))
                        BText(if (on) "Locked" else "Open", 14)
                    }
                }
            }
        } }
    }
}

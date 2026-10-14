package app.chesslock

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.InputMethodManager

class ChessLockService : AccessibilityService() {
    private var lastLaunch = 0L

    private fun imePackages(): Set<String> =
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .enabledInputMethodList.map { it.packageName }.toSet()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg == packageName || pkg == "com.android.systemui") return

        if (Prefs.everyTime(this)) {
            val session = Prefs.session(this)
            if (session != null) {
                if (pkg == session || pkg in imePackages()) return
                Prefs.setSession(this, null) // user left the app, so it locks again
            }
        } else if (Prefs.isUnlocked(this, pkg)) return

        if (pkg !in Prefs.blocked(this)) return
        val now = SystemClock.elapsedRealtime()
        if (now - lastLaunch < 800) return
        lastLaunch = now
        startActivity(Intent(this, PuzzleActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("target", pkg)
        })
    }

    override fun onInterrupt() {}
}

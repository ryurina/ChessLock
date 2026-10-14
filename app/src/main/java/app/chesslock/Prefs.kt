package app.chesslock

import android.content.Context

object Prefs {
    private val DEFAULTS = setOf(
        "com.facebook.katana", "com.google.android.youtube",
        "com.instagram.android", "com.zhiliaoapp.musically"
    )
    private fun sp(c: Context) = c.getSharedPreferences("chesslock", Context.MODE_PRIVATE)

    fun blocked(c: Context): Set<String> = sp(c).getStringSet("blocked", DEFAULTS) ?: DEFAULTS
    fun setBlocked(c: Context, s: Set<String>) = sp(c).edit().putStringSet("blocked", s).apply()

    fun minutes(c: Context) = sp(c).getInt("minutes", 15)
    fun setMinutes(c: Context, m: Int) = sp(c).edit().putInt("minutes", m).apply()

    /** true = puzzle on every open (app relocks when you leave it); false = timed unlock. */
    fun everyTime(c: Context) = sp(c).getBoolean("everyTime", false)
    fun setEveryTime(c: Context, b: Boolean) = sp(c).edit().putBoolean("everyTime", b).apply()

    fun session(c: Context): String? = sp(c).getString("session", null)
    fun setSession(c: Context, pkg: String?) = sp(c).edit().putString("session", pkg).apply()

    fun unlock(c: Context, pkg: String) =
        sp(c).edit().putLong("until:$pkg", System.currentTimeMillis() + minutes(c) * 60_000L).apply()
    fun isUnlocked(c: Context, pkg: String) = System.currentTimeMillis() < sp(c).getLong("until:$pkg", 0L)

    fun grant(c: Context, pkg: String) = if (everyTime(c)) setSession(c, pkg) else unlock(c, pkg)
}

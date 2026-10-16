package com.example.floatingmacro

import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.view.WindowManager
import org.json.JSONArray
import org.json.JSONObject

data class Macro(
    val id: Long,
    val name: String,
    val text: String,
    val color: Int,
    val repeat: Boolean,
    val intervalSec: Int
)

object MacroStore {
    private const val PREFS = "macro_prefs"
    private const val KEY_MACROS = "macros"

    const val POINT_CHAT = "chat"
    const val POINT_SEND = "send"

    const val MIN_INTERVAL_SEC = 3

    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(c: Context): MutableList<Macro> {
        val list = mutableListOf<Macro>()
        val arr = JSONArray(prefs(c).getString(KEY_MACROS, "[]"))
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            list.add(
                Macro(
                    id = o.optLong("id", 1000L + i),
                    name = o.getString("name"),
                    text = o.getString("text"),
                    color = o.optInt("color", 0xFF3949AB.toInt()),
                    repeat = o.optBoolean("repeat", false),
                    intervalSec = o.optInt("interval", 5)
                )
            )
        }
        return list
    }

    private fun save(c: Context, list: List<Macro>) {
        val arr = JSONArray()
        list.forEach {
            arr.put(
                JSONObject()
                    .put("id", it.id).put("name", it.name).put("text", it.text)
                    .put("color", it.color).put("repeat", it.repeat).put("interval", it.intervalSec)
            )
        }
        prefs(c).edit().putString(KEY_MACROS, arr.toString()).apply()
    }

    fun add(c: Context, macro: Macro) = save(c, load(c).apply { add(macro) })

    fun remove(c: Context, id: Long) = save(c, load(c).filter { it.id != id })

    fun getPoint(c: Context, key: String): PointF {
        val defX = if (key == POINT_CHAT) 0.50f else 0.93f
        val defY = if (key == POINT_CHAT) 0.06f else 0.92f
        val p = prefs(c)
        return PointF(p.getFloat("${key}_x", defX), p.getFloat("${key}_y", defY))
    }

    fun setPoint(c: Context, key: String, fx: Float, fy: Float) {
        prefs(c).edit().putFloat("${key}_x", fx).putFloat("${key}_y", fy).apply()
    }
}

fun screenSize(c: Context): Point {
    val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= 30) {
        val b = wm.maximumWindowMetrics.bounds
        Point(b.width(), b.height())
    } else {
        val p = Point()
        @Suppress("DEPRECATION") wm.defaultDisplay.getRealSize(p)
        p
    }
}

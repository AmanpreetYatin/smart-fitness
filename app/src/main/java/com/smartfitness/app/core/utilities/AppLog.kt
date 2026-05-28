package com.smartfitness.app.core.utilities

import android.util.Log

object AppLog {

    private const val DEFAULT_TAG = "SmartFitness"
    private const val MAX_LENGTH = 4000

    // Toggle logs
    var IS_DEBUG = true

    // ─────────────────────────────────────────────
    // 🔹 Core Logger
    // ─────────────────────────────────────────────

    private fun buildMessage(message: String): String {
        val stackTrace = Throwable().stackTrace
        val element = stackTrace.getOrNull(3)

        val className = element?.fileName ?: "Unknown"
        val methodName = element?.methodName ?: "Unknown"
        val lineNumber = element?.lineNumber ?: -1

        return """
            ─────────────────────────────────────────────
            📍 $className → $methodName():$lineNumber
            💬 $message
            ─────────────────────────────────────────────
        """.trimIndent()
    }

    private fun logLong(tag: String, message: String, logFunc: (String, String) -> Unit) {
        if (message.length <= MAX_LENGTH) {
            logFunc(tag, message)
        } else {
            message.chunked(MAX_LENGTH).forEach {
                logFunc(tag, it)
            }
        }
    }

    // ─────────────────────────────────────────────
    // 🔹 Debug
    // ─────────────────────────────────────────────

    fun d(tag: String = DEFAULT_TAG, message: String) {
        if (!IS_DEBUG) return
        logLong(tag, buildMessage("🐛 DEBUG → $message")) { t, m ->
            Log.d(t, m)
        }
    }

    // ─────────────────────────────────────────────
    // 🔹 Info
    // ─────────────────────────────────────────────

    fun i(tag: String = DEFAULT_TAG, message: String) {
        if (!IS_DEBUG) return
        logLong(tag, buildMessage("ℹ️ INFO → $message")) { t, m ->
            Log.i(t, m)
        }
    }

    // ─────────────────────────────────────────────
    // 🔹 Warning
    // ─────────────────────────────────────────────

    fun w(tag: String = DEFAULT_TAG, message: String) {
        if (!IS_DEBUG) return
        logLong(tag, buildMessage("⚠️ WARNING → $message")) { t, m ->
            Log.w(t, m)
        }
    }

    // ─────────────────────────────────────────────
    // 🔹 Error
    // ─────────────────────────────────────────────

    fun e(
        tag: String = DEFAULT_TAG,
        message: String,
        throwable: Throwable? = null
    ) {
        if (!IS_DEBUG) return
        logLong(tag, buildMessage("❌ ERROR → $message")) { t, m ->
            Log.e(t, m, throwable)
        }
    }

    // ─────────────────────────────────────────────
    // 🔹 Verbose
    // ─────────────────────────────────────────────

    fun v(tag: String = DEFAULT_TAG, message: String) {
        if (!IS_DEBUG) return
        logLong(tag, buildMessage("🔍 VERBOSE → $message")) { t, m ->
            Log.v(t, m)
        }
    }

    // ─────────────────────────────────────────────
    // 🔹 Special Pretty Logs
    // ─────────────────────────────────────────────

    fun json(tag: String = DEFAULT_TAG, json: String) {
        if (!IS_DEBUG) return
        logLong(tag, "📦 JSON ↓↓↓\n$json") { t, m ->
            Log.d(t, m)
        }
    }

    fun line(tag: String = DEFAULT_TAG) {
        if (!IS_DEBUG) return
        Log.d(tag, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }

    fun section(tag: String = DEFAULT_TAG, title: String) {
        if (!IS_DEBUG) return
        Log.d(tag, """
            ╔════════════════════════════════════
            ║ 🚀 $title
            ╚════════════════════════════════════
        """.trimIndent())
    }
}
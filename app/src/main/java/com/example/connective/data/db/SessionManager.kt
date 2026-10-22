package com.example.connective.data.db

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("connective_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SAVED_EMAIL = "saved_user_email"
        private const val KEY_JOB_ALERTS = "job_alerts_enabled"
        private const val KEY_REMOTE_WORK = "remote_work_enabled"
    }

    var savedUserEmail: String?
        get() = prefs.getString(KEY_SAVED_EMAIL, null)
        set(value) {
            prefs.edit().putString(KEY_SAVED_EMAIL, value).apply()
        }

    var isJobAlertsEnabled: Boolean
        get() = prefs.getBoolean(KEY_JOB_ALERTS, true)
        set(value) {
            prefs.edit().putBoolean(KEY_JOB_ALERTS, value).apply()
        }

    var isRemoteWorkEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMOTE_WORK, true)
        set(value) {
            prefs.edit().putBoolean(KEY_REMOTE_WORK, value).apply()
        }

    fun clearSession() {
        prefs.edit().remove(KEY_SAVED_EMAIL).apply()
    }
}
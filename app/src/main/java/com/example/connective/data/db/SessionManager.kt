package com.example.connective.data.db

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("connective_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_JOB_ALERTS = "job_alerts_enabled"
        private const val KEY_REMOTE_WORK = "remote_work_enabled"
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
}

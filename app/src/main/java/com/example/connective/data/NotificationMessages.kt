package com.example.connective.data

data class NotificationPayload(
    val title: String,
    val body: String,
    val type: String
)

object NotificationMessages {

    // Triggered when a job seeker submits an application -> Saved in users/{employerId}/notifications
    fun applicationReceived(candidateName: String, jobTitle: String): NotificationPayload {
        return NotificationPayload(
            title = "New Application Received",
            body = "$candidateName has submitted an application for $jobTitle.",
            type = "application"
        )
    }

    // Triggered when employer updates application status -> Saved in users/{seekerId}/notifications
    fun applicationStatusChanged(jobTitle: String, company: String, newStatus: String): NotificationPayload {
        return NotificationPayload(
            title = "Application Status Update",
            body = "Your application status for $jobTitle at $company has been updated to: $newStatus.",
            type = "status_change"
        )
    }

    // Triggered when a new chat message is sent -> Saved in users/{recipientId}/notifications
    fun newMessageReceived(senderName: String, textPreview: String): NotificationPayload {
        val snippet = if (textPreview.length > 50) textPreview.take(47) + "..." else textPreview
        return NotificationPayload(
            title = "New Message from $senderName",
            body = if (snippet.isBlank()) "You received a new message." else snippet,
            type = "message"
        )
    }

    // Triggered when a job listing is closed or removed -> Saved in users/{seekerId}/notifications
    fun jobClosedOrRemoved(jobTitle: String, company: String): NotificationPayload {
        return NotificationPayload(
            title = "Job Listing Status Update",
            body = "The job opportunity '$jobTitle' at $company has been closed or removed.",
            type = "job_update"
        )
    }

    // Triggered on new user registration -> Saved in users/{uid}/notifications
    fun welcomeUser(userName: String): NotificationPayload {
        return NotificationPayload(
            title = "Welcome to Connective!",
            body = "Hello $userName, welcome to Connective! Discover job opportunities matched to your qualifications.",
            type = "welcome"
        )
    }
}

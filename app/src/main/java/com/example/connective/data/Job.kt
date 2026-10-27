package com.example.connective.data

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    @DocumentId val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "seeker", // "seeker" | "employer" | "admin"
    val status: String = "active", // "active" | "suspended"
    val companyName: String = "",
    val title: String = "",
    val location: String = "",
    val phone: String = "",
    val educationLevel: String = "Bachelor's Degree",
    val experienceYears: Int = 0,
    val skills: String = "",
    val bio: String = "",
    val avatarInitials: String = "",
    val fcmToken: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class Job(
    @DocumentId val id: String = "",
    val title: String = "",
    val company: String = "",
    val employerId: String = "",
    val location: String = "",
    val type: String = "Full-time", // "Full-time" | "Part-time" | "Contract"
    val category: String = "IT", // "IT" | "Finance" | "Marketing" | "Design" | "Healthcare" | "Education" | "Engineering" | "Other"
    val salary: String = "",
    val description: String = "",
    val requirements: List<String> = emptyList(),
    val minEducation: String = "Bachelor's Degree",
    val minExperienceYears: Int = 0,
    val status: String = "open", // "open" | "closed" | "removed"
    val createdAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class AttachmentItem(
    val name: String = "",
    val url: String = ""
)

@IgnoreExtraProperties
data class JobApplication(
    @DocumentId val id: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val company: String = "",
    val employerId: String = "",
    val seekerId: String = "",
    val candidateName: String = "",
    val candidateEmail: String = "",
    val candidatePhone: String = "",
    val candidateLocation: String = "",
    val candidateEducation: String = "",
    val candidateExperienceYears: Int = 0,
    val candidateSkills: String = "",
    val coverNote: String = "",
    val attachments: List<AttachmentItem> = emptyList(),
    val status: String = "Applied", // "Applied" | "Shortlisted" | "Rejected" | "Hired"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class Conversation(
    @DocumentId val id: String = "",
    val participantIds: List<String> = emptyList(),
    val participantNames: Map<String, String> = emptyMap(),
    val jobId: String? = null,
    val lastMessage: String = "",
    val lastMessageAt: Long = System.currentTimeMillis(),
    val unreadBy: List<String> = emptyList()
)

@IgnoreExtraProperties
data class ChatMessage(
    @DocumentId val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@IgnoreExtraProperties
data class AppNotification(
    @DocumentId val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "general", // "application" | "status_change" | "message" | "general"
    val relatedId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

sealed class QualificationResult {
    data class Success(val message: String) : QualificationResult()
    data class Ineligible(val reason: String) : QualificationResult()
    data class Error(val message: String) : QualificationResult()
}

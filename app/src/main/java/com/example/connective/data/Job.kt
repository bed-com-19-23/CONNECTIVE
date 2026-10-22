package com.example.connective.data

data class Job(
    val id: Int,
    val title: String,
    val company: String,
    val location: String,
    val type: String,
    val salary: String,
    val description: String = "We are looking for a skilled professional to join our growing team.",
    val requirements: List<String> = listOf(
        "Bachelor's degree in a related field",
        "2+ years of relevant experience",
        "Good communication and teamwork skills"
    ),
    val minEducation: String = "Bachelor's Degree", // "Master's Degree", "Bachelor's Degree", "Diploma", "Certificate", "Secondary School"
    val minExperienceYears: Int = 2,
    val requiredSkill: String = "General",
    val isSaved: Boolean = false
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String, // "seeker" or "employer"
    val companyName: String = "",
    val title: String = "Software Developer",
    val location: String = "Lilongwe, Malawi",
    val phone: String = "+265 999 123 456",
    val educationLevel: String = "Bachelor's Degree",
    val experienceYears: Int = 2,
    val skills: String = "Mobile Development, Git",
    val bio: String = "Motivated professional seeking impactful opportunities.",
    val avatarInitials: String = "SS"
)

data class JobApplication(
    val id: Int,
    val jobId: Int,
    val jobTitle: String,
    val company: String,
    val candidateName: String,
    val candidateEmail: String,
    val candidatePhone: String = "",
    val candidateLocation: String = "",
    val candidateEducation: String = "",
    val candidateExperienceYears: Int = 0,
    val candidateSkills: String = "",
    val coverNote: String = "",
    val attachments: List<String> = emptyList(),
    val appliedDate: String,
    val status: String // "Applied", "Shortlisted", "Rejected", "Hired"
)

data class ChatMessage(
    val id: Int,
    val senderName: String,
    val text: String,
    val mine: Boolean,
    val timestamp: String = "Just now"
)

val sampleJobs get() = ConnectiveRepository.jobs
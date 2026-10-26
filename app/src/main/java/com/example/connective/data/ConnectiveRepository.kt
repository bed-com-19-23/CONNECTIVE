package com.example.connective.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.connective.data.db.ConnectiveDbHelper
import com.example.connective.data.db.SessionManager

sealed class QualificationResult {
    data class Success(val message: String) : QualificationResult()
    data class Ineligible(val reason: String) : QualificationResult()
    data class Error(val message: String) : QualificationResult()
}

object ConnectiveRepository {
    private var dbHelper: ConnectiveDbHelper? = null
    private var sessionManager: SessionManager? = null

    var currentUser by mutableStateOf<User?>(null)

    val registeredUsers = mutableStateListOf(
        User(
            id = 1,
            name = "Shepherd Sadick",
            email = "shepherd@example.com",
            role = "seeker",
            title = "Computer Science Graduate",
            location = "Lilongwe, Malawi",
            phone = "+265 999 123 456",
            educationLevel = "Bachelor's Degree",
            experienceYears = 2,
            skills = "Kotlin, Mobile Development, REST APIs",
            bio = "Motivated computer science graduate with a strong interest in software development.",
            avatarInitials = "SS"
        ),
        User(
            id = 2,
            name = "Sunrise Media",
            email = "employer@example.com",
            role = "employer",
            companyName = "Sunrise Media",
            title = "Hiring Manager",
            location = "Blantyre, Malawi",
            phone = "+265 888 777 666",
            avatarInitials = "SM"
        )
    )

    val jobs = mutableStateListOf(
        Job(
            id = 1,
            title = "Software Developer",
            company = "TechWave Ltd",
            location = "Lilongwe, Malawi",
            type = "Full-time",
            salary = "MWK 600,000 – 1,000,000",
            minEducation = "Bachelor's Degree",
            minExperienceYears = 2,
            description = "Develop high quality Android and web applications for local and international clients.",
            requirements = listOf(
                "Bachelor's degree in Computer Science or Software Engineering",
                "2+ years experience with Kotlin or Java",
                "Experience with REST APIs and Git version control"
            )
        ),
        Job(
            id = 2,
            title = "Marketing Officer",
            company = "Sunrise Media",
            location = "Blantyre, Malawi",
            type = "Full-time",
            salary = "MWK 400,000 – 700,000",
            minEducation = "Diploma",
            minExperienceYears = 1,
            description = "Drive strategic digital marketing campaigns and manage social media branding.",
            requirements = listOf(
                "Diploma or Bachelor's Degree in Marketing or Communications",
                "1+ years experience managing social media accounts",
                "Strong copywriting and visual design awareness"
            )
        ),
        Job(
            id = 3,
            title = "Senior Accountant",
            company = "Global Solutions",
            location = "Lilongwe, Malawi",
            type = "Full-time",
            salary = "MWK 700,000 – 1,200,000",
            minEducation = "Bachelor's Degree",
            minExperienceYears = 3,
            description = "Manage daily financial statements, budget audits and tax compliance.",
            requirements = listOf(
                "Bachelor's degree in Accounting or ACCA qualification",
                "3+ years corporate finance experience",
                "Proficiency in QuickBooks and Excel"
            )
        ),
        Job(
            id = 4,
            title = "UI/UX Designer",
            company = "Innovate MW",
            location = "Mzuzu, Malawi",
            type = "Contract",
            salary = "MWK 500,000 – 750,000",
            minEducation = "Certificate",
            minExperienceYears = 1,
            description = "Create modern user experiences and Figma mockups for digital products.",
            requirements = listOf(
                "Certificate or Diploma in Design or IT",
                "1+ years experience in UI/UX design",
                "Proficiency in Figma and Adobe XD"
            )
        )
    )

    val applications = mutableStateListOf(
        JobApplication(
            id = 1,
            jobId = 1,
            jobTitle = "Software Developer",
            company = "TechWave Ltd",
            candidateName = "Joyce Phiri",
            candidateEmail = "joyce@example.com",
            candidatePhone = "+265 991 223 344",
            candidateLocation = "Lilongwe",
            candidateEducation = "Bachelor's Degree",
            candidateExperienceYears = 3,
            candidateSkills = "Kotlin, Jetpack Compose, Firebase",
            coverNote = "Passionate Android developer ready to build great apps.",
            attachments = listOf("Degree_Certificate.pdf", "Passport_Photo.png"),
            appliedDate = "2 days ago",
            status = "Applied"
        ),
        JobApplication(
            id = 2,
            jobId = 1,
            jobTitle = "Software Developer",
            company = "TechWave Ltd",
            candidateName = "Brian Moyo",
            candidateEmail = "brian@example.com",
            candidatePhone = "+265 888 112 233",
            candidateLocation = "Blantyre",
            candidateEducation = "Bachelor's Degree",
            candidateExperienceYears = 4,
            candidateSkills = "Android, Java, REST APIs",
            coverNote = "Experienced mobile engineer.",
            attachments = listOf("BSc_Computer_Science.pdf", "CV_BrianMoyo.pdf"),
            appliedDate = "3 days ago",
            status = "Shortlisted"
        ),
        JobApplication(
            id = 3,
            jobId = 2,
            jobTitle = "Marketing Officer",
            company = "Sunrise Media",
            candidateName = "Grace Chisomo",
            candidateEmail = "grace@example.com",
            candidatePhone = "+265 993 445 566",
            candidateLocation = "Blantyre",
            candidateEducation = "Diploma",
            candidateExperienceYears = 2,
            candidateSkills = "Digital Marketing, SEO, Social Media",
            coverNote = "Creative digital marketer with proven campaign results.",
            attachments = listOf("Marketing_Diploma.pdf"),
            appliedDate = "4 days ago",
            status = "Shortlisted"
        )
    )

    val messages = mutableStateListOf(
        ChatMessage(1, "Sunrise Media", "Hi, thank you for applying for the position.", false, "10:30 AM"),
        ChatMessage(2, "Applicant", "Thank you! I'm very excited about the opportunity.", true, "10:32 AM"),
        ChatMessage(3, "Sunrise Media", "Would you be available for an interview this week?", false, "10:35 AM"),
        ChatMessage(4, "Applicant", "Yes, I'm available. Please let me know the best time.", true, "10:36 AM")
    )

    fun initialize(context: Context) {
        if (dbHelper != null) return
        val db = ConnectiveDbHelper(context)
        val session = SessionManager(context)
        dbHelper = db
        sessionManager = session

        // Load persisted data from SQLite database
        val dbUsers = db.getAllUsers()
        if (dbUsers.isNotEmpty()) {
            registeredUsers.clear()
            registeredUsers.addAll(dbUsers)
        }

        val dbJobs = db.getAllJobs()
        if (dbJobs.isNotEmpty()) {
            jobs.clear()
            jobs.addAll(dbJobs)
        }

        val dbApps = db.getAllApplications()
        if (dbApps.isNotEmpty()) {
            applications.clear()
            applications.addAll(dbApps)
        }

        val dbMsgs = db.getAllMessages()
        if (dbMsgs.isNotEmpty()) {
            messages.clear()
            messages.addAll(dbMsgs)
        }

        // Restore active user session if previously logged in
        val savedEmail = session.savedUserEmail
        if (!savedEmail.isNullOrBlank()) {
            val user = registeredUsers.find { it.email.equals(savedEmail, ignoreCase = true) }
            if (user != null) {
                currentUser = user
            }
        }
    }

    fun getEducationRank(level: String): Int {
        return when (level.lowercase().trim()) {
            "master's degree", "masters degree", "masters", "master" -> 4
            "bachelor's degree", "bachelors degree", "bachelors", "bachelor", "degree" -> 3
            "diploma" -> 2
            "certificate" -> 1
            else -> 0 // "Secondary School", "None", etc.
        }
    }

    fun loginUser(email: String, name: String = "Shepherd Sadick", role: String = "seeker"): User {
        val existingUser = registeredUsers.find { it.email.equals(email, ignoreCase = true) }
        val user = if (existingUser != null) {
            existingUser
        } else {
            val initials = name.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "US" }

            val newUser = User(
                id = (100..999).random(),
                name = name,
                email = email,
                role = role,
                avatarInitials = initials.uppercase()
            )
            registeredUsers.add(newUser)
            dbHelper?.saveUser(newUser)
            newUser
        }

        currentUser = user
        sessionManager?.savedUserEmail = user.email
        return user
    }

    fun registerUser(name: String, email: String, role: String, companyName: String = ""): User {
        val existingUser = registeredUsers.find { it.email.equals(email, ignoreCase = true) }
        val user = if (existingUser != null) {
            existingUser
        } else {
            val initials = name.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")
                .ifEmpty { "US" }

            val newUser = User(
                id = (100..999).random(),
                name = name,
                email = email,
                role = role,
                companyName = companyName,
                avatarInitials = initials.uppercase()
            )
            registeredUsers.add(newUser)
            dbHelper?.saveUser(newUser)
            newUser
        }

        currentUser = user
        sessionManager?.savedUserEmail = user.email
        return user
    }

    fun logout() {
        currentUser = null
        sessionManager?.clearSession()
    }

    fun updateUserProfileDetails(
        name: String,
        email: String = currentUser?.email ?: "",
        phone: String,
        location: String,
        title: String,
        educationLevel: String,
        experienceYears: Int,
        skills: String,
        bio: String
    ) {
        val user = currentUser ?: return
        val initials = name.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .ifEmpty { "US" }

        val newEmail = email.ifBlank { user.email }

        val updatedUser = user.copy(
            name = name.ifBlank { user.name },
            email = newEmail,
            phone = phone.ifBlank { user.phone },
            location = location.ifBlank { user.location },
            title = title.ifBlank { user.title },
            educationLevel = educationLevel.ifBlank { user.educationLevel },
            experienceYears = experienceYears,
            skills = skills.ifBlank { user.skills },
            bio = bio.ifBlank { user.bio },
            avatarInitials = initials.uppercase()
        )

        currentUser = updatedUser
        sessionManager?.savedUserEmail = newEmail

        val index = registeredUsers.indexOfFirst { it.id == user.id || it.email.equals(user.email, ignoreCase = true) }
        if (index != -1) {
            registeredUsers[index] = updatedUser
        } else {
            registeredUsers.add(updatedUser)
        }

        dbHelper?.saveUser(updatedUser)
    }

    fun deleteUser(userId: Int) {
        registeredUsers.removeAll { it.id == userId }
        dbHelper?.deleteUser(userId)
    }

    fun addJob(
        title: String,
        company: String,
        location: String,
        type: String,
        salary: String,
        minEducation: String = "Bachelor's Degree",
        minExperienceYears: Int = 1,
        description: String = ""
    ): Job {
        val newId = (jobs.maxOfOrNull { it.id } ?: 0) + 1
        val job = Job(
            id = newId,
            title = title.trim(),
            company = company.ifBlank { currentUser?.companyName ?: "Sunrise Media" },
            location = location.trim(),
            type = type,
            salary = salary.ifBlank { "Negotiable" },
            minEducation = minEducation,
            minExperienceYears = minExperienceYears,
            description = description.ifBlank { "Describe the role and responsibilities..." }
        )
        jobs.add(0, job)
        dbHelper?.saveJob(job)
        return job
    }

    fun deleteJob(jobId: Int) {
        jobs.removeAll { it.id == jobId }
        dbHelper?.deleteJob(jobId)
    }

    fun toggleSaveJob(jobId: Int) {
        val index = jobs.indexOfFirst { it.id == jobId }
        if (index != -1) {
            val job = jobs[index]
            val updatedJob = job.copy(isSaved = !job.isSaved)
            jobs[index] = updatedJob
            dbHelper?.saveJob(updatedJob)
        }
    }

    fun applyForJobWithQualification(
        jobId: Int,
        name: String,
        email: String,
        phone: String,
        location: String,
        educationLevel: String,
        experienceYears: Int,
        skills: String,
        coverNote: String,
        attachments: List<String> = emptyList()
    ): QualificationResult {
        val job = jobs.find { it.id == jobId } ?: return QualificationResult.Error("Job not found.")

        val candidateEducationRank = getEducationRank(educationLevel)
        val jobMinEducationRank = getEducationRank(job.minEducation)

        if (candidateEducationRank < jobMinEducationRank) {
            return QualificationResult.Ineligible(
                "Qualification Ineligible: This position requires at least a ${job.minEducation}. Your current level ($educationLevel) does not meet the minimum requirement for this role."
            )
        }

        if (experienceYears < job.minExperienceYears) {
            return QualificationResult.Ineligible(
                "Experience Ineligible: This position requires at least ${job.minExperienceYears} year(s) of experience. You currently selected $experienceYears year(s)."
            )
        }

        val existing = applications.find { it.jobId == jobId && it.candidateEmail == email }
        if (existing != null) {
            return QualificationResult.Ineligible("You have already applied for this position.")
        }

        val newApp = JobApplication(
            id = (applications.maxOfOrNull { it.id } ?: 0) + 1,
            jobId = jobId,
            jobTitle = job.title,
            company = job.company,
            candidateName = name,
            candidateEmail = email,
            candidatePhone = phone,
            candidateLocation = location,
            candidateEducation = educationLevel,
            candidateExperienceYears = experienceYears,
            candidateSkills = skills,
            coverNote = coverNote,
            attachments = attachments,
            appliedDate = "Just now",
            status = "Applied"
        )
        applications.add(0, newApp)
        dbHelper?.saveApplication(newApp)
        return QualificationResult.Success("Application submitted successfully! Your qualifications & attachments have been submitted.")
    }

    fun updateApplicationStatus(appId: Int, newStatus: String) {
        val index = applications.indexOfFirst { it.id == appId }
        if (index != -1) {
            val updatedApp = applications[index].copy(status = newStatus)
            applications[index] = updatedApp
            dbHelper?.saveApplication(updatedApp)
        }
    }

    fun sendMessage(text: String, isMine: Boolean = true, senderOverride: String? = null) {
        if (text.isBlank()) return
        val sender = senderOverride ?: if (isMine) (currentUser?.name ?: "Me") else "HR Support"
        val newMsg = ChatMessage(
            id = (messages.maxOfOrNull { it.id } ?: 0) + 1,
            senderName = sender,
            text = text.trim(),
            mine = isMine,
            timestamp = "Just now"
        )
        messages.add(newMsg)
        dbHelper?.saveMessage(newMsg)
    }

    fun triggerConditionResponse(user: User?) {
        val userName = user?.name ?: "User"
        val userApp = applications.find { it.candidateEmail.equals(user?.email, ignoreCase = true) }
        val company = userApp?.company ?: "Sunrise Media"
        val jobTitle = userApp?.jobTitle ?: "your job application"
        val status = userApp?.status ?: "Applied"

        val responseText = when (status) {
            "Shortlisted" -> "Hello $userName! Your application for $jobTitle at $company is SHORTLISTED. We will contact you soon for an interview."
            "Hired" -> "Congratulations $userName! You have been HIRED for $jobTitle at $company. Welcome to the team!"
            "Rejected" -> "Hello $userName, thank you for following up regarding $jobTitle at $company. Our HR team has noted your update."
            "Applied" -> "Hello $userName! We received your message regarding $jobTitle at $company. Our hiring team is reviewing your qualifications and attachments."
            else -> "Hello $userName! Thank you for contacting Connective HR Support. How can we help you today?"
        }

        val newMsg = ChatMessage(
            id = (messages.maxOfOrNull { it.id } ?: 0) + 1,
            senderName = "$company HR",
            text = responseText,
            mine = false,
            timestamp = "Just now"
        )
        messages.add(newMsg)
        dbHelper?.saveMessage(newMsg)
    }
}

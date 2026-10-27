package com.example.connective.data

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AccountSuspendedException(message: String) : Exception(message)

object ConnectiveRepository {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val storage: FirebaseStorage by lazy { Firebase.storage }

    // Init context if needed for local prefs or notifications
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    // Qualification Rank Helper
    fun getEducationRank(level: String): Int {
        return when (level.lowercase().trim()) {
            "master's degree", "masters degree", "masters", "master" -> 4
            "bachelor's degree", "bachelors degree", "bachelors", "bachelor", "degree" -> 3
            "diploma" -> 2
            "certificate" -> 1
            else -> 0
        }
    }

    private fun getInitials(name: String): String {
        return name.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .take(2)
            .joinToString("")
            .ifEmpty { "US" }
            .uppercase()
    }

    // ----------------------------------------------------------------
    // AUTHENTICATION
    // ----------------------------------------------------------------

    val currentAuthUid: String?
        get() = auth.currentUser?.uid

    val currentUserFlow: Flow<User?> = callbackFlow {
        var listenerRegistration = auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                trySend(null)
            } else {
                val reg = firestore.collection("users").document(uid)
                    .addSnapshotListener { snapshot, _ ->
                        if (snapshot != null && snapshot.exists()) {
                            val user = snapshot.toObject(User::class.java)
                            trySend(user)
                        } else {
                            trySend(null)
                        }
                    }
                // Handle cancellation
            }
        }
        awaitClose {
            // Cleanup auth state listener
        }
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        role: String,
        companyName: String = ""
    ): User {
        val sanitizedRole = role.lowercase().trim()
        if (sanitizedRole == "admin") {
            throw IllegalArgumentException("Self-registration as administrator is not permitted.")
        }
        if (sanitizedRole != "seeker" && sanitizedRole != "employer") {
            throw IllegalArgumentException("Invalid registration role specified.")
        }

        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("Registration failed.")

        val now = System.currentTimeMillis()
        val newUser = User(
            uid = uid,
            name = name,
            email = email,
            role = sanitizedRole,
            status = "active",
            companyName = if (sanitizedRole == "employer") companyName.ifBlank { name } else "",
            avatarInitials = getInitials(name),
            createdAt = now
        )

        firestore.collection("users").document(uid).set(newUser).await()

        // Welcome Notification
        val welcomeNotif = NotificationMessages.welcomeUser(name)
        val notifRef = firestore.collection("users").document(uid).collection("notifications").document()
        val appNotif = AppNotification(
            id = notifRef.id,
            title = welcomeNotif.title,
            body = welcomeNotif.body,
            type = welcomeNotif.type,
            relatedId = uid,
            isRead = false,
            createdAt = now
        )
        firestore.collection("users").document(uid).collection("notifications").document(notifRef.id).set(appNotif).await()

        updateFcmTokenForCurrentUser()
        return newUser
    }

    suspend fun loginUser(email: String, password: String): User {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        val uid = authResult.user?.uid ?: throw Exception("Login failed.")

        val docSnapshot = firestore.collection("users").document(uid).get().await()
        if (!docSnapshot.exists()) {
            auth.signOut()
            throw Exception("User profile document not found.")
        }

        val user = docSnapshot.toObject(User::class.java) ?: throw Exception("Invalid user profile format.")

        if (user.status.equals("suspended", ignoreCase = true)) {
            auth.signOut()
            throw AccountSuspendedException("Your account has been suspended. Please contact support.")
        }

        updateFcmTokenForCurrentUser()
        return user
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun updateFcmTokenForCurrentUser() {
        val uid = currentAuthUid ?: return
        try {
            val token = Firebase.messaging.token.await()
            if (token.isNotBlank()) {
                firestore.collection("users").document(uid).update("fcmToken", token).await()
            }
        } catch (_: Exception) {
            // Non-critical background task
        }
    }

    suspend fun updateFcmToken(token: String) {
        val uid = currentAuthUid ?: return
        try {
            firestore.collection("users").document(uid).update("fcmToken", token).await()
        } catch (_: Exception) {}
    }

    // ----------------------------------------------------------------
    // USER PROFILE
    // ----------------------------------------------------------------

    suspend fun updateUserProfile(user: User) {
        val uid = user.uid.ifBlank { currentAuthUid ?: return }
        val updatedUser = user.copy(
            uid = uid,
            avatarInitials = getInitials(user.name)
        )
        firestore.collection("users").document(uid).set(updatedUser, SetOptions.merge()).await()
    }

    fun searchSeekersFlow(
        query: String = "",
        education: String = "All",
        location: String = "",
        minExperienceYears: Int = 0
    ): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .whereEqualTo("role", "seeker")
            .whereEqualTo("status", "active")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val seekers = snapshot?.toObjects(User::class.java) ?: emptyList()
                val filtered = seekers.filter { u ->
                    val matchesQuery = query.isBlank() ||
                            u.name.contains(query, ignoreCase = true) ||
                            u.skills.contains(query, ignoreCase = true) ||
                            u.title.contains(query, ignoreCase = true)

                    val matchesLocation = location.isBlank() || u.location.contains(location, ignoreCase = true)

                    val matchesEducation = education == "All" ||
                            u.educationLevel.equals(education, ignoreCase = true) ||
                            getEducationRank(u.educationLevel) >= getEducationRank(education)

                    val matchesExp = u.experienceYears >= minExperienceYears

                    matchesQuery && matchesLocation && matchesEducation && matchesExp
                }
                trySend(filtered)
            }
        awaitClose { listener.remove() }
    }

    // ----------------------------------------------------------------
    // JOBS
    // ----------------------------------------------------------------

    fun getOpenJobsFlow(): Flow<List<Job>> = callbackFlow {
        val listener = firestore.collection("jobs")
            .whereEqualTo("status", "open")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val jobs = snapshot?.toObjects(Job::class.java) ?: emptyList()
                val sorted = jobs.sortedByDescending { it.createdAt }
                trySend(sorted)
            }
        awaitClose { listener.remove() }
    }

    fun getEmployerJobsFlow(employerId: String): Flow<List<Job>> = callbackFlow {
        if (employerId.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        val listener = firestore.collection("jobs")
            .whereEqualTo("employerId", employerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val jobs = (snapshot?.toObjects(Job::class.java) ?: emptyList())
                    .filter { it.status != "removed" }
                    .sortedByDescending { it.createdAt }
                trySend(jobs)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getJobById(jobId: String): Job? {
        if (jobId.isBlank()) return null
        val doc = firestore.collection("jobs").document(jobId).get().await()
        return if (doc.exists()) doc.toObject(Job::class.java) else null
    }

    suspend fun addJob(
        title: String,
        company: String,
        employerId: String,
        location: String,
        type: String,
        category: String = "IT",
        salary: String,
        minEducation: String = "Bachelor's Degree",
        minExperienceYears: Int = 1,
        description: String = ""
    ): Job {
        val docRef = firestore.collection("jobs").document()
        val newJob = Job(
            id = docRef.id,
            title = title.trim(),
            company = company.trim(),
            employerId = employerId,
            location = location.trim(),
            type = type,
            category = category,
            salary = salary.trim().ifBlank { "Negotiable" },
            minEducation = minEducation,
            minExperienceYears = minExperienceYears,
            description = description.ifBlank { "Role details and responsibilities..." },
            status = "open",
            createdAt = System.currentTimeMillis()
        )
        docRef.set(newJob).await()
        return newJob
    }

    suspend fun updateJob(job: Job) {
        firestore.collection("jobs").document(job.id).set(job, SetOptions.merge()).await()
    }

    suspend fun closeJob(jobId: String) {
        val job = getJobById(jobId)
        firestore.collection("jobs").document(jobId).update("status", "closed").await()
        if (job != null) {
            notifyApplicantsOfJobChange(job, "closed")
        }
    }

    suspend fun deleteJob(jobId: String) {
        val job = getJobById(jobId)
        firestore.collection("jobs").document(jobId).update("status", "removed").await()
        if (job != null) {
            notifyApplicantsOfJobChange(job, "removed")
        }
    }

    private suspend fun notifyApplicantsOfJobChange(job: Job, action: String) {
        try {
            val appsSnap = firestore.collection("applications")
                .whereEqualTo("jobId", job.id)
                .get().await()

            if (!appsSnap.isEmpty) {
                val batch = firestore.batch()
                val now = System.currentTimeMillis()
                val payload = NotificationMessages.jobClosedOrRemoved(job.title, job.company)

                appsSnap.documents.forEach { doc ->
                    val seekerId = doc.getString("seekerId") ?: ""
                    if (seekerId.isNotBlank()) {
                        val notifRef = firestore.collection("users").document(seekerId)
                            .collection("notifications").document()
                        val notification = AppNotification(
                            id = notifRef.id,
                            title = payload.title,
                            body = "Job '$action': ${payload.body}",
                            type = payload.type,
                            relatedId = job.id,
                            isRead = false,
                            createdAt = now
                        )
                        batch.set(notifRef, notification)
                    }
                }
                batch.commit().await()
            }
        } catch (_: Exception) {}
    }

    // ----------------------------------------------------------------
    // SAVED JOBS (PER USER)
    // ----------------------------------------------------------------

    fun getSavedJobIdsFlow(seekerId: String): Flow<Set<String>> = callbackFlow {
        if (seekerId.isBlank()) {
            trySend(emptySet())
            awaitClose {}
            return@callbackFlow
        }
        val listener = firestore.collection("users").document(seekerId)
            .collection("savedJobs")
            .addSnapshotListener { snapshot, _ ->
                val ids = snapshot?.documents?.map { it.id }?.toSet() ?: emptySet()
                trySend(ids)
            }
        awaitClose { listener.remove() }
    }

    suspend fun toggleSaveJob(seekerId: String, jobId: String, currentlySaved: Boolean) {
        if (seekerId.isBlank() || jobId.isBlank()) return
        val docRef = firestore.collection("users").document(seekerId)
            .collection("savedJobs").document(jobId)

        if (currentlySaved) {
            docRef.delete().await()
        } else {
            docRef.set(mapOf("jobId" to jobId, "savedAt" to System.currentTimeMillis())).await()
        }
    }

    // ----------------------------------------------------------------
    // APPLICATIONS & DOCUMENTS
    // ----------------------------------------------------------------

    suspend fun checkAlreadyApplied(jobId: String, seekerId: String): Boolean {
        if (jobId.isBlank() || seekerId.isBlank()) return false
        val snap = firestore.collection("applications")
            .whereEqualTo("jobId", jobId)
            .whereEqualTo("seekerId", seekerId)
            .get().await()
        return !snap.isEmpty
    }

    suspend fun uploadDocumentFile(
        seekerId: String,
        applicationId: String,
        fileUri: Uri,
        fileName: String,
        onProgress: (Float) -> Unit
    ): AttachmentItem {
        return try {
            val storageRef = storage.reference.child("applications/$seekerId/$applicationId/$fileName")
            val uploadTask = storageRef.putFile(fileUri)

            uploadTask.addOnProgressListener { snapshot ->
                val progress = (snapshot.bytesTransferred.toFloat() / snapshot.totalByteCount.toFloat())
                onProgress(progress)
            }

            uploadTask.await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            AttachmentItem(name = fileName, url = downloadUrl)
        } catch (e: Exception) {
            // Fallback if Storage is disabled or fails: return name and fileUri string
            AttachmentItem(name = fileName, url = fileUri.toString())
        }
    }

    suspend fun submitApplication(
        job: Job,
        seeker: User,
        phone: String,
        location: String,
        educationLevel: String,
        experienceYears: Int,
        skills: String,
        coverNote: String,
        pickedFiles: List<Pair<Uri, String>>,
        onProgress: (Float) -> Unit = {}
    ): QualificationResult {
        // Qualification Eligibility Checks
        val candidateEduRank = getEducationRank(educationLevel)
        val jobMinEduRank = getEducationRank(job.minEducation)

        if (candidateEduRank < jobMinEduRank) {
            return QualificationResult.Ineligible(
                "Qualification Ineligible: This position requires at least a ${job.minEducation}. Your current level ($educationLevel) does not meet the minimum requirement."
            )
        }

        if (experienceYears < job.minExperienceYears) {
            return QualificationResult.Ineligible(
                "Experience Ineligible: This position requires at least ${job.minExperienceYears} year(s) of experience. You entered $experienceYears year(s)."
            )
        }

        if (checkAlreadyApplied(job.id, seeker.uid)) {
            return QualificationResult.Ineligible("You have already applied for this position.")
        }

        val appDocRef = firestore.collection("applications").document()
        val appId = appDocRef.id

        val uploadedAttachments = mutableListOf<AttachmentItem>()
        if (pickedFiles.isNotEmpty()) {
            pickedFiles.forEachIndexed { index, pair ->
                val uri = pair.first
                val name = pair.second
                val item = uploadDocumentFile(seeker.uid, appId, uri, name) { prog ->
                    val totalProgress = (index + prog) / pickedFiles.size
                    onProgress(totalProgress)
                }
                uploadedAttachments.add(item)
            }
        }

        val now = System.currentTimeMillis()
        val newApplication = JobApplication(
            id = appId,
            jobId = job.id,
            jobTitle = job.title,
            company = job.company,
            employerId = job.employerId,
            seekerId = seeker.uid,
            candidateName = seeker.name,
            candidateEmail = seeker.email,
            candidatePhone = phone,
            candidateLocation = location,
            candidateEducation = educationLevel,
            candidateExperienceYears = experienceYears,
            candidateSkills = skills,
            coverNote = coverNote,
            attachments = uploadedAttachments,
            status = "Applied",
            createdAt = now,
            updatedAt = now
        )

        // Batched Write: Application + Notification for Employer
        val batch = firestore.batch()
        batch.set(appDocRef, newApplication)

        val notifPayload = NotificationMessages.applicationReceived(seeker.name, job.title)
        val notifDocRef = firestore.collection("users").document(job.employerId)
            .collection("notifications").document()
        val notification = AppNotification(
            id = notifDocRef.id,
            title = notifPayload.title,
            body = notifPayload.body,
            type = notifPayload.type,
            relatedId = appId,
            isRead = false,
            createdAt = now
        )
        batch.set(notifDocRef, notification)

        batch.commit().await()
        return QualificationResult.Success("Application submitted successfully! Your qualifications and documents have been recorded.")
    }

    fun getSeekerApplicationsFlow(seekerId: String): Flow<List<JobApplication>> = callbackFlow {
        if (seekerId.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        val listener = firestore.collection("applications")
            .whereEqualTo("seekerId", seekerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val apps = (snapshot?.toObjects(JobApplication::class.java) ?: emptyList())
                    .sortedByDescending { it.createdAt }
                trySend(apps)
            }
        awaitClose { listener.remove() }
    }

    fun getEmployerApplicationsFlow(employerId: String): Flow<List<JobApplication>> = callbackFlow {
        if (employerId.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        val listener = firestore.collection("applications")
            .whereEqualTo("employerId", employerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val apps = (snapshot?.toObjects(JobApplication::class.java) ?: emptyList())
                    .sortedByDescending { it.createdAt }
                trySend(apps)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateApplicationStatus(
        applicationId: String,
        newStatus: String,
        seekerId: String,
        jobTitle: String,
        company: String = "Company"
    ) {
        val now = System.currentTimeMillis()
        val batch = firestore.batch()

        val appRef = firestore.collection("applications").document(applicationId)
        batch.update(appRef, mapOf("status" to newStatus, "updatedAt" to now))

        if (seekerId.isNotBlank()) {
            val notifPayload = NotificationMessages.applicationStatusChanged(jobTitle, company, newStatus)
            val notifRef = firestore.collection("users").document(seekerId)
                .collection("notifications").document()
            val notification = AppNotification(
                id = notifRef.id,
                title = notifPayload.title,
                body = notifPayload.body,
                type = notifPayload.type,
                relatedId = applicationId,
                isRead = false,
                createdAt = now
            )
            batch.set(notifRef, notification)
        }

        batch.commit().await()
    }

    // ----------------------------------------------------------------
    // REAL MESSAGING & CONVERSATIONS
    // ----------------------------------------------------------------

    fun getUserConversationsFlow(uid: String): Flow<List<Conversation>> = callbackFlow {
        if (uid.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        val listener = firestore.collection("conversations")
            .whereArrayContains("participantIds", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = (snapshot?.toObjects(Conversation::class.java) ?: emptyList())
                    .sortedByDescending { it.lastMessageAt }
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getConversationMessagesFlow(conversationId: String): Flow<List<ChatMessage>> = callbackFlow {
        if (conversationId.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        val listener = firestore.collection("conversations").document(conversationId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.toObjects(ChatMessage::class.java) ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getOrCreateConversation(
        myUid: String,
        myName: String,
        partnerUid: String,
        partnerName: String,
        jobId: String? = null
    ): String {
        if (myUid.isBlank() || partnerUid.isBlank()) throw IllegalArgumentException("Participant UIDs required")

        // Search if conversation exists
        val snap = firestore.collection("conversations")
            .whereArrayContains("participantIds", myUid)
            .get().await()

        val existing = snap.documents.mapNotNull { it.toObject(Conversation::class.java) }
            .find { it.participantIds.contains(partnerUid) }

        if (existing != null) {
            return existing.id
        }

        // Create new conversation
        val docRef = firestore.collection("conversations").document()
        val now = System.currentTimeMillis()
        val newConv = Conversation(
            id = docRef.id,
            participantIds = listOf(myUid, partnerUid),
            participantNames = mapOf(myUid to myName, partnerUid to partnerName),
            jobId = jobId,
            lastMessage = "Conversation started",
            lastMessageAt = now,
            unreadBy = emptyList()
        )
        docRef.set(newConv).await()
        return docRef.id
    }

    suspend fun sendChatMessage(
        conversationId: String,
        senderId: String,
        senderName: String,
        recipientId: String,
        text: String
    ) {
        if (conversationId.isBlank() || text.isBlank()) return
        val now = System.currentTimeMillis()
        val msgDocRef = firestore.collection("conversations").document(conversationId)
            .collection("messages").document()

        val chatMessage = ChatMessage(
            id = msgDocRef.id,
            conversationId = conversationId,
            senderId = senderId,
            senderName = senderName,
            text = text.trim(),
            createdAt = now
        )

        val batch = firestore.batch()
        batch.set(msgDocRef, chatMessage)

        val convRef = firestore.collection("conversations").document(conversationId)
        batch.update(
            convRef,
            mapOf(
                "lastMessage" to text.trim(),
                "lastMessageAt" to now,
                "unreadBy" to FieldValue.arrayUnion(recipientId)
            )
        )

        // Notification for Recipient
        if (recipientId.isNotBlank()) {
            val notifPayload = NotificationMessages.newMessageReceived(senderName, text)
            val notifRef = firestore.collection("users").document(recipientId)
                .collection("notifications").document()
            val notif = AppNotification(
                id = notifRef.id,
                title = notifPayload.title,
                body = notifPayload.body,
                type = notifPayload.type,
                relatedId = conversationId,
                isRead = false,
                createdAt = now
            )
            batch.set(notifRef, notif)
        }

        batch.commit().await()
    }

    suspend fun markConversationRead(conversationId: String, myUid: String) {
        if (conversationId.isBlank() || myUid.isBlank()) return
        firestore.collection("conversations").document(conversationId)
            .update("unreadBy", FieldValue.arrayRemove(myUid)).await()
    }

    // ----------------------------------------------------------------
    // NOTIFICATIONS
    // ----------------------------------------------------------------

    fun getNotificationsFlow(uid: String): Flow<List<AppNotification>> = callbackFlow {
        if (uid.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        val listener = firestore.collection("users").document(uid)
            .collection("notifications")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = (snapshot?.toObjects(AppNotification::class.java) ?: emptyList())
                    .sortedByDescending { it.createdAt }
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun markNotificationRead(uid: String, notificationId: String) {
        if (uid.isBlank() || notificationId.isBlank()) return
        firestore.collection("users").document(uid)
            .collection("notifications").document(notificationId)
            .update("isRead", true).await()
    }

    suspend fun markAllNotificationsRead(uid: String) {
        if (uid.isBlank()) return
        val snap = firestore.collection("users").document(uid)
            .collection("notifications")
            .whereEqualTo("isRead", false)
            .get().await()

        if (!snap.isEmpty) {
            val batch = firestore.batch()
            snap.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()
        }
    }

    // ----------------------------------------------------------------
    // ADMIN FUNCTIONS
    // ----------------------------------------------------------------

    fun getAllUsersFlow(): Flow<List<User>> = callbackFlow {
        val listener = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val users = (snapshot?.toObjects(User::class.java) ?: emptyList())
                    .sortedByDescending { it.createdAt }
                trySend(users)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateUserStatusByAdmin(uid: String, newStatus: String) {
        firestore.collection("users").document(uid).update("status", newStatus).await()
    }

    suspend fun deleteUserByAdmin(uid: String) {
        firestore.collection("users").document(uid).delete().await()
    }

    fun getAllJobsForAdminFlow(): Flow<List<Job>> = callbackFlow {
        val listener = firestore.collection("jobs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val jobs = (snapshot?.toObjects(Job::class.java) ?: emptyList())
                    .filter { it.status != "removed" }
                    .sortedByDescending { it.createdAt }
                trySend(jobs)
            }
        awaitClose { listener.remove() }
    }

    suspend fun deleteAnyJobByAdmin(jobId: String) {
        firestore.collection("jobs").document(jobId).update("status", "removed").await()
    }

    fun getPlatformMetricsFlow(): Flow<Triple<Int, Int, Int>> = callbackFlow {
        var userCount = 0
        var jobCount = 0
        var appCount = 0

        fun emitMetrics() {
            trySend(Triple(userCount, jobCount, appCount))
        }

        val regUsers = firestore.collection("users").addSnapshotListener { s, _ ->
            userCount = s?.size() ?: 0
            emitMetrics()
        }
        val regJobs = firestore.collection("jobs").whereEqualTo("status", "open").addSnapshotListener { s, _ ->
            jobCount = s?.size() ?: 0
            emitMetrics()
        }
        val regApps = firestore.collection("applications").addSnapshotListener { s, _ ->
            appCount = s?.size() ?: 0
            emitMetrics()
        }

        awaitClose {
            regUsers.remove()
            regJobs.remove()
            regApps.remove()
        }
    }
}

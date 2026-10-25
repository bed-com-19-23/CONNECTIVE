package com.example.connective.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.connective.data.ChatMessage
import com.example.connective.data.Job
import com.example.connective.data.JobApplication
import com.example.connective.data.User

class ConnectiveDbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "connective.db"
        const val DATABASE_VERSION = 1

        // Table names
        const val TABLE_USERS = "users"
        const val TABLE_JOBS = "jobs"
        const val TABLE_APPLICATIONS = "applications"
        const val TABLE_MESSAGES = "messages"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                role TEXT NOT NULL,
                companyName TEXT,
                title TEXT,
                location TEXT,
                phone TEXT,
                educationLevel TEXT,
                experienceYears INTEGER,
                skills TEXT,
                bio TEXT,
                avatarInitials TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_JOBS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                company TEXT NOT NULL,
                location TEXT NOT NULL,
                type TEXT NOT NULL,
                salary TEXT NOT NULL,
                minEducation TEXT NOT NULL,
                minExperienceYears INTEGER NOT NULL,
                description TEXT NOT NULL,
                requirements TEXT NOT NULL,
                isSaved INTEGER DEFAULT 0
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_APPLICATIONS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                jobId INTEGER NOT NULL,
                jobTitle TEXT NOT NULL,
                company TEXT NOT NULL,
                candidateName TEXT NOT NULL,
                candidateEmail TEXT NOT NULL,
                candidatePhone TEXT,
                candidateLocation TEXT,
                candidateEducation TEXT,
                candidateExperienceYears INTEGER,
                candidateSkills TEXT,
                coverNote TEXT,
                attachments TEXT,
                appliedDate TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_MESSAGES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                senderName TEXT NOT NULL,
                text TEXT NOT NULL,
                mine INTEGER NOT NULL,
                timestamp TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Seed initial data
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_JOBS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_APPLICATIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        onCreate(db)
    }

    private fun seedInitialData(db: SQLiteDatabase) {
        // Initial Users
        insertUserRaw(db, User(1, "Shepherd Sadick", "shepherd@example.com", "seeker", "", "Computer Science Graduate", "Lilongwe, Malawi", "+265 999 123 456", "Bachelor's Degree", 2, "Kotlin, Mobile Development", "Motivated CS graduate.", "SS"))
        insertUserRaw(db, User(2, "Sunrise Media", "employer@example.com", "employer", "Sunrise Media", "Hiring Manager", "Blantyre, Malawi", "+265 888 777 666", "Bachelor's Degree", 5, "Management, Hiring", "Leading media house in Blantyre.", "SM"))

        // Initial Jobs
        insertJobRaw(db, Job(1, "Software Developer", "TechWave Ltd", "Lilongwe, Malawi", "Full-time", "MWK 600,000 – 1,000,000", "Develop high quality Android and web applications.", listOf("Bachelor's degree in Computer Science", "2+ years experience with Kotlin", "REST APIs and Git"), "Bachelor's Degree", 2))
        insertJobRaw(db, Job(2, "Marketing Officer", "Sunrise Media", "Blantyre, Malawi", "Full-time", "MWK 400,000 – 700,000", "Drive strategic digital marketing campaigns.", listOf("Diploma or Degree in Marketing", "1+ years experience", "Copywriting skills"), "Diploma", 1))
        insertJobRaw(db, Job(3, "Senior Accountant", "Global Solutions", "Lilongwe, Malawi", "Full-time", "MWK 700,000 – 1,200,000", "Manage daily financial statements and tax compliance.", listOf("Bachelor's degree in Accounting or ACCA", "3+ years corporate finance exp"), "Bachelor's Degree", 3))
        insertJobRaw(db, Job(4, "UI/UX Designer", "Innovate MW", "Mzuzu, Malawi", "Contract", "MWK 500,000 – 750,000", "Create modern user experiences and Figma mockups.", listOf("Certificate or Diploma in Design", "1+ years Figma experience"), "Certificate", 1))

        // Initial Applications
        insertApplicationRaw(db, JobApplication(1, 1, "Software Developer", "TechWave Ltd", "Joyce Phiri", "joyce@example.com", "+265 991 223 344", "Lilongwe", "Bachelor's Degree", 3, "Kotlin, Jetpack Compose", "Passionate Android developer.", listOf("Degree_Certificate.pdf", "Passport_Photo.png"), "2 days ago", "Applied"))
        insertApplicationRaw(db, JobApplication(2, 1, "Software Developer", "TechWave Ltd", "Brian Moyo", "brian@example.com", "+265 888 112 233", "Blantyre", "Bachelor's Degree", 4, "Android, Java", "Experienced mobile engineer.", listOf("BSc_Computer_Science.pdf", "CV_BrianMoyo.pdf"), "3 days ago", "Shortlisted"))
        insertApplicationRaw(db, JobApplication(3, 2, "Marketing Officer", "Sunrise Media", "Grace Chisomo", "grace@example.com", "+265 993 445 566", "Blantyre", "Diploma", 2, "Digital Marketing, SEO", "Creative digital marketer.", listOf("Marketing_Diploma.pdf"), "4 days ago", "Shortlisted"))

        // Initial Messages
        insertMessageRaw(db, ChatMessage(1, "Sunrise Media", "Hi, thank you for applying for the position.", false, "10:30 AM"))
        insertMessageRaw(db, ChatMessage(2, "Applicant", "Thank you! I'm very excited about the opportunity.", true, "10:32 AM"))
    }

    private fun insertUserRaw(db: SQLiteDatabase, user: User) {
        val values = ContentValues().apply {
            put("id", user.id)
            put("name", user.name)
            put("email", user.email)
            put("role", user.role)
            put("companyName", user.companyName)
            put("title", user.title)
            put("location", user.location)
            put("phone", user.phone)
            put("educationLevel", user.educationLevel)
            put("experienceYears", user.experienceYears)
            put("skills", user.skills)
            put("bio", user.bio)
            put("avatarInitials", user.avatarInitials)
        }
        db.insertWithOnConflict(TABLE_USERS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertJobRaw(db: SQLiteDatabase, job: Job) {
        val values = ContentValues().apply {
            put("id", job.id)
            put("title", job.title)
            put("company", job.company)
            put("location", job.location)
            put("type", job.type)
            put("salary", job.salary)
            put("minEducation", job.minEducation)
            put("minExperienceYears", job.minExperienceYears)
            put("description", job.description)
            put("requirements", job.requirements.joinToString("||"))
            put("isSaved", if (job.isSaved) 1 else 0)
        }
        db.insertWithOnConflict(TABLE_JOBS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertApplicationRaw(db: SQLiteDatabase, app: JobApplication) {
        val values = ContentValues().apply {
            put("id", app.id)
            put("jobId", app.jobId)
            put("jobTitle", app.jobTitle)
            put("company", app.company)
            put("candidateName", app.candidateName)
            put("candidateEmail", app.candidateEmail)
            put("candidatePhone", app.candidatePhone)
            put("candidateLocation", app.candidateLocation)
            put("candidateEducation", app.candidateEducation)
            put("candidateExperienceYears", app.candidateExperienceYears)
            put("candidateSkills", app.candidateSkills)
            put("coverNote", app.coverNote)
            put("attachments", app.attachments.joinToString("||"))
            put("appliedDate", app.appliedDate)
            put("status", app.status)
        }
        db.insertWithOnConflict(TABLE_APPLICATIONS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    private fun insertMessageRaw(db: SQLiteDatabase, msg: ChatMessage) {
        val values = ContentValues().apply {
            put("id", msg.id)
            put("senderName", msg.senderName)
            put("text", msg.text)
            put("mine", if (msg.mine) 1 else 0)
            put("timestamp", msg.timestamp)
        }
        db.insertWithOnConflict(TABLE_MESSAGES, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    // Public DAO Query & Insert Methods
    fun getAllUsers(): List<User> {
        val list = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.query(TABLE_USERS, null, null, null, null, null, "id ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    User(
                        id = c.getInt(c.getColumnIndexOrThrow("id")),
                        name = c.getString(c.getColumnIndexOrThrow("name")),
                        email = c.getString(c.getColumnIndexOrThrow("email")),
                        role = c.getString(c.getColumnIndexOrThrow("role")),
                        companyName = c.getString(c.getColumnIndexOrThrow("companyName")) ?: "",
                        title = c.getString(c.getColumnIndexOrThrow("title")) ?: "",
                        location = c.getString(c.getColumnIndexOrThrow("location")) ?: "",
                        phone = c.getString(c.getColumnIndexOrThrow("phone")) ?: "",
                        educationLevel = c.getString(c.getColumnIndexOrThrow("educationLevel")) ?: "Bachelor's Degree",
                        experienceYears = c.getInt(c.getColumnIndexOrThrow("experienceYears")),
                        skills = c.getString(c.getColumnIndexOrThrow("skills")) ?: "",
                        bio = c.getString(c.getColumnIndexOrThrow("bio")) ?: "",
                        avatarInitials = c.getString(c.getColumnIndexOrThrow("avatarInitials")) ?: "US"
                    )
                )
            }
        }
        return list
    }

    fun saveUser(user: User) {
        val db = writableDatabase
        insertUserRaw(db, user)
    }

    fun getAllJobs(): List<Job> {
        val list = mutableListOf<Job>()
        val db = readableDatabase
        val cursor = db.query(TABLE_JOBS, null, null, null, null, null, "id DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                val reqStr = c.getString(c.getColumnIndexOrThrow("requirements")) ?: ""
                val reqList = if (reqStr.isNotBlank()) reqStr.split("||") else emptyList()
                list.add(
                    Job(
                        id = c.getInt(c.getColumnIndexOrThrow("id")),
                        title = c.getString(c.getColumnIndexOrThrow("title")),
                        company = c.getString(c.getColumnIndexOrThrow("company")),
                        location = c.getString(c.getColumnIndexOrThrow("location")),
                        type = c.getString(c.getColumnIndexOrThrow("type")),
                        salary = c.getString(c.getColumnIndexOrThrow("salary")),
                        description = c.getString(c.getColumnIndexOrThrow("description")),
                        requirements = reqList,
                        minEducation = c.getString(c.getColumnIndexOrThrow("minEducation")),
                        minExperienceYears = c.getInt(c.getColumnIndexOrThrow("minExperienceYears")),
                        isSaved = c.getInt(c.getColumnIndexOrThrow("isSaved")) == 1
                    )
                )
            }
        }
        return list
    }

    fun saveJob(job: Job) {
        val db = writableDatabase
        insertJobRaw(db, job)
    }

    fun deleteJob(jobId: Int) {
        val db = writableDatabase
        db.delete(TABLE_JOBS, "id = ?", arrayOf(jobId.toString()))
    }

    fun getAllApplications(): List<JobApplication> {
        val list = mutableListOf<JobApplication>()
        val db = readableDatabase
        val cursor = db.query(TABLE_APPLICATIONS, null, null, null, null, null, "id DESC")
        cursor.use { c ->
            while (c.moveToNext()) {
                val attStr = c.getString(c.getColumnIndexOrThrow("attachments")) ?: ""
                val attList = if (attStr.isNotBlank()) attStr.split("||") else emptyList()
                list.add(
                    JobApplication(
                        id = c.getInt(c.getColumnIndexOrThrow("id")),
                        jobId = c.getInt(c.getColumnIndexOrThrow("jobId")),
                        jobTitle = c.getString(c.getColumnIndexOrThrow("jobTitle")),
                        company = c.getString(c.getColumnIndexOrThrow("company")),
                        candidateName = c.getString(c.getColumnIndexOrThrow("candidateName")),
                        candidateEmail = c.getString(c.getColumnIndexOrThrow("candidateEmail")),
                        candidatePhone = c.getString(c.getColumnIndexOrThrow("candidatePhone")) ?: "",
                        candidateLocation = c.getString(c.getColumnIndexOrThrow("candidateLocation")) ?: "",
                        candidateEducation = c.getString(c.getColumnIndexOrThrow("candidateEducation")) ?: "",
                        candidateExperienceYears = c.getInt(c.getColumnIndexOrThrow("candidateExperienceYears")),
                        candidateSkills = c.getString(c.getColumnIndexOrThrow("candidateSkills")) ?: "",
                        coverNote = c.getString(c.getColumnIndexOrThrow("coverNote")) ?: "",
                        attachments = attList,
                        appliedDate = c.getString(c.getColumnIndexOrThrow("appliedDate")),
                        status = c.getString(c.getColumnIndexOrThrow("status"))
                    )
                )
            }
        }
        return list
    }

    fun saveApplication(app: JobApplication) {
        val db = writableDatabase
        insertApplicationRaw(db, app)
    }

    fun getAllMessages(): List<ChatMessage> {
        val list = mutableListOf<ChatMessage>()
        val db = readableDatabase
        val cursor = db.query(TABLE_MESSAGES, null, null, null, null, null, "id ASC")
        cursor.use { c ->
            while (c.moveToNext()) {
                list.add(
                    ChatMessage(
                        id = c.getInt(c.getColumnIndexOrThrow("id")),
                        senderName = c.getString(c.getColumnIndexOrThrow("senderName")),
                        text = c.getString(c.getColumnIndexOrThrow("text")),
                        mine = c.getInt(c.getColumnIndexOrThrow("mine")) == 1,
                        timestamp = c.getString(c.getColumnIndexOrThrow("timestamp"))
                    )
                )
            }
        }
        return list
    }

    fun saveMessage(msg: ChatMessage) {
        val db = writableDatabase
        insertMessageRaw(db, msg)
    }
}

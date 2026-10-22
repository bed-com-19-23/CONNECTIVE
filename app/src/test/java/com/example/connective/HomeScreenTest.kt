package com.example.connective

import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.QualificationResult
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class HomeScreenTest {

    @Before
    fun setUp() {
        // Reset repository state before each test
        ConnectiveRepository.currentUser = null
    }

    @Test
    fun testInitialJobsListNotEmpty() {
        val jobs = ConnectiveRepository.jobs
        assertTrue("Jobs list should contain initial featured jobs", jobs.isNotEmpty())
        assertTrue("Jobs list should contain Software Developer job", jobs.any { it.title.contains("Software", ignoreCase = true) })
    }

    @Test
    fun testSearchFilteringByKeyword() {
        val query = "Developer"
        val results = ConnectiveRepository.jobs.filter { job ->
            job.title.contains(query, ignoreCase = true) ||
                    job.company.contains(query, ignoreCase = true) ||
                    job.location.contains(query, ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("Software Developer", results.first().title)
    }

    @Test
    fun testSearchFilteringByLocation() {
        val query = "Blantyre"
        val results = ConnectiveRepository.jobs.filter { job ->
            job.location.contains(query, ignoreCase = true)
        }
        assertTrue("Should find jobs located in Blantyre", results.isNotEmpty())
        assertEquals("Sunrise Media", results.first().company)
    }

    @Test
    fun testQualificationRankOrdering() {
        val masterRank = ConnectiveRepository.getEducationRank("Master's Degree")
        val bachelorRank = ConnectiveRepository.getEducationRank("Bachelor's Degree")
        val diplomaRank = ConnectiveRepository.getEducationRank("Diploma")
        val certRank = ConnectiveRepository.getEducationRank("Certificate")
        val secondaryRank = ConnectiveRepository.getEducationRank("Secondary School")

        assertTrue("Master's should outrank Bachelor's", masterRank > bachelorRank)
        assertTrue("Bachelor's should outrank Diploma", bachelorRank > diplomaRank)
        assertTrue("Diploma should outrank Certificate", diplomaRank > certRank)
        assertTrue("Certificate should outrank Secondary School", certRank > secondaryRank)
    }

    @Test
    fun testQualifiedCandidateCanApply() {
        // Apply for Software Developer (requires Bachelor's Degree & 2 yrs exp)
        val result = ConnectiveRepository.applyForJobWithQualification(
            jobId = 1,
            name = "Test Candidate",
            email = "test.candidate@example.com",
            phone = "+265 999 000 111",
            location = "Lilongwe",
            educationLevel = "Bachelor's Degree",
            experienceYears = 3,
            skills = "Kotlin, Android",
            coverNote = "Ready to contribute."
        )

        assertTrue("Qualified candidate application should succeed", result is QualificationResult.Success)
        val successMsg = (result as QualificationResult.Success).message
        assertTrue(successMsg.contains("submitted successfully", ignoreCase = true))
    }

    @Test
    fun testUnqualifiedCandidateIsBlockedFromApplying() {
        // Apply for Senior Accountant (requires Bachelor's Degree) with Secondary School
        val result = ConnectiveRepository.applyForJobWithQualification(
            jobId = 3,
            name = "Unqualified Candidate",
            email = "unqualified@example.com",
            phone = "+265 888 111 222",
            location = "Blantyre",
            educationLevel = "Secondary School",
            experienceYears = 1,
            skills = "Basic Math",
            coverNote = "Eager to learn."
        )

        assertTrue("Unqualified candidate should be marked ineligible", result is QualificationResult.Ineligible)
        val reason = (result as QualificationResult.Ineligible).reason
        assertTrue("Reason should mention qualification requirement", reason.contains("Ineligible", ignoreCase = true))
    }

    @Test
    fun testCandidateWithInsufficientExperienceIsBlocked() {
        // Apply for Senior Accountant (requires 3 years experience) with only 1 year experience
        val result = ConnectiveRepository.applyForJobWithQualification(
            jobId = 3,
            name = "Junior Candidate",
            email = "junior@example.com",
            phone = "+265 993 333 444",
            location = "Lilongwe",
            educationLevel = "Bachelor's Degree",
            experienceYears = 1,
            skills = "Accounting",
            coverNote = "Looking for growth."
        )

        assertTrue("Candidate with insufficient experience should be ineligible", result is QualificationResult.Ineligible)
        val reason = (result as QualificationResult.Ineligible).reason
        assertTrue(reason.contains("Experience Ineligible", ignoreCase = true))
    }

    @Test
    fun testToggleSaveJobBookmark() {
        val jobId = 1
        val initialStatus = ConnectiveRepository.jobs.find { it.id == jobId }?.isSaved ?: false
        ConnectiveRepository.toggleSaveJob(jobId)
        val newStatus = ConnectiveRepository.jobs.find { it.id == jobId }?.isSaved ?: false

        assertNotEquals("Save status should flip when toggled", initialStatus, newStatus)
    }

    @Test
    fun testEmployerPostNewJob() {
        val initialCount = ConnectiveRepository.jobs.size
        ConnectiveRepository.addJob(
            title = "Data Analyst",
            company = "TechWave Ltd",
            location = "Lilongwe, Malawi",
            type = "Full-time",
            salary = "MWK 800,000",
            minEducation = "Bachelor's Degree",
            minExperienceYears = 2,
            description = "Analyze large datasets and generate actionable insights."
        )

        assertEquals("Jobs count should increase by 1 after posting", initialCount + 1, ConnectiveRepository.jobs.size)
        assertEquals("Data Analyst", ConnectiveRepository.jobs.first().title)
    }

    @Test
    fun testUpdateApplicationStatusByEmployer() {
        val appId = 1
        ConnectiveRepository.updateApplicationStatus(appId, "Shortlisted")
        val updatedApp = ConnectiveRepository.applications.find { it.id == appId }

        assertNotNull("Application should exist", updatedApp)
        assertEquals("Shortlisted", updatedApp?.status)
    }
}
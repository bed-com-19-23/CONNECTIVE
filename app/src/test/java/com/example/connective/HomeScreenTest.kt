package com.example.connective

import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.Job
import com.example.connective.data.TimeUtils
import org.junit.Assert.*
import org.junit.Test

class HomeScreenTest {

    @Test
    fun testEducationRankOrdering() {
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
    fun testEducationQualificationValidation() {
        val candidateEduLevel = "Diploma"
        val jobMinEduLevel = "Bachelor's Degree"

        val candidateRank = ConnectiveRepository.getEducationRank(candidateEduLevel)
        val jobMinRank = ConnectiveRepository.getEducationRank(jobMinEduLevel)

        assertTrue("Candidate with Diploma should be ineligible for job requiring Bachelor's", candidateRank < jobMinRank)
    }

    @Test
    fun testExperienceYearsValidation() {
        val candidateExp = 1
        val jobMinExp = 3

        assertTrue("Candidate with 1 year experience should be ineligible for job requiring 3 years", candidateExp < jobMinExp)
    }

    @Test
    fun testRelativeTimeFormatting() {
        val now = System.currentTimeMillis()
        val justNow = TimeUtils.formatRelativeTime(now)
        val fiveMinsAgo = TimeUtils.formatRelativeTime(now - (5 * 60 * 1000))
        val twoHoursAgo = TimeUtils.formatRelativeTime(now - (2 * 3600 * 1000))

        assertEquals("Just now", justNow)
        assertEquals("5m ago", fiveMinsAgo)
        assertEquals("2h ago", twoHoursAgo)
    }

    @Test
    fun testPasswordValidationRules() {
        val validPass = "password123"
        val shortPass = "1234567"

        assertTrue("Password with length >= 8 should be valid", validPass.length >= 8)
        assertFalse("Password with length < 8 should be invalid", shortPass.length >= 8)
    }

    @Test
    fun testCategoryFilteringLogic() {
        val jobsList = listOf(
            Job(id = "1", title = "Software Dev", category = "IT"),
            Job(id = "2", title = "Accountant", category = "Finance"),
            Job(id = "3", title = "Graphic Designer", category = "Design")
        )

        val itJobs = jobsList.filter { it.category.equals("IT", ignoreCase = true) }
        assertEquals(1, itJobs.size)
        assertEquals("Software Dev", itJobs.first().title)
    }
}

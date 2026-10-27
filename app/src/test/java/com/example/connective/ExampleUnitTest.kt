package com.example.connective

import com.example.connective.data.ConnectiveRepository
import org.junit.Test
import org.junit.Assert.*

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testEducationRankCalculations() {
        val master = ConnectiveRepository.getEducationRank("Master's Degree")
        val bachelor = ConnectiveRepository.getEducationRank("Bachelor's Degree")
        val diploma = ConnectiveRepository.getEducationRank("Diploma")
        val cert = ConnectiveRepository.getEducationRank("Certificate")
        val sec = ConnectiveRepository.getEducationRank("Secondary School")

        assertEquals(4, master)
        assertEquals(3, bachelor)
        assertEquals(2, diploma)
        assertEquals(1, cert)
        assertEquals(0, sec)
    }
}

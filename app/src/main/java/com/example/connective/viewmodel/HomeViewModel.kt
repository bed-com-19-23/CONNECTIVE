package com.example.connective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.Job
import com.example.connective.data.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedFilter = MutableStateFlow("All") // "All", "My Qualification", "Bachelor's", "Diploma", "Certificate", "Full-time"
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    val openJobs: StateFlow<List<Job>> = ConnectiveRepository.getOpenJobsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedJobIds: StateFlow<Set<String>> = ConnectiveRepository.currentUserFlow
        .flatMapLatest { user ->
            if (user != null) ConnectiveRepository.getSavedJobIdsFlow(user.uid)
            else flowOf(emptySet())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val filteredJobs: StateFlow<List<Job>> = combine(
        openJobs,
        searchQuery,
        selectedCategory,
        selectedFilter,
        ConnectiveRepository.currentUserFlow
    ) { jobs, query, category, filter, currentUser ->
        jobs.filter { job ->
            val matchesQuery = query.isBlank() ||
                    job.title.contains(query, ignoreCase = true) ||
                    job.company.contains(query, ignoreCase = true) ||
                    job.location.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || job.category.equals(category, ignoreCase = true)

            val matchesFilter = when (filter) {
                "All" -> true
                "My Qualification" -> {
                    if (currentUser != null) {
                        val userRank = ConnectiveRepository.getEducationRank(currentUser.educationLevel)
                        val jobRank = ConnectiveRepository.getEducationRank(job.minEducation)
                        userRank >= jobRank
                    } else true
                }
                "Bachelor's" -> job.minEducation.contains("Bachelor", ignoreCase = true)
                "Diploma" -> job.minEducation.contains("Diploma", ignoreCase = true)
                "Certificate" -> job.minEducation.contains("Certificate", ignoreCase = true)
                "Full-time" -> job.type.equals("Full-time", ignoreCase = true)
                else -> true
            }

            matchesQuery && matchesCategory && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun toggleSaveJob(jobId: String, seeker: User?) {
        if (seeker == null || jobId.isBlank()) return
        val currentlySaved = savedJobIds.value.contains(jobId)
        viewModelScope.launch {
            ConnectiveRepository.toggleSaveJob(seeker.uid, jobId, currentlySaved)
        }
    }
}

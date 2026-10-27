package com.example.connective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.JobApplication
import com.example.connective.data.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CandidatesViewModel : ViewModel() {

    private val _searchMode = MutableStateFlow("Applications") // "Applications" | "Find Candidates"
    val searchMode: StateFlow<String> = _searchMode.asStateFlow()

    // Filter for applications
    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    // Filters for Find Candidates Mode
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _educationFilter = MutableStateFlow("All")
    val educationFilter: StateFlow<String> = _educationFilter.asStateFlow()

    private val _locationFilter = MutableStateFlow("")
    val locationFilter: StateFlow<String> = _locationFilter.asStateFlow()

    private val _minExperienceYears = MutableStateFlow(0)
    val minExperienceYears: StateFlow<Int> = _minExperienceYears.asStateFlow()

    // Employer applications
    val employerApplications: StateFlow<List<JobApplication>> = ConnectiveRepository.currentUserFlow
        .flatMapLatest { user ->
            if (user != null && (user.role == "employer" || user.role == "admin")) {
                ConnectiveRepository.getEmployerApplicationsFlow(user.uid)
            } else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredApplications: StateFlow<List<JobApplication>> = combine(
        employerApplications,
        _statusFilter,
        _searchQuery
    ) { apps, status, query ->
        apps.filter { app ->
            val matchesStatus = status == "All" || app.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    app.candidateName.contains(query, ignoreCase = true) ||
                    app.jobTitle.contains(query, ignoreCase = true) ||
                    app.candidateSkills.contains(query, ignoreCase = true)
            matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Seeker Candidates Search Results Flow
    val searchedSeekers: StateFlow<List<User>> = combine(
        _searchQuery,
        _educationFilter,
        _locationFilter,
        _minExperienceYears
    ) { q, edu, loc, minExp ->
        Triple(q, edu, Pair(loc, minExp))
    }.flatMapLatest { (q, edu, pair) ->
        ConnectiveRepository.searchSeekersFlow(
            query = q,
            education = edu,
            location = pair.first,
            minExperienceYears = pair.second
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchMode(mode: String) {
        _searchMode.value = mode
    }

    fun setStatusFilter(filter: String) {
        _statusFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setEducationFilter(edu: String) {
        _educationFilter.value = edu
    }

    fun setLocationFilter(loc: String) {
        _locationFilter.value = loc
    }

    fun setMinExperienceYears(exp: Int) {
        _minExperienceYears.value = exp
    }

    fun updateStatus(application: JobApplication, newStatus: String) {
        viewModelScope.launch {
            try {
                ConnectiveRepository.updateApplicationStatus(
                    applicationId = application.id,
                    newStatus = newStatus,
                    seekerId = application.seekerId,
                    jobTitle = application.jobTitle,
                    company = application.company
                )
            } catch (_: Exception) {}
        }
    }
}

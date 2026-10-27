package com.example.connective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.JobApplication
import com.example.connective.data.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class ApplicationsViewModel : ViewModel() {

    private val _filter = MutableStateFlow("All") // "All", "Applied", "Shortlisted", "Rejected", "Hired"
    val filter: StateFlow<String> = _filter.asStateFlow()

    val applications: StateFlow<List<JobApplication>> = ConnectiveRepository.currentUserFlow
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList())
            else if (user.role == "employer") ConnectiveRepository.getEmployerApplicationsFlow(user.uid)
            else ConnectiveRepository.getSeekerApplicationsFlow(user.uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredApplications: StateFlow<List<JobApplication>> = combine(
        applications,
        _filter
    ) { apps, currentFilter ->
        if (currentFilter == "All") apps
        else apps.filter { it.status.equals(currentFilter, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(newFilter: String) {
        _filter.value = newFilter
    }
}

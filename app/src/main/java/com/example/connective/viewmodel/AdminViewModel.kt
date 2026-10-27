package com.example.connective.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.Job
import com.example.connective.data.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val _adminTab = MutableStateFlow("Users") // "Users" | "Jobs" | "Reports"
    val adminTab: StateFlow<String> = _adminTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val allUsers: StateFlow<List<User>> = ConnectiveRepository.getAllUsersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredUsers: StateFlow<List<User>> = combine(
        allUsers,
        _searchQuery
    ) { users, query ->
        if (query.isBlank()) users
        else users.filter { u ->
            u.name.contains(query, ignoreCase = true) ||
                    u.email.contains(query, ignoreCase = true) ||
                    u.role.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allJobs: StateFlow<List<Job>> = ConnectiveRepository.getAllJobsForAdminFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredJobs: StateFlow<List<Job>> = combine(
        allJobs,
        _searchQuery
    ) { jobs, query ->
        if (query.isBlank()) jobs
        else jobs.filter { j ->
            j.title.contains(query, ignoreCase = true) ||
                    j.company.contains(query, ignoreCase = true) ||
                    j.location.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platformMetrics: StateFlow<Triple<Int, Int, Int>> = ConnectiveRepository.getPlatformMetricsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Triple(0, 0, 0))

    fun setAdminTab(tab: String) {
        _adminTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleUserStatus(user: User) {
        val newStatus = if (user.status.equals("active", ignoreCase = true)) "suspended" else "active"
        viewModelScope.launch {
            try {
                ConnectiveRepository.updateUserStatusByAdmin(user.uid, newStatus)
            } catch (_: Exception) {}
        }
    }

    fun deleteUser(uid: String) {
        viewModelScope.launch {
            try {
                ConnectiveRepository.deleteUserByAdmin(uid)
            } catch (_: Exception) {}
        }
    }

    fun deleteJob(jobId: String) {
        viewModelScope.launch {
            try {
                ConnectiveRepository.deleteAnyJobByAdmin(jobId)
            } catch (_: Exception) {}
        }
    }
}

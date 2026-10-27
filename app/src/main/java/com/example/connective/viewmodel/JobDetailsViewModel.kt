package com.example.connective.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.connective.data.ConnectiveRepository
import com.example.connective.data.Job
import com.example.connective.data.QualificationResult
import com.example.connective.data.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JobDetailsViewModel : ViewModel() {

    private val _jobState = MutableStateFlow<UiState<Job>>(UiState.Idle)
    val jobState: StateFlow<UiState<Job>> = _jobState.asStateFlow()

    private val _applicationResult = MutableStateFlow<UiState<String>>(UiState.Idle)
    val applicationResult: StateFlow<UiState<String>> = _applicationResult.asStateFlow()

    private val _uploadProgress = MutableStateFlow(0f)
    val uploadProgress: StateFlow<Float> = _uploadProgress.asStateFlow()

    fun loadJob(jobId: String) {
        _jobState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val job = ConnectiveRepository.getJobById(jobId)
                if (job != null) {
                    _jobState.value = UiState.Success(job)
                } else {
                    _jobState.value = UiState.Error("Job listing not found or has been removed.")
                }
            } catch (e: Exception) {
                _jobState.value = UiState.Error(e.message ?: "Failed to load job details.")
            }
        }
    }

    fun submitApplication(
        job: Job,
        seeker: User,
        phone: String,
        location: String,
        educationLevel: String,
        experienceYears: Int,
        skills: String,
        coverNote: String,
        pickedFiles: List<Pair<Uri, String>>
    ) {
        _applicationResult.value = UiState.Loading
        _uploadProgress.value = 0f

        viewModelScope.launch {
            try {
                val result = ConnectiveRepository.submitApplication(
                    job = job,
                    seeker = seeker,
                    phone = phone.trim(),
                    location = location.trim(),
                    educationLevel = educationLevel,
                    experienceYears = experienceYears,
                    skills = skills.trim(),
                    coverNote = coverNote.trim(),
                    pickedFiles = pickedFiles,
                    onProgress = { prog -> _uploadProgress.value = prog }
                )

                when (result) {
                    is QualificationResult.Success -> {
                        _applicationResult.value = UiState.Success(result.message)
                    }
                    is QualificationResult.Ineligible -> {
                        _applicationResult.value = UiState.Error(result.reason)
                    }
                    is QualificationResult.Error -> {
                        _applicationResult.value = UiState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _applicationResult.value = UiState.Error(e.message ?: "Failed to submit application.")
            }
        }
    }

    fun resetApplicationState() {
        _applicationResult.value = UiState.Idle
        _uploadProgress.value = 0f
    }
}

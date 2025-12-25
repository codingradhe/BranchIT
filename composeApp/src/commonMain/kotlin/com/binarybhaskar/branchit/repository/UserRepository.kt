package com.binarybhaskar.branchit.repository

import com.binarybhaskar.branchit.model.UserProfile
import com.binarybhaskar.branchit.model.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * MVP: Simple in-memory user repository. Replace with Firebase logic for production.
 */
object UserRepository {
    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val fakeUser = UserProfile(
        uid = "demoUid",
        username = "ggvstudent",
        displayName = "GGV Student",
        ggvInfo = "B.Tech CSE 2025",
        backgroundUrl = "",
        about = "Aspiring developer, open to connect!",
        linkedIn = "ggvstudent",
        github = "ggvstudent",
        instagram = "ggvstudent.ig",
        email = "student@ggv.edu.in",
        skills = listOf("Kotlin", "Compose", "Firebase", "AI"),
        resumeUrl = "https://example.com/resume.pdf",
        projects = listOf(
            Project(title = "Campus Connect", description = "A GGV social app", link = "https://github.com/ggvstudent/campus-connect"),
            Project(title = "Hackathon Helper", description = "Tools for hackathons", link = "https://github.com/ggvstudent/hackathon-helper"),
            Project(title = "Portfolio Site", description = "Personal website", link = "https://ggvstudent.dev")
        ),
        achievements = listOf("Winner: GGV Hack 2025", "Dean's List 2024"),
        isVerified = true
    )

    init {
        // For demo/dev: always have a fake user logged in
        _currentUser.value = fakeUser
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null

    fun loginWithGoogle(dummyProfile: UserProfile) {
        // TODO: Replace with real Firebase Auth logic
        _currentUser.value = dummyProfile
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateProfile(updated: UserProfile) {
        _currentUser.value = updated
    }
}

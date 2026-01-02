package com.binarybhaskar.branchitandroid.data

import androidx.annotation.Keep

@Keep
data class UserProfile(
    @Keep var displayName: String = "",
    @Keep var photoUrl: String = "",
    @Keep var about: String = "",
    @Keep var linkedin: String = "",
    @Keep var instagram: String = "",
    @Keep var github: String = "",
    @Keep var skills: List<String> = emptyList(),
    @Keep var resumeUrl: String = "",
    @Keep var projectLinks: List<String> = emptyList(),
    @Keep var username: String = "",
    @Keep var usernameUpdatedAt: Long = 0L,
    @Keep var updatedAt: Long = 0L,
) {
    // explicit no-arg constructor for Firestore mapping
    constructor() : this("", "", "", "","","", emptyList(), "", emptyList(), "", 0L, 0L)
}

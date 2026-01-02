package com.binarybhaskar.branchitandroid.screen

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.binarybhaskar.branchitandroid.R
import com.binarybhaskar.branchitandroid.data.UserProfile
import com.binarybhaskar.branchitandroid.data.UserRepository
import com.binarybhaskar.branchitandroid.data.UsernameRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun SettingsScreen(navController: NavController, prefs: SharedPreferences) {
    val context: Context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id)).requestEmail().build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    var dynamicColor by remember { mutableStateOf(prefs.getBoolean("dynamic_color", true)) }
    val scope = rememberCoroutineScope()

    // Helper functions to normalize/extract usernames and build full URLs
    fun linkedinUsernameFrom(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        var s = raw.trim()
        s = s.substringAfter("linkedin.com/in/", s)
        s = s.substringAfter("linkedin.com/", s)
        s = s.substringBefore('?')
        return s.trim().trim('/')
    }

    fun githubUsernameFrom(raw: String?): String {
        if (raw.isNullOrBlank()) return ""
        var s = raw.trim()
        s = s.substringAfter("github.com/", s)
        s = s.substringBefore('?')
        return s.trim().trim('/')
    }

    fun linkedinFullFromUsername(username: String): String {
        val u = username.trim().trim('/')
        return if (u.isBlank()) "" else "https://linkedin.com/in/$u"
    }

    fun githubFullFromUsername(username: String): String {
        val u = username.trim().trim('/')
        return if (u.isBlank()) "" else "https://github.com/$u"
    }

    // Local repo and state
    val repo = remember { UserRepository() }
    var isSaving by remember { mutableStateOf(false) }

    // Pull from memory cache if available so UI paints instantly
    val cached = remember(user?.uid) { UserRepository.getCachedProfile() }

    // Profile editable fields (seed from cache or fallback to user/defaults)
    var displayName by remember(user?.uid) {
        mutableStateOf(
            (cached?.displayName ?: (user?.displayName ?: "User")).take(50)
        )
    }
    var photoUrl by remember(user?.uid) {
        mutableStateOf(
            cached?.photoUrl ?: user?.photoUrl?.toString().orEmpty()
        )
    }
    var about by remember(user?.uid) { mutableStateOf((cached?.about ?: "").take(300)) }
    var linkedin by remember(user?.uid) {
        mutableStateOf(
            linkedinUsernameFrom(
                cached?.linkedin ?: ""
            )
        )
    }
    var github by remember(user?.uid) { mutableStateOf(githubUsernameFrom(cached?.github ?: "")) }
    val skills = remember(user?.uid) {
        mutableStateListOf<String>().also { list ->
            cached?.skills?.let {
                list.addAll(it)
            }
        }
    }
    var resumeUrl by remember(user?.uid) { mutableStateOf(cached?.resumeUrl ?: "") }
    val projectLinks = remember(user?.uid) {
        val padded = ((cached?.projectLinks ?: emptyList()) + listOf(
            "", "", ""
        )).take(3); mutableStateListOf<String>().also { it.addAll(padded) }
    }

    // Username state & repo
    val usernameRepo = remember { UsernameRepository(FirebaseFirestore.getInstance(), FirebaseAuth.getInstance()) }
    var username by remember(user?.uid) { mutableStateOf(cached?.username ?: "") }
    var usernameAvailable by remember { mutableStateOf<Boolean?>(null) }
    var isCheckingUsername by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf<String?>(null) }

    // change cooldown: days user must wait between changes. For now set to 0 days.
    val USERNAME_COOLDOWN_DAYS = 0L

    // Image picker
    val imagePicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                scope.launch {
                    try {
                        isSaving = true
                        val url = repo.uploadProfileImage(uri)
                        photoUrl = url
                        Toast.makeText(context, "Profile image uploaded", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message ?: "Upload failed", Toast.LENGTH_LONG)
                            .show()
                    } finally {
                        isSaving = false
                    }
                }
            }
        }

    // PDF picker
    val pdfPicker =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                scope.launch {
                    try {
                        isSaving = true
                        val url = repo.uploadResumePdf(context.contentResolver, uri)
                        resumeUrl = url
                        Toast.makeText(context, "Resume uploaded", Toast.LENGTH_SHORT).show()
                    } catch (_: IllegalArgumentException) {
                        Toast.makeText(context, "File too large (max 1 MB)", Toast.LENGTH_LONG)
                            .show()
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message ?: "Upload failed", Toast.LENGTH_LONG)
                            .show()
                    } finally {
                        isSaving = false
                    }
                }
            }
        }

    // Initial profile snapshot for change detection
    var initialProfile by remember(user?.uid) {
        mutableStateOf(
            (cached?.copy(
                displayName = cached.displayName.ifBlank { user?.displayName ?: "User" },
                photoUrl = cached.photoUrl.ifBlank { user?.photoUrl?.toString().orEmpty() },
                projectLinks = ((cached.projectLinks) + listOf("", "", "")).take(3)
            )) ?: UserProfile(
                displayName = user?.displayName ?: "User",
                photoUrl = user?.photoUrl?.toString().orEmpty(),
                about = "",
                linkedin = "",
                github = "",
                skills = emptyList(),
                resumeUrl = "",
                projectLinks = listOf()
            )
        )
    }

    // Load profile from cloud (refresh UI if changed)
    LaunchedEffect(user?.uid) {
        if (user == null) {
            // not signed in, nothing to do
        } else {
            try {
                val profile = repo.getOrCreateProfile()
                displayName = profile.displayName.ifBlank { user.displayName ?: "User" }.take(50)
                photoUrl = profile.photoUrl.ifBlank { user.photoUrl?.toString().orEmpty() }
                about = profile.about.take(300)
                // Extract username portion for the editable fields
                linkedin = linkedinUsernameFrom(profile.linkedin)
                github = githubUsernameFrom(profile.github)
                skills.clear(); skills.addAll(profile.skills)
                resumeUrl = profile.resumeUrl
                projectLinks.clear(); projectLinks.addAll(
                    (profile.projectLinks + listOf(
                        "", "", ""
                    )).take(3)
                )
                initialProfile = profile.copy(
                    displayName = profile.displayName.ifBlank { user.displayName ?: "User" },
                    photoUrl = profile.photoUrl.ifBlank { user.photoUrl?.toString().orEmpty() },
                    projectLinks = (profile.projectLinks + listOf("", "", "")).take(3)
                )

                // also sync username state from loaded profile
                username = profile.username
            } catch (_: Exception) {
                // swallow; UI shows defaults
            }
        }
    }

    // Detect profile changes
    val profileChanged = remember(
        displayName,
        photoUrl,
        about,
        linkedin,
        github,
        resumeUrl,
        skills.toList(),
        projectLinks.toList(),
        initialProfile
    ) {
        val computedLinkedinFull = linkedinFullFromUsername(linkedin)
        val computedGithubFull = githubFullFromUsername(github)
        displayName != initialProfile.displayName || photoUrl != initialProfile.photoUrl || about != initialProfile.about || computedLinkedinFull != initialProfile.linkedin || computedGithubFull != initialProfile.github || skills.toList() != initialProfile.skills || resumeUrl != initialProfile.resumeUrl || projectLinks.toList()
            .filter { it.isNotBlank() } != initialProfile.projectLinks.filter { it.isNotBlank() }
    }

    // Dialog state for unsaved changes
    var showUnsavedDialog by remember { mutableStateOf(false) }

    // Intercept back navigation if profile changed
    BackHandler(enabled = profileChanged) {
        showUnsavedDialog = true
    }

    fun saveProfile() {
        scope.launch {
            try {
                isSaving = true
                val normalizedProjects = projectLinks.filter { it.isNotBlank() }
                val profile = UserProfile(
                    displayName = displayName,
                    photoUrl = photoUrl,
                    about = about,
                    linkedin = linkedinFullFromUsername(linkedin),
                    github = githubFullFromUsername(github),
                    skills = skills.toList(),
                    resumeUrl = resumeUrl,
                    projectLinks = normalizedProjects
                )
                repo.saveProfile(profile)
                // Update baseline so Save button disappears
                initialProfile = profile.copy()
                Toast.makeText(context, "Profile saved", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message ?: "Save failed", Toast.LENGTH_LONG).show()
            } finally {
                isSaving = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable(onClick = {
                                if (profileChanged) {
                                    showUnsavedDialog = true
                                } else {
                                    navController.popBackStack()
                                }
                            })
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Settings",
                        modifier = Modifier,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.W800,
                    )
                }
            }, actions = {
                if (profileChanged) {
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { saveProfile() }, enabled = !isSaving) {
                            Text("Save Changes")
                        }
                    }
                }
            })
        }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile image
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (photoUrl.isNotBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(96.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier.size(96.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { imagePicker.launch("image/*") }, enabled = !isSaving) {
                    Text("Change Photo")
                }
                if (photoUrl.isNotBlank()) {
                    TextButton(
                        onClick = { photoUrl = user?.photoUrl?.toString().orEmpty() },
                        enabled = !isSaving
                    ) {
                        Text("Reset")
                    }
                }
            }

            // -> Inserted: Username change UI (below Change Photo)
            Spacer(modifier = Modifier.height(8.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Username", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it.trim()
                        usernameAvailable = null
                        usernameError = null
                    },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = { Text("Choose a username") }
                )
                Spacer(modifier = Modifier.height(6.dp))

                // compute cooldown/time calculation so `remainingMs` and `canChangeNow` are in scope for both the Change button and the cooldown note below; keep changes minimal and in the Username UI block
                val lastUpdated = initialProfile.usernameUpdatedAt
                val now = System.currentTimeMillis()
                val cooldownMs = USERNAME_COOLDOWN_DAYS * 24 * 60 * 60 * 1000L
                val remainingMs = if (lastUpdated <= 0L) 0L else (cooldownMs - (now - lastUpdated)).coerceAtLeast(0L)
                val canChangeNow = remainingMs == 0L

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        // check availability
                        if (username.isBlank()) {
                            usernameError = "Enter a username"
                            return@Button
                        }
                        isCheckingUsername = true
                        usernameRepo.isUsernameAvailable(username)
                            .addOnSuccessListener { available ->
                                usernameAvailable = available
                                isCheckingUsername = false
                                if (available == true) Toast.makeText(context, "Username is available", Toast.LENGTH_SHORT).show()
                                else Toast.makeText(context, "Username is taken", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { ex ->
                                usernameAvailable = null
                                isCheckingUsername = false
                                Toast.makeText(context, ex.message ?: "Check failed", Toast.LENGTH_LONG).show()
                            }
                    }, enabled = !isSaving && !isCheckingUsername) {
                        Text("Check Availability")
                    }


                    Button(onClick = {
                        // perform change
                        if (username.isBlank()) {
                            Toast.makeText(context, "Enter a username", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (username == initialProfile.username) {
                            Toast.makeText(context, "No change", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (!canChangeNow) {
                            val daysLeft = (remainingMs / (24 * 60 * 60 * 1000L)).coerceAtLeast(0L)
                            Toast.makeText(context, "You can change username in $daysLeft days", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        isSaving = true
                        usernameRepo.changeUsername(username)
                            .addOnSuccessListener {
                                // update baseline profile so Save/Changed UI reflects
                                initialProfile = initialProfile.copy(username = username, usernameUpdatedAt = System.currentTimeMillis())
                                Toast.makeText(context, "Username updated", Toast.LENGTH_SHORT).show()
                                isSaving = false
                            }
                            .addOnFailureListener { ex ->
                                Toast.makeText(context, ex.message ?: "Change failed", Toast.LENGTH_LONG).show()
                                isSaving = false
                            }
                    }, enabled = !isSaving && (username != initialProfile.username)) {
                        Text("Change Username")
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                // helper text
                if (usernameAvailable == true) {
                    Text(text = "Available", color = MaterialTheme.colorScheme.primary)
                } else if (usernameAvailable == false) {
                    Text(text = "Taken", color = MaterialTheme.colorScheme.error)
                }

                if (usernameError != null) {
                    Text(text = usernameError ?: "", color = MaterialTheme.colorScheme.error)
                }

                // cooldown note
                if (initialProfile.username.isNotBlank()) {
                    if (USERNAME_COOLDOWN_DAYS > 0L && remainingMs > 0L) {
                        val daysLeft = (remainingMs / (24 * 60 * 60 * 1000L)).coerceAtLeast(0L)
                        Text(text = "You can change username again in $daysLeft days", fontSize = 12.sp)
                    } else {
                        Text(text = "You can change your username (limit: once every $USERNAME_COOLDOWN_DAYS days)", fontSize = 12.sp)
                    }
                } else {
                    Text(text = "Choose your username. It's unique and can be changed later.", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Profile Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    // Display name
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it.take(50) },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // About
                    OutlinedTextField(
                        value = about,
                        onValueChange = { about = it.take(300) },
                        label = { Text("About / Description") },
                        singleLine = false,
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // LinkedIn
                    Text(text = "LinkedIn")
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        label = { Text("LinkedIn URL") },
                        value = linkedin,
                        onValueChange = { linkedin = linkedinUsernameFrom(it) },
                        placeholder = { Text("<LinkedIn Username>") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        prefix = { Text("linkedin.com/in/") })

                    Spacer(modifier = Modifier.height(12.dp))

                    // GitHub
                    Text(text = "GitHub")
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        label = { Text("GitHub URL") },
                        value = github,
                        onValueChange = { github = githubUsernameFrom(it) },
                        placeholder = { Text("<GitHub Username>") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        prefix = { Text("github.com/") },
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Skills
                    Text(
                        text = "Skills",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    var newSkill by remember { mutableStateOf("") }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSkill,
                            onValueChange = { newSkill = it },
                            label = { Text("Add a skill") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Button(onClick = {
                            val s = newSkill.trim()
                            if (s.isNotEmpty() && s !in skills) {
                                skills.add(s)
                                newSkill = ""
                            }
                        }) { Text("Add") }
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(skills.size) { i ->
                            val s = skills[i]
                            ElevatedAssistChip(
                                onClick = { skills.removeAt(i) },
                                label = { Text(s) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(18.dp)
                                    )
                                })
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Resume upload
                    Text(
                        text = "Resume",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { pdfPicker.launch("application/pdf") }, enabled = !isSaving
                        ) {
                            Text(if (resumeUrl.isBlank()) "Upload PDF (< 1 MB)" else "Replace PDF")
                        }
                        if (resumeUrl.isNotBlank()) {
                            OutlinedButton(onClick = { resumeUrl = "" }) {
                                Text("Remove Uploaded PDF")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    // Project links
                    Text(
                        text = "Projects",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    repeat(3) { idx ->
                        OutlinedTextField(
                            value = projectLinks.getOrElse(idx) { "" },
                            onValueChange = { value ->
                                if (projectLinks.size <= idx) {
                                    while (projectLinks.size <= idx) projectLinks.add("")
                                }
                                projectLinks[idx] = value
                            },
                            label = { Text("Project URL ${idx + 1}") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // App Theme (Dynamic Color)
            Text(
                text = "App Theme",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = !dynamicColor, onClick = {
                            dynamicColor = false
                            prefs.edit { putBoolean("dynamic_color", false) }
                        })
                        Column(modifier = Modifier.padding(start = 4.dp)) {
                            Text("Default: BranchIT Theme")
                            Text(
                                "Branded palette: Blue (Light), Yellow (Dark)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = dynamicColor, onClick = {
                            dynamicColor = true
                            prefs.edit { putBoolean("dynamic_color", true) }
                        })
                        Column(modifier = Modifier.padding(start = 4.dp)) {
                            Text("Material Theming")
                            Text(
                                "Dynamic color from your device wallpaper",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Notifications",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(Modifier.height(8.dp))
//            Card(
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
//                shape = RoundedCornerShape(28.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text("Event Notifications")
//                    Switch(checked = notifEvents, onCheckedChange = {
//                        notifEvents = it
//                        prefs.edit { putBoolean("notif_events", it) }
//                        // Sync FCM topic subscriptions immediately
////                        NotificationSettingsManager.sync(context as MainActivity, prefs)
//                    })
//                }
////            }
//            Spacer(Modifier.height(8.dp))
//            Card(
//                Modifier.align(Alignment.Start),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
//                shape = RoundedCornerShape(28.dp)
//            ) {
//                Row(
//                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text("Chat Notifications")
//                    var expanded by remember { mutableStateOf(false) }
//                    val options = listOf("All", "Groups Only", "Queries Only", "None")
//                    Box(modifier = Modifier.padding(16.dp)) {
//                        OutlinedButton(
//                            onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Text(options[notifChatChoice])
//                        }
//                        DropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false },
//                            modifier = Modifier
//                                .fillMaxWidth(0.7f)
//                                .background(color = MaterialTheme.colorScheme.surfaceVariant),
//                            shape = RoundedCornerShape(16.dp)
//                        ) {
//                            options.forEachIndexed { idx, label ->
//                                DropdownMenuItem(text = { Text(label) }, onClick = {
//                                    notifChatChoice = idx
//                                    prefs.edit { putInt("notif_chat_choice", idx) }
//                                    expanded = false
//                                    // Sync FCM topic subscriptions immediately
////                                    NotificationSettingsManager.sync(context as MainActivity, prefs)
//                                })
//                            }
//                        }
//                    }
//                }
//            }
//            Spacer(Modifier.height(8.dp))
//            Card(
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
//                shape = RoundedCornerShape(28.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text("Miscellaneous")
//                    Switch(checked = notifMisc, onCheckedChange = {
//                        notifMisc = it
//                        prefs.edit { putBoolean("notif_misc", it) }
//                    })
//                }
//            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // About
            Text(
                text = "About",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "App version ${
                    try {
                        context.packageManager.getPackageInfo(
                            context.packageName, 0
                        ).versionName
                    } catch (_: Exception) {
                        ""
                    }
                }", fontSize = 14.sp, modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)

            ) {
                Button(onClick = {
                    val pkg = context.packageName
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW, "market://details?id=$pkg".toUri()
                        ).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    } catch (_: ActivityNotFoundException) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://play.google.com/store/apps/details?id=$pkg".toUri()
                        ).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                }) { Text("Check for Updates") }
                OutlinedButton(onClick = {
                    val versionName = try {
                        context.packageManager.getPackageInfo(context.packageName, 0).versionName
                    } catch (_: Exception) {
                        ""
                    }
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = "mailto:".toUri()
                        putExtra(
                            Intent.EXTRA_EMAIL, arrayOf("bhaskar.patel.mail+helpbranchit@gmail.com")
                        )
                        putExtra(
                            Intent.EXTRA_SUBJECT, "Feedback for BranchIT App $versionName"
                        )
                    }
                    try {
                        context.startActivity(emailIntent)
                    } catch (_: Exception) {
                        // Fallback: open LinkedIn page
                        val webIntent = Intent(
                            Intent.ACTION_VIEW,
                            "https://www.youtube.com/watch?v=dQw4w9WgXcQ".toUri()
                        ).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(webIntent)
                    }
                }) { Text("Help and Feedback") }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = {
                // Clear all subscriptions before signing out
//                NotificationSettingsManager.clearAllFor(user?.uid)
                FirebaseAuth.getInstance().signOut()
                googleSignInClient.signOut()
                googleSignInClient.revokeAccess()
                prefs.edit { putBoolean("is_logged_in", false) }
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }) {
                Text("Sign Out")
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Unsaved changes dialog
        if (showUnsavedDialog) {
            AlertDialog(
                onDismissRequest = { showUnsavedDialog = false },
                title = { Text("Unsaved Profile Changes") },
                text = { Text("Do you want to continue without saving?") },
                confirmButton = {
                    Button(onClick = {
                        saveProfile()
                        showUnsavedDialog = false
                    }) { Text("Save") }
                },
                dismissButton = {
                    Button(onClick = {
                        showUnsavedDialog = false
                        navController.popBackStack()
                    }) { Text("Cancel and Exit") }
                })
        }
    }
}
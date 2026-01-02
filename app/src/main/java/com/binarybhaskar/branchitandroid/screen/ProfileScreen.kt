package com.binarybhaskar.branchitandroid.screen

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.binarybhaskar.branchitandroid.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.core.net.toUri

@Composable
fun ProfileScreen() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { Spacer(Modifier.size(8.dp)) }

        item {
            if (uid != null) {
                UserProfileCard(userId = uid)
            } else {
                Text("Not signed in", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileCard(userId: String, onEdit: (() -> Unit)? = null) {
    val repo = remember { UserRepository() }
    val ctx = LocalContext.current

    val nameState = remember { mutableStateOf("") }
    val photoState = remember { mutableStateOf("") }
    val aboutState = remember { mutableStateOf("") }
    val linkedinState = remember { mutableStateOf("") }
    val githubState = remember { mutableStateOf("") }
    val skillsState = remember { mutableStateOf(listOf<String>()) }
    val resumeState = remember { mutableStateOf("") }
    val projectsState = remember { mutableStateOf(listOf<String>()) }

    LaunchedEffect(userId) {
        try {
            val p = repo.getProfile(userId)
            if (p != null) {
                nameState.value = p.displayName
                photoState.value = p.photoUrl
                aboutState.value = p.about
                linkedinState.value = p.linkedin
                githubState.value = p.github
                skillsState.value = p.skills
                resumeState.value = p.resumeUrl
                projectsState.value = p.projectLinks.take(3)
            }
        } catch (e: Exception) {
            Log.w("UserProfileCard", "failed to load profile", e)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val photo = photoState.value
                if (photo.isNotBlank()) {
                    AsyncImage(
                        model = photo,
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // simple placeholder
                    Image(
                        painter = painterResource(com.binarybhaskar.branchitandroid.R.drawable.ic_ggv_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(Modifier.size(12.dp))

                Column(Modifier.weight(1f)) {
                    val displayName = nameState.value.ifBlank { "User" }
                    Text(displayName, style = MaterialTheme.typography.titleMedium)
                }

                onEdit?.let {
                    Button(onClick = it) { Text("Edit") }
                }
            }

            val linkedin = linkedinState.value
            val github = githubState.value
            if (linkedin.isNotBlank() || github.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (linkedin.isNotBlank()) {
                        Icon(
                            painter = painterResource(com.binarybhaskar.branchitandroid.R.drawable.ic_linkedin),
                            contentDescription = "LinkedIn",
                            modifier = Modifier.clickable(onClick = {
                                ctx.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        linkedin.toUri()
                                    )
                                )
                            })
                        )
                    }
                    if (github.isNotBlank()) {
                        Icon(
                            painter = painterResource(com.binarybhaskar.branchitandroid.R.drawable.ic_github),
                            contentDescription = "GitHub",
                            modifier = Modifier.clickable(onClick = {
                                ctx.startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        github.toUri()
                                    )
                                )
                            })
                        )
                    }
                }
            }

            val about = aboutState.value
            if (about.isNotBlank()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(about, Modifier.padding(12.dp))
                }
            }

            val skills = skillsState.value
            if (skills.isNotEmpty()) {
                Text("Skills")
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    skills.take(8).forEach { s ->
                        SuggestionChip(
                            modifier = Modifier
                                .size(32.dp)
                                .wrapContentWidth(),
                            label = { Text(s) },
                            onClick = {}
                        )
                    }
                }
            }

            val resumeUrl = resumeState.value
            if (resumeUrl.isNotBlank()) {
                Button(onClick = {
                    ctx.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            resumeUrl.toUri()
                        )
                    )
                }) { Text("Download Resume") }
            }

            val projects = projectsState.value
            if (projects.isNotEmpty()) {
                Text("Projects")
                projects.forEach { url ->
                    TextButton(onClick = {
                        ctx.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                url.toUri()
                            )
                        )
                    }) { Text(url, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                }
            }
        }
    }
}
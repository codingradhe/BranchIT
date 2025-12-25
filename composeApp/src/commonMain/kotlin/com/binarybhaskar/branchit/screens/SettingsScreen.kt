package com.binarybhaskar.branchit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalUriHandler
import com.binarybhaskar.branchit.getPlatform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var displayName by rememberSaveable { mutableStateOf("") }
    var about by rememberSaveable { mutableStateOf("") }
    var linkedin by rememberSaveable { mutableStateOf("") }
    var github by rememberSaveable { mutableStateOf("") }
    var newSkill by remember { mutableStateOf("") }
    val skills = remember { mutableStateListOf<String>() }
    var resumeUrl by rememberSaveable { mutableStateOf("") }
    var linkedinBg by rememberSaveable { mutableStateOf("") }
    val projectLinks = remember { mutableStateListOf("", "", "") }
    val uriHandler = LocalUriHandler.current
    val platform = getPlatform().name
    val uniqueUsername = rememberSaveable { mutableStateOf("user123") } // Replace with actual logic

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LinkedIn-style editable background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0077B5)),
                contentAlignment = Alignment.Center
            ) {
                OutlinedTextField(
                    value = linkedinBg,
                    onValueChange = { linkedinBg = it },
                    label = { Text("Profile Background Text") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Profile image placeholder and change picture
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 48.sp)
            }
            Button(onClick = { /* TODO: Implement image picker */ }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Change Picture")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your unique username: ${uniqueUsername.value}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it.take(50) },
                label = { Text("Display Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
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
            OutlinedTextField(
                value = linkedin,
                onValueChange = { linkedin = it },
                label = { Text("LinkedIn Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Button(onClick = {
                if (linkedin.isNotBlank()) {
                    uriHandler.openUri("https://linkedin.com/in/${linkedin.trim()}")
                }
            }, modifier = Modifier.padding(top = 4.dp)) {
                Text("Open LinkedIn Profile")
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = github,
                onValueChange = { github = it },
                label = { Text("GitHub Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Button(onClick = {
                if (github.isNotBlank()) {
                    uriHandler.openUri("https://github.com/${github.trim()}")
                }
            }, modifier = Modifier.padding(top = 4.dp)) {
                Text("Open GitHub Profile")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Skills", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newSkill,
                    onValueChange = { newSkill = it },
                    label = { Text("Add a skill") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = {
                    val s = newSkill.trim()
                    if (s.isNotEmpty() && s !in skills) {
                        skills.add(s)
                        newSkill = ""
                    }
                }) { Text("Add") }
            }
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(skills) { skill ->
                    AssistChip(
                        onClick = { skills.remove(skill) },
                        label = { Text(skill) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            // Only show theming on Android
            if (platform == "Android") {
                Text("App Theme", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = true, onClick = { })
                    Text("Default")
                    Spacer(Modifier.width(8.dp))
                    RadioButton(selected = false, onClick = { })
                    Text("Dynamic Color")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))
            }
            // Resume section
            Text("Resume", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
            OutlinedTextField(
                value = resumeUrl,
                onValueChange = { resumeUrl = it },
                label = { Text("Resume URL (PDF)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )
            Button(onClick = {
                if (resumeUrl.isNotBlank()) {
                    uriHandler.openUri(resumeUrl)
                }
            }, modifier = Modifier.padding(top = 4.dp)) {
                Text("Open Resume")
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            // Projects section
            Text("Projects", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
            repeat(3) { idx ->
                OutlinedTextField(
                    value = projectLinks[idx],
                    onValueChange = { value -> projectLinks[idx] = value },
                    label = { Text("Project URL ${idx + 1}") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Button(onClick = {
                    if (projectLinks[idx].isNotBlank()) {
                        uriHandler.openUri(projectLinks[idx])
                    }
                }, modifier = Modifier.padding(top = 4.dp)) {
                    Text("Open Project ${idx + 1}")
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text("About", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
            Text("BranchIT App\nVersion 1.0.0", fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
        }
    }
}

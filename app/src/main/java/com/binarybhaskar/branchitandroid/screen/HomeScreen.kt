package com.binarybhaskar.branchitandroid.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.binarybhaskar.branchitandroid.data.UsernameRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import kotlinx.coroutines.launch

@Composable
fun HomeScreen() {
    val scope = rememberCoroutineScope()

    LaunchedEffect(FirebaseAuth.getInstance().currentUser?.uid) {
        // Run once per signed-in user. Ensure a username exists for the user; generates one if missing.
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val repo = UsernameRepository(FirebaseFirestore.getInstance(), auth)
            try {
                // Using Tasks API; we wire listeners to log success/failure.
                repo.ensureUsernameIfMissing(user.email)
                    .addOnSuccessListener {
                        Log.d("HomeScreen", "Username ensured for user=${user.uid}")
                    }
                    .addOnFailureListener { ex ->
                        Log.w("HomeScreen", "Failed to ensure username", ex)
                    }
            } catch (e: Exception) {
                Log.w("HomeScreen", "Exception while ensuring username", e)
            }
        }

        // Keep any existing coroutine work if needed
        scope.launch {
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item { Spacer(Modifier.size(8.dp)) }
        item {
            WelcomeHeader()
        }
        item {
            HorizontalDivider(
                thickness = 2.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun WelcomeHeader() {
    Text("BranchIT")
}

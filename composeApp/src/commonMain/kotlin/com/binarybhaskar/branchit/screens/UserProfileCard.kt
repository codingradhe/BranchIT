import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binarybhaskar.branchit.model.UserProfile
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState

@Composable
fun UserProfileCard(
    user: UserProfile,
    modifier: Modifier = Modifier,
    onLinkedInClick: (() -> Unit)? = null,
    onGitHubClick: (() -> Unit)? = null,
    onResumeClick: (() -> Unit)? = null,
    onProjectClick: ((String) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Use a placeholder for the profile image (no Coil)
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(user.displayName.take(1), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Column(Modifier.weight(1f)) {
                    Text(user.displayName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(user.ggvInfo, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                // Social icons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (user.linkedIn.isNotBlank()) {
                        Box(
                            modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFF0A66C2)).clickable { onLinkedInClick?.invoke() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("in", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                    if (user.github.isNotBlank()) {
                        Box(
                            modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.Black).clickable { onGitHubClick?.invoke() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("GH", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
            // About
            if (user.about.isNotBlank()) {
                Text(user.about, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth())
            }
            // Skills
            if (user.skills.isNotEmpty()) {
                Column {
                    Text("Skills", style = MaterialTheme.typography.labelLarge)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        user.skills.take(8).forEach { s ->
                            Surface(
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(s, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
            // Resume
            if (user.resumeUrl.isNotBlank()) {
                Button(onClick = { onResumeClick?.invoke() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Download Resume")
                }
            }
            // Projects
            if (user.projects.isNotEmpty()) {
                Column {
                    Text("Projects", style = MaterialTheme.typography.labelLarge)
                    user.projects.take(3).forEach { project ->
                        TextButton(onClick = { onProjectClick?.invoke(project.link) }, modifier = Modifier.fillMaxWidth()) {
                            Text(project.title.ifBlank { project.link }, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

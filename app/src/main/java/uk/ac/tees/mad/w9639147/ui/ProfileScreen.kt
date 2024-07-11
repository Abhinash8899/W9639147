package uk.ac.tees.mad.w9639147.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, onLogout: () -> Unit) {
    Column {
        Text(text = "Profile")
        Button(onClick = onLogout) {
            Text(text = "Logout")
        }
    }
}
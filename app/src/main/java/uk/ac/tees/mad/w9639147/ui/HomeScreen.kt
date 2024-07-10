package uk.ac.tees.mad.w9639147.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val firebase = Firebase.auth
    Scaffold(modifier = modifier, topBar = { Text(text = "MediMinder") }) { iv ->
        LazyColumn(Modifier.padding(iv)) {
            item { 
                Text(text = "Home")
            }
        }
    }
}
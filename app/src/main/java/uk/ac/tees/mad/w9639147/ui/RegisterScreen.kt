package uk.ac.tees.mad.w9639147.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier) { iv ->
        Column(Modifier.padding(iv)) {
            Text(text = "Register")
        }
    }
}
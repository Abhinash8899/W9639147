package uk.ac.tees.mad.w9639147.ui

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier,userUid : String ,onAddClicked: () -> Unit) {
    val tasks = remember { mutableStateOf<List<HashMap<String, Any>>>(emptyList()) }

    // Fetch tasks from Firestore
    LaunchedEffect(key1 = true) {
        val firestore = FirebaseFirestore.getInstance()
        val tasksCollection = firestore.collection("tasks").document(userUid).collection("user_tasks")

        try {
            val querySnapshot = tasksCollection.get().await() // Await the result

            val taskList = mutableListOf<HashMap<String, Any>>()
            for (document in querySnapshot.documents) {
                val taskMap = document.data as HashMap<String, Any>
                taskList.add(taskMap)
            }
            tasks.value = taskList
            Log.d("Task", "Tasks: ${tasks.value}")
        } catch (e: Exception) {
            // Handle any errors
            tasks.value = emptyList() // Optionally clear the list or handle error state
             Log.e(TAG, "Error fetching tasks", e)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "MediMinder", fontSize = 26.sp, fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) { iv ->
        LazyColumn(
            Modifier
                .padding(iv)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tasks.value){ task->
                TaskItem(task)
            }
        }
    }
}

@Composable
fun TaskItem(task: HashMap<String, Any>) {
    // UI for displaying each task item
    val name = task["name"] as? String ?: ""
    val description = task["description"] as? String ?: ""

    Text(
        text = "$name - $description - $t",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(8.dp)
    )
}
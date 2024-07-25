package uk.ac.tees.mad.w9639147

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun TaskDetailsScreen(modifier: Modifier = Modifier,taskId: String, userUid: String) {
    val firestore = FirebaseFirestore.getInstance()
    val taskRef =
        firestore.collection("tasks").document(userUid).collection("user_tasks").document(taskId)

    val task = remember { mutableStateOf(emptyMap<String, Any>()) }


    remember {
        taskRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.data
            if (data != null) {
                task.value = data
            }
        }
    }

    val name = task.value["name"].toString()
    val description = task.value["description"].toString()
    val time = task.value["time"].toString()
    val location = task.value["location"].toString()
    Column(modifier = Modifier.fillMaxSize(),Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Task Detail")
        cardView(name = name, description = description, time = time, location = location)
    }
}

@Composable
fun cardView(name: String, description : String, time : String, location : String){
    Card(elevation = CardDefaults.elevatedCardElevation(30.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = name)
            Text(text = description)
            Text(text = time)
            Text(text = location)
        }
    }
}
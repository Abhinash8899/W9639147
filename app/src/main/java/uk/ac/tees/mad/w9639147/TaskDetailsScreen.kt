package uk.ac.tees.mad.w9639147

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun TaskDetailsScreen(modifier: Modifier = Modifier,taskId: String, onBackClick: () -> Unit) {
    val userUid = Firebase.auth.currentUser?.uid
    val firestore = FirebaseFirestore.getInstance()
    val taskRef =
        firestore.collection("tasks").document(userUid!!).collection("user_tasks").document(taskId)

    val task = remember { mutableStateOf(emptyMap<String, Any>()) }


    remember {
        taskRef.get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.data
            if (data != null) {
                task.value = data
            }
        }
    }
    val id = task.value["id"].toString()
    val name = task.value["name"].toString()
    val description = task.value["description"].toString()
    val time = task.value["time"].toString()
    val location = task.value["location"].toString()
    Column(modifier = Modifier.fillMaxSize(),Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        val gradient = Brush.linearGradient(
            colors = listOf(Color(0xFF3F51B5), Color(0xFF2196F3))
        )
        Text(text = "Task Details",
            style = TextStyle(
                brush = gradient,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        ),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 22.dp)
        )
        cardView(name = name, description = description, time = time, location = location, onDelete = {
            taskRef.delete()
                .addOnSuccessListener {
                    Log.d("TaskDetailsScreen", "Task deleted successfully")
                    onBackClick()
                }
                .addOnFailureListener { e ->
                    Log.w("TaskDetailsScreen", "Error deleting task", e)
                }
        }
        )
    }
}

@Composable
fun cardView(name: String, description : String, time : String, location : String, onDelete:() -> Unit){
    Column() {
        Card(modifier =Modifier.fillMaxWidth() ,elevation = CardDefaults.elevatedCardElevation(30.dp)) {
            Column(modifier = Modifier.fillMaxWidth().background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3F51B5),
                        Color(0xFF2196F3)
                    )
                )
            )) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,modifier = Modifier.padding(10.dp))
                Text(
                    text = description,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,modifier = Modifier.padding(start = 10.dp, end = 10.dp))
                Row {
                    Text(
                        text = time,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,modifier = Modifier.padding(start = 10.dp))

                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = null, tint = Color.White)
                    Text(
                        text = location,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 10.dp))
                }
            }
        }
        Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete Task", modifier = Modifier.padding(8.dp).clickable {
            onDelete()
        })
    }
}
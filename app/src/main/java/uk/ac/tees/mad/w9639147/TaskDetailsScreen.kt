package uk.ac.tees.mad.w9639147

import android.content.Context
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(context : Context, id: String, name: String, description: String, time: String, location : String, onDismiss:() -> Unit) {
    val names = remember { mutableStateOf(name) }
    val descriptions = remember { mutableStateOf(description) }
    val times = remember { mutableStateOf(time) }
    val locations = remember { mutableStateOf(location) }
    val thisContext = LocalContext.current
    val isLoadingHere = remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Edit Details",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold) })
        }
    ) {innerPadding ->

        AlertDialog(onDismissRequest = { onDismiss() }) {
            if (isLoadingHere.value) {
                CircularProgressIndicator(modifier = Modifier.size(50.dp))
            }
            Column(modifier = Modifier.padding(innerPadding)) {
                OutlinedTextField(value = names.value, onValueChange = { names.value = it },
                    label = { Text(text = "Name") })
                OutlinedTextField(value = descriptions.value,
                    onValueChange = { descriptions.value = it },
                    label = { Text(text = "Description") })
                OutlinedTextField(value = times.value, onValueChange = { times.value = it },
                    label = { Text(text = "Time") })
                OutlinedTextField(value = locations.value, onValueChange = { locations.value = it },
                    label = { Text(text = "Location") })
                Button(onClick = {
                    isLoadingHere.value = true
                    val firestore = FirebaseFirestore.getInstance()
                    val userUid = Firebase.auth.currentUser?.uid
                    val taskRef =
                        firestore.collection("tasks").document(userUid!!).collection("user_tasks")
                            .document(id)
                    taskRef.update(
                        mapOf(
                            "name" to names.value,
                            "description" to descriptions.value,
                            "time" to times.value,
                            "location" to locations.value
                        )
                    ).addOnSuccessListener {
                        isLoadingHere.value = false
                        onDismiss()
                        Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT)
                            .show()
                    }.addOnFailureListener {
                        isLoadingHere.value = false
                        onDismiss()
                        Toast.makeText(context, "Failed to update task", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text(text = "Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(modifier: Modifier = Modifier,taskId: String, onBackClick: () -> Unit) {
    val userUid = Firebase.auth.currentUser?.uid
    val firestore = FirebaseFirestore.getInstance()
    val taskRef =
        firestore.collection("tasks").document(userUid!!).collection("user_tasks").document(taskId)

    val task = remember { mutableStateOf(emptyMap<String, Any>()) }

    val isEditVisible = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

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
    val imageUri = task.value["imageUri"].toString()
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Details")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }

    ) {innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp), Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                if (isEditVisible.value) {
                    EditTaskScreen(
                        context = context,
                        id = id,
                        name = name,
                        description = description,
                        time = time,
                        location = location,
                        onDismiss = {
                            isEditVisible.value = false
                        })
                }
//                val gradient = Brush.linearGradient(
//                    colors = listOf(Color(0xFF3F51B5), Color(0xFF2196F3))
//                )
//                Text(
//                    text = "Reminder Details",
//                    style = TextStyle(
//                        brush = gradient,
//                        fontSize = 40.sp,
//                        fontWeight = FontWeight.Bold
//                    ),
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(start = 22.dp)
//                )
                if(imageUri.length>2){
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.size(250.dp),
                    contentScale = ContentScale.Inside
                )
                }else{
                    Image(painter = painterResource(id = R.drawable._2648222), contentDescription = "no Image Thumbnail")
                }
                cardView(
                    name = name,
                    description = description,
                    time = time,
                    location = location,
                    imageUri = imageUri,
                    onDelete = {
                        taskRef.delete()
                            .addOnSuccessListener {
                                Log.d("TaskDetailsScreen", "Task deleted successfully")
                                onBackClick()
                            }
                            .addOnFailureListener { e ->
                                Log.w("TaskDetailsScreen", "Error deleting task", e)
                            }
                    },
                    onEditClick = {
                        isEditVisible.value = true
                    }
                )
            }
        }
    }
}

@Composable
fun cardView(name: String, description : String, time : String, location : String,imageUri : String, onDelete:() -> Unit, onEditClick:() -> Unit) {
    Column() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3F51B5),
                                Color(0xFF2196F3)
                            )
                        )
                    )
            ) {
                Text(
                    text ="Name - $name",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp)
                )
                Text(
                    text = "Description - $description",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                )
                Row {
                    Text(
                        text = "Time - $time",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = location,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 10.dp)
                    )
                }
            }
        }
        Row {
            Icon(imageVector = Icons.Filled.Delete,
                contentDescription = "Delete Task",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onDelete()
                    })
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Filled.Edit,
                contentDescription = "Edit Profile",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        onEditClick()
                    })
        }
    }
}
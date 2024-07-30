package uk.ac.tees.mad.w9639147.ui

import android.Manifest
import android.content.Context
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import uk.ac.tees.mad.w9639147.ApplicationViewModel
import uk.ac.tees.mad.w9639147.LocationManager
import uk.ac.tees.mad.w9639147.MediMinderApp
import uk.ac.tees.mad.w9639147.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class TaskEntity(
    val id: String?,
    val name: String?,
    val description: String?,
    val time: String?,
    val location: String?,
    val imageUri: String?
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddTask(modifier: Modifier = Modifier, onBackPressed: () -> Unit) {
    val isLoading = remember { mutableStateOf(false) }
    val taskName = remember { mutableStateOf("") }
    val taskDescription = remember { mutableStateOf("") }
    val location = remember {
        mutableStateOf("")
    }
    val imageUrl = remember { mutableStateOf("") }

    val userUid = Firebase.auth.currentUser?.uid
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var showTimePicker by remember { mutableStateOf(false) }

    val currentTime = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val formattedTime = currentTime.format(formatter)
    val time = remember { mutableStateOf(formattedTime) }

    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(selectedTime) {
        time.value = selectedTime.format(formatter)
    }

    val locationPermissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )
    val locationPermissionsState = rememberMultiplePermissionsState(
        locationPermissions
    )

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }

    val applicationViewModel: ApplicationViewModel = viewModel()

    val activity = (context as ComponentActivity)
    val locationManager = LocationManager(context, activity)
    val isGpsEnabled = locationManager.gpsStatus.collectAsState(initial = false)
    val locationState =
        applicationViewModel.locationFlow.collectAsState(initial = randomLocation())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Add Task")
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                imageUri?.let {
                    val painter = rememberAsyncImagePainter(it)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(250.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                } ?: run {
                    Image(
                        painter = rememberAsyncImagePainter(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(250.dp)
                            .border(
                                width = 2.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                }

                OutlinedTextField(
                    value = taskName.value,
                    onValueChange = { taskName.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "Task Name")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                OutlinedTextField(
                    value = taskDescription.value,
                    onValueChange = { taskDescription.value = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(128.dp),
                    placeholder = {
                        Text(text = "Task description")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )
                OutlinedTextField(
                    value = time.value,
                    onValueChange = { time.value = it },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showTimePicker = true
                        },
                    placeholder = {
                        Text(text = "Reminder time")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = MaterialTheme.colorScheme.primary,
                        disabledTextColor = MaterialTheme.colorScheme.primary
                    )
                )

                OutlinedTextField(
                    value = location.value,
                    onValueChange = {
                        location.value = it
                    },
                    placeholder = {
                        Text(
                            text = "Address"
                        )
                    },
                    trailingIcon = {

                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = "Get location",
                            modifier = Modifier.clickable {
                                if (locationPermissionsState.allPermissionsGranted) {
                                    if (!isGpsEnabled.value) {
                                        locationManager.checkGpsSettings()
                                        println("Not enabled")
                                    } else {
                                        Log.d("Location", "Location permission granted")
                                        Log.d("Latitude", "${locationState.value.latitude}")
                                        location.value = locationManager.getAddressFromCoordinate(
                                            latitude = locationState.value.latitude,
                                            longitude = locationState.value.longitude
                                        )
                                        println("enabled")

                                    }
                                } else {
                                    println("Permission not got")

                                    locationPermissionsState.launchMultiplePermissionRequest()
                                }

                            }
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {  /*on Done Logic*/
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()

                )
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isLoading.value = true
                            val firestore = Firebase.firestore
                            imageUri.let {
                                if (it != null) {
                                    imageUrl.value = uploadImageToFirebase(context, it)!!
                                }
                            }
                            val task = TaskEntity(
                                id = "",
                                name = taskName.value,
                                description = taskDescription.value,
                                time = time.value,
                                location = location.value,
                                imageUri = imageUrl.value
                            )
                            Log.d("Task", task.toString())
                            firestore.collection("tasks").document(userUid!!)
                                .collection("user_tasks")
                                .add(task).addOnSuccessListener { documentReference ->
                                    val taskID = documentReference.id
                                    val taskEntity = TaskEntity(
                                        id = taskID,
                                        name = taskName.value,
                                        description = taskDescription.value,
                                        time = time.value,
                                        location = location.value,
                                        imageUri = imageUrl.value
                                    )
                                    Log.d("Task", taskEntity.toString())
                                    firestore.collection("tasks").document(userUid)
                                        .collection("user_tasks").document(taskID).set(taskEntity)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Task added",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            isLoading.value = false
                                            onBackPressed()
                                        }

                                    Toast.makeText(context, "Task added", Toast.LENGTH_SHORT).show()
                                    isLoading.value = false

                                }.addOnFailureListener {
                                    Toast.makeText(context, "Task not added", Toast.LENGTH_SHORT)
                                        .show()
                                    isLoading.value = false
                                }

                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = "Add")
                    }
                }
            }
        }
        if (showTimePicker) {
            TimePickerDialog(
                onTimeSet = { time ->
                    selectedTime = time
                    showTimePicker = false
                },
                onDismissRequest = { showTimePicker = false },
                initialTime = selectedTime
            )
        }
    }
}

suspend fun uploadImageToFirebase(context: Context, imageUri: Uri): String? {
    return try {
        val storageRef = Firebase.storage.reference
        val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val uploadTask = imageRef.putFile(imageUri)

        val downloadUrl = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.await()

        downloadUrl.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun TimePickerDialog(
    onTimeSet: (LocalTime) -> Unit,
    onDismissRequest: () -> Unit,
    initialTime: LocalTime,
) {
    var selectedHour by remember { mutableStateOf(initialTime.hour) }
    var selectedMinute by remember { mutableStateOf(initialTime.minute) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Select Time") },
        text = {
            Row {
                NumberPicker(
                    value = selectedHour,
                    onValueChange = { selectedHour = it },
                    range = 0..23
                )
                NumberPicker(
                    value = selectedMinute,
                    onValueChange = { selectedMinute = it },
                    range = 0..59
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onTimeSet(LocalTime.of(selectedHour, selectedMinute))
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun NumberPicker(value: Int, onValueChange: (Int) -> Unit, range: IntRange) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onValueChange(value - 1) }, enabled = value > range.first) {
            Text(text = "-")
        }
        Text(text = value.toString())
        IconButton(onClick = { onValueChange(value + 1) }, enabled = value < range.last) {
            Text(text = "+")

        }
    }
}

private fun randomLocation(): Location {
    val location = Location("MyLocationProvider")
    location.apply {
        latitude = 51.509865
        longitude = -0.118092
    }
    return location
}
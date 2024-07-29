package uk.ac.tees.mad.w9639147.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, onLogout: () -> Unit) {

    val coroutineScope = rememberCoroutineScope()
    val userUid = Firebase.auth.currentUser?.uid
    val context = LocalContext.current
    var email by remember { mutableStateOf("Loading...") }
    var name by remember { mutableStateOf("Loading...") }
    var mobileNumber by remember { mutableStateOf("Loading...") }
    var password by remember { mutableStateOf("Loading...") }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = userUid) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocument = firestore.collection("users").document(userUid!!)

        try {
            val usernode = userDocument.get().await()
            val userData = usernode.data
            Log.d("user", "userdata: $userData")
            if (userData != null) {
                email = userData["email"] as? String ?: "No Email. Kindly Edit your profile"
                name = userData["name"] as? String ?: "No Name."
                mobileNumber = userData["number"] as? String ?: "No Mobile Number."
                password = userData["password"] as? String ?: "No Password."
            }
        } catch (e: Exception) {
            println(e.message)
        }
    }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(elevation = CardDefaults.elevatedCardElevation(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )) {
            Column(
                modifier = Modifier.background(brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3F51B5),
                        Color(0xFF2196F3)
                    )
                )),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Profile",modifier = Modifier.padding(10.dp), fontSize = 30.sp, color = Color.White)
                userInfoData(email = email)
                outlinedField(value = name, onValueChange = { name = it }, label = "Name")
                outlinedField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = " Mobile Number"
                )
                Row(modifier = Modifier.padding(10.dp)) {
                    Button(onClick = {
                        isLoading.value = true
                        coroutineScope.launch {
                            val firestore = FirebaseFirestore.getInstance()
                            val userDocument = firestore.collection("users").document(userUid!!)
                            val userData = hashMapOf(
                                "name" to name,
                                "number" to mobileNumber,
                                "email" to email,
                                "password" to password
                            )
                            userDocument.set(userData).addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Data Saved Successfully",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                Log.d("user", "userdata: $userData")
                                isLoading.value = false
                            }.addOnFailureListener {
                                Log.d("user", "userdata saved successfully")
                                isLoading.value = false
                                Toast.makeText(context, "Failed to save data", Toast.LENGTH_SHORT)
                                    .show()
                            }.await()
                        }
                    }, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ), modifier = Modifier.border(width = 1.dp, Color.White)) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(text = "Save")
                        }
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Button(onClick = onLogout, colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ), modifier = Modifier.border(width = 1.dp, Color.White)) {
                        Text(text = "Logout")
                    }
                }
            }
        }
    }
}
@Composable
fun userInfoData(email: String) {
    Column {
        Text(text = email, color = Color.White)
    }
}

@Composable
fun outlinedField(value: String, onValueChange: (String) -> Unit, label : String) {
    OutlinedTextField(value = value, onValueChange = onValueChange, singleLine = true, label = {
        Text(text = label)
    })
}
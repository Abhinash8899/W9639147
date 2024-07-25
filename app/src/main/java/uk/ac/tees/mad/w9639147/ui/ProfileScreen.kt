package uk.ac.tees.mad.w9639147.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, userUid: String, onLogout: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    var email by remember { mutableStateOf("Loading...") }
    var name by remember { mutableStateOf("Loading...") }
    var mobileNumber by remember { mutableStateOf("Loading...") }
    var password by remember { mutableStateOf("Loading...") }
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = userUid) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocument = firestore.collection("users").document(userUid)

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
        Card(elevation = CardDefaults.elevatedCardElevation(20.dp)) {
            Column(
                modifier = Modifier.background(Color.LightGray),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Profile", fontSize = 30.sp)
                userInfoData(email = email)
                outlinedField(value = name, onValueChange = { name = it }, label = "Name")
                outlinedField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it },
                    label = " Mobile Number"
                )
                Button(onClick = {
                    isLoading.value = true
                    coroutineScope.launch {
                        val firestore = FirebaseFirestore.getInstance()
                        val userDocument = firestore.collection("users").document(userUid)
                        val userData = hashMapOf(
                            "name" to name,
                            "number" to mobileNumber,
                            "email" to email,
                            "password" to password
                        )
                        userDocument.set(userData).addOnSuccessListener {
                            Toast.makeText(context, "Data Saved Successfully", Toast.LENGTH_SHORT)
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
                }) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = "Save")
                    }
                }
                Button(onClick = onLogout) {
                    Text(text = "Logout")
                }
            }
        }
    }
}
@Composable
fun userInfoData(email: String) {
    Column {
        Text(text = email)
    }
}

@Composable
fun outlinedField(value: String, onValueChange: (String) -> Unit, label : String) {
    OutlinedTextField(value = value, onValueChange = onValueChange, singleLine = true, label = {
        Text(text = label)
    })
}
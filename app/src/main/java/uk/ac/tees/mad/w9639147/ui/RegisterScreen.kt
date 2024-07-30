package uk.ac.tees.mad.w9639147.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    onRegisterSuccess: () -> Unit,
    onLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val loading = remember { mutableStateOf(false) }
    Scaffold(modifier = modifier.fillMaxSize()) { iv ->
        Column(
            Modifier.padding(iv), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Register",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineLarge
            )
            Column(modifier = Modifier.padding(16.dp)) {

                TextField(
                    value = username,
                    onValueChange = {
                        username = it
                    },
                    label = { Text(text = "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    })
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = { Text(text = "Password") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        loading.value = true
                        val firebaseAuth = Firebase.auth
                        scope.launch {
                            firebaseAuth.createUserWithEmailAndPassword(username, password)
                                .addOnSuccessListener {
                                    val firestore = Firebase.firestore
                                    val user = hashMapOf(
                                        "email" to username,
                                        "password" to password
                                    )
                                    val uid = it.user?.uid
                                    firestore.collection("users").document(uid!!)
                                        .set(user).addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Register success",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loading.value = false
                                            onRegisterSuccess()
                                        }.addOnFailureListener {
                                            it.printStackTrace()
                                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT)
                                                .show()
                                            loading.value = false
                                        }
                                }.addOnFailureListener {
                                    it.printStackTrace()
                                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                                    loading.value = false
                                }
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (loading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(text = "Register")
                    }
                }
            }
            Text(text = "Already have an account? Sign in", modifier = Modifier.clickable {
                onLogin()
            })
        }
    }
}
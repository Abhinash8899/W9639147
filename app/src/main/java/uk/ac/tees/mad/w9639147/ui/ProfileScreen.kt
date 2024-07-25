package uk.ac.tees.mad.w9639147.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(modifier: Modifier = Modifier,userUid : String, onLogout: () -> Unit) {

    LaunchedEffect(key1 = true) {
        val firestore = FirebaseFirestore.getInstance()
        val userCollection = firestore.collection("users")
        val userDocument = userCollection.document(userUid)

        try {
            val usernode = userDocument.get().await()
            val userData = usernode.data?.let { HashMap<String, Any>(it) }
            Log.d("user","userdata : $userData")

        }catch (e:Exception){
            println(e.message)
        }
    }
    Column {
        Text(text = "User ID: $userUid")
        Text(text = "Profile")
        Button(onClick = onLogout) {
            Text(text = "Logout")
        }
    }
}

@Preview
@Composable
fun showProfile(){
    ProfileScreen(userUid = "123", onLogout = {})
}
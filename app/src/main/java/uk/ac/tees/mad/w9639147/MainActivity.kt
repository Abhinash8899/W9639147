package uk.ac.tees.mad.w9639147

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import uk.ac.tees.mad.w9639147.ui.AddTask
import uk.ac.tees.mad.w9639147.ui.SplashScreen
import uk.ac.tees.mad.w9639147.ui.HomeScreen
import uk.ac.tees.mad.w9639147.ui.LoginScreen
import uk.ac.tees.mad.w9639147.ui.ProfileScreen
import uk.ac.tees.mad.w9639147.ui.RegisterScreen
import uk.ac.tees.mad.w9639147.ui.theme.MediMinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediMinderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MediMinderApp(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MediMinderApp(modifier: Modifier) {
    val navController = rememberNavController()
    val firebase = Firebase.auth
    val currentUser = firebase.currentUser
    val isLoggedIn = currentUser != null

    Scaffold(modifier = modifier) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = "splash"
        ) {
            composable("splash") {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(if (isLoggedIn) "home" else "login") {
                            popUpTo("splash") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen()
            }
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") {
                                inclusive = true
                            }
                        }
                    },
                    onRegister = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                RegisterScreen()
            }
            composable("addtask") {
                AddTask()
            }
            composable("taskdetails") {
                TaskDetailsScreen()
            }
            composable("profile") {
                ProfileScreen(
                    onLogout = {
                        firebase.signOut()
                        navController.navigate("login") {
                            popUpTo("home") {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}
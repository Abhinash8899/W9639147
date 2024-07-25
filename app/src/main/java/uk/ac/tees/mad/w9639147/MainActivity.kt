package uk.ac.tees.mad.w9639147

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
                MediMinderApp()

            }
        }
    }
}

@Composable
fun MediMinderApp() {
    val navController = rememberNavController()
    val firebase = Firebase.auth
    val currentUser = firebase.currentUser
    val isLoggedIn = currentUser != null
    val uid = currentUser?.uid
    Surface {
        NavHost(
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
                HomeScreen(userUid = uid!!,
                    onAddClicked = {
                        navController.navigate("addtask")
                    },
                    onProfileClick = {
                        navController.navigate("profile")
                    },
                    onItemClicked = {
                        Log.d("ItemClicked2", "ItemClicked: $it")
                        navController.navigate("taskdetails/$it")

                    }
                )
            }
            composable(
                route = "taskdetails/{taskId}",
                arguments = listOf(navArgument("taskId") { type = NavType.StringType })
            ) { backStackEntry ->
                val taskId = backStackEntry.arguments?.getString("taskId")
                if (taskId != null && uid != null) {
                    TaskDetailsScreen(taskId = taskId, userUid = uid, onBackClick = {
                        navController.popBackStack()
                    })
                }
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
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("home") {
                            popUpTo("register") {
                                inclusive = true
                            }
                        }
                    },
                    onLogin = {
                        navController.navigate("login")
                    }
                )
            }
            composable("addtask") {
                AddTask(userUid = uid!!,
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    userUid = uid!!,
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
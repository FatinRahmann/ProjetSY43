package com.example.projetsy43.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projetsy43.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.projetsy43.model.entities.User
import com.example.projetsy43.model.UserSession.currentUser
import com.example.projetsy43.ui.theme.components.AppToast
import com.example.projetsy43.ui.theme.components.ToastType

@Composable
fun LoginScreen(navController: NavController) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var shouldNavigate by remember { mutableStateOf(false) }

    // Toast variables :
    var toastVisible by remember { mutableStateOf(false) }
    var toastType by remember { mutableStateOf(ToastType.SUCCESS) }
    var toastMessage by remember { mutableStateOf("") }

    // Helper
    fun showToast(msg: String, type: ToastType) {
        toastMessage = msg
        toastType = type
        toastVisible = true
    }


    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            showToast(errorMessage, ToastType.ERROR)
        }
    }

    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            kotlinx.coroutines.delay(1000)
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Login", fontSize = 28.sp, modifier = Modifier.padding(bottom = 24.dp) , color = MaterialTheme.colorScheme.onSurface)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                // add toggle password
                trailingIcon = {
                    val iconRes =
                        if (passwordVisible) R.drawable.visibility else R.drawable.visibilityoff
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (passwordVisible) "Cacher" else "Afficher",
                        modifier = Modifier
                            .clickable { passwordVisible = !passwordVisible }
                            .padding(8.dp)
                    )
                }
            )

            Button(
                onClick = {
                    val auth = FirebaseAuth.getInstance()
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //to get the role of user
                                val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                                val dbRef = FirebaseDatabase.getInstance().reference.child("user").child(uid)

                                dbRef.get().addOnSuccessListener { snapshot ->
                                    val user = User(
                                        uid = uid,
                                        email = snapshot.child("email").value?.toString() ?: "",
                                        prenom = snapshot.child("prenom").value?.toString() ?: "",
                                        nom = snapshot.child("nom").value?.toString() ?: "",
                                        role = snapshot.child("role").value?.toString() ?: ""
                                    )
                                    currentUser = user
                                    showToast("Welcome Back, ${user.prenom}!", ToastType.SUCCESS)
                                    shouldNavigate = true
                                }
                            } else {
                                errorMessage = task.exception?.message ?: "Erreur inconnue"
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)  // force alignment at the top
        ) {
            AppToast(
                message = toastMessage,
                visible = toastVisible,
                type = toastType
            ) {
                toastVisible = false
            }
        }
    }
}

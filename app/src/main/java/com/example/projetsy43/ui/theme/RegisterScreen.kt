package com.example.projetsy43.ui.theme

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.projetsy43.R
import com.example.projetsy43.ui.theme.components.RoleCardSelector
import com.example.projetsy43.viewmodel.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RegisterScreen(navController: NavHostController) {
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    var prenom by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // role :
    val roleOption = listOf("buyer" , "seller")
    var selectedRole by remember { mutableStateOf(roleOption[0]) }
    var expanded by remember { mutableStateOf(false) }

    //viewModel:
    val registerViewModel: RegisterViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center,

    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Register",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground ,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = prenom,
                onValueChange = { prenom = it },
                label = { Text("Prenom") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

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

            RoleCardSelector (
                selectedRole = registerViewModel.type.value,
                onRoleSelected = { registerViewModel.type.value = it }
            )



            Button(onClick = {
                if (prenom.isNotEmpty() && nom.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    registerProfile(prenom, nom, email, password, context , registerViewModel.type.value)
                    navController.navigate("home") {
                        popUpTo("register") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Please Fill all the forms", Toast.LENGTH_SHORT).show()
                }
            },
                colors = ButtonDefaults.buttonColors(  // Corrected colors usage
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )) {
                Text(text="Register")
            }
        }
    }
}


fun registerProfile(prenom: String, nom: String, email: String, password: String, context: Context , selectedRole: String ) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val uid = user?.uid
                val userMap = mapOf(
                    "prenom" to prenom,
                    "nom" to nom,
                    "email" to email,
                    "role" to selectedRole,
                    "dateCreation" to System.currentTimeMillis()
                )
                if (uid != null) {
                    database.child("user").child(uid).setValue(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Succès de l'enregistrement", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Échec : ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Erreur : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val navController = rememberNavController() // Mock nav controller
    RegisterScreen(navController = navController)
}

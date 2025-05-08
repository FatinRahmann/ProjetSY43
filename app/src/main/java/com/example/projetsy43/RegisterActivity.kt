package com.example.projetsy43

import android.util.Log
import android.os.Bundle
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.TextFieldValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen()
        }
    }
}

//interface
@Composable
fun RegisterScreen(){
    val context = LocalContext.current
    var prenom by remember { mutableStateOf("") }
    var nom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ){
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register",
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            OutlinedTextField(
                value = prenom,
                onValueChange = { prenom = it },
                label = { Text("Prenom") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            Button(
                onClick = {
                    if (prenom.isNotEmpty() && nom.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()){
                        registerProfile(prenom, nom, email, password, context)
                        val intent = Intent(context, HomeActivity::class.java) // changer par LoginActivity pour se login
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "tt remplir", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(text = "S'inscrire")
            }

        }
    }
}

//stockage profile utilisateur
fun registerProfile(prenom: String, nom: String, email: String, password: String, context: Context){

    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference

    // creer utilisateur avec un email et mdp grace a firebase auth
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener {
                task ->
            // user actuellement connecte et un uid unique
            if (task.isSuccessful){
                val user = auth.currentUser
                val uid = user?.uid
                val userMap = mapOf(
                    "prenom" to prenom,
                    "nom" to nom,
                    "email" to email,
                    "dateCreation" to System.currentTimeMillis() // equivalent timestamp
                )
                if (uid != null){
                    database.child("user").child(uid).setValue(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "succès de l'enregistrement",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                        .addOnFailureListener {
                                e ->
                            Toast.makeText(
                                context,
                                "echec :${e.message}",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                }
                else {
                    Toast.makeText(
                        context,
                        "Erreur : ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
}



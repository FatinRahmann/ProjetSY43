package com.example.projetsy43.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetsy43.R
import com.example.projetsy43.viewmodel.UserViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    //current authenticated user
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val context = LocalContext.current

    //state var to hold user profile data
    var nom by remember { mutableStateOf(TextFieldValue("")) }
    var prenom by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var currentPassword by remember { mutableStateOf(TextFieldValue("")) }
    var newPassword by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }


    //load current user data from Firebase when the screen loads
    LaunchedEffect(currentUser?.uid) {
        val uid = currentUser?.uid ?: return@LaunchedEffect
        FirebaseDatabase.getInstance().getReference("user").child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                nom = TextFieldValue(snapshot.child("nom").value as? String ?: "")
                prenom = TextFieldValue(snapshot.child("prenom").value as? String ?: "")
                email = TextFieldValue(snapshot.child("email").value as? String ?: "")
                userViewModel.setRole(snapshot.child("role").value as? String ?: "")
                isLoading = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    //fct pour supprimer compte + supprimer event en lien avec le seller
    fun deleteAccount() {

        val user = auth.currentUser
        val uid = user?.uid ?: return //nul si un des deux n'existe pas

        val dbRef = FirebaseDatabase.getInstance().reference

        //acccede a testevents dans la db recup evenements, recup ensuite seller id et uid si egaux alors supp event
        dbRef.child("testevents").get().addOnSuccessListener { snapshot ->
            for (eventSnapshot in snapshot.children) {
                val sellerId = eventSnapshot.child("seller_id").value?.toString()
                if (sellerId == uid) {
                    eventSnapshot.ref.removeValue()
                }
            }

            //supp comp de la db
            dbRef.child("user").child(uid).removeValue()

            //supp utilisateur de firebase auth
            user.delete()
                .addOnSuccessListener {
                    Toast.makeText(context, "compte supp reussis", Toast.LENGTH_SHORT).show()
                    onLogout()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "erreur suppression : ${it.message}", Toast.LENGTH_SHORT).show()
                }

        }.addOnFailureListener {
            Toast.makeText(context, "erreur recuperation events", Toast.LENGTH_SHORT).show()
        }
    }

    //boite de dialogue
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, //ferme boite si aucune des dexu reps
            title = { Text("confirmer suppression") },
            text = { Text("cette action supprimera votre compte et tous events. Continuer ?") },

            //fermeture boite + appel fct supp compte
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    deleteAccount()
                }) {
                    Text("Oui")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Non")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_retour),
                            contentDescription = "return",
                            modifier = Modifier
                                .size(28.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Edit Profile",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(value = nom,
                onValueChange = { nom = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = prenom,
                onValueChange = { prenom = it },
                label = { Text("Prenom") },
                modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val uid = currentUser?.uid ?: return@Button
                val updatedData = mapOf("nom" to nom.text, "prenom" to prenom.text, "email" to email.text)
                FirebaseDatabase.getInstance().getReference("user").child(uid).updateChildren(updatedData)

                if (currentPassword.text.isNotBlank() && newPassword.text.isNotBlank()) {
                    val credential = EmailAuthProvider.getCredential(currentUser.email ?: "", currentPassword.text)
                    currentUser.reauthenticate(credential)
                        .addOnSuccessListener {
                            currentUser.updatePassword(newPassword.text)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to update password: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Re-authentication failed: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save Changes")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut() //  Sign the user out
                    onLogout() //  Navigate back to login screen (you control this)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Logout", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text(
                    "supp mon compte",
                    color = Color.White)
            }
        }
    }
}

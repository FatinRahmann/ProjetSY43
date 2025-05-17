package com.example.projetsy43.viewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.projetsy43.navigation.ConcertNavGraph
import com.example.projetsy43.ui.theme.ProjetSY43Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProjetSY43Theme {
                val navController = rememberNavController()
                ConcertNavGraph(navController = navController)
            }
        }
    }
}

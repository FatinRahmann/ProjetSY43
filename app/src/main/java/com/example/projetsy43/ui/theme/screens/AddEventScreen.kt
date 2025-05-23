package com.example.projetsy43.ui.theme.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.projetsy43.ui.theme.components.Dropdown
import com.example.projetsy43.ui.theme.components.EventMultilineField
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.projetsy43.factory.AddEventViewModelFactory
import com.example.projetsy43.model.repository.EventRepository

import com.example.projetsy43.ui.theme.components.EventTextField
import com.example.projetsy43.viewmodel.AddEventViewModel


//TODO: Control the values on the fields!
@Composable
fun AddEventScreen(
    navController: NavController,
    )
{
    //ViewModel instantiation with EventRepository Injection
    val repository = remember { EventRepository() }
    val viewModel: AddEventViewModel = viewModel(
        factory = AddEventViewModelFactory(repository)
    )
    // Non string value placeholders
    var capacityText by remember { mutableStateOf(viewModel.capacity.toString()) }
    var priceText by remember { mutableStateOf(viewModel.capacity.toString()) }



    //Page items
    Column (modifier = Modifier.padding(16.dp))
    {

        //Event text field with its arguments
        EventTextField(
            label = "Event name",
            value = viewModel.name,
            onValueChange = { viewModel.name = it}
        )
        EventMultilineField("Description", viewModel.description, {viewModel.description = it})
        EventTextField("Address", viewModel.address, { viewModel.address = it})
        Dropdown("Type", listOf("Concert", "Conference", "Festival"), viewModel.type, {viewModel.type = it})
        EventTextField("Price", priceText, {
            newText ->
            priceText = newText

            val parsed = newText.toDoubleOrNull()
            if (parsed != null) {
                viewModel.price = parsed
            }
            //TODO: Decide wether or not to handle adding a different value
        })
        EventTextField("Capacity", capacityText, {
            newText ->
            capacityText = newText
            val parsed = newText.toIntOrNull()
            if (parsed != null) {
                viewModel.capacity = parsed
            }
            //TODO: Decide wether or not to handle adding a different value
        })
        //TODO: Decide what to do when no image is added and check if logic works
        EventTextField("Cover Image URL", viewModel.coverImage, { viewModel.coverImage = it })
        EventTextField("Attraction", viewModel.attraction, { viewModel.attraction = it })

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = viewModel::onSubmitEvent, modifier = Modifier.fillMaxWidth()) {
            Text("Create Event")
        }

    }


}
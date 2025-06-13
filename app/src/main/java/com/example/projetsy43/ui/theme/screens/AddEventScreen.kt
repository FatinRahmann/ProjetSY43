package com.example.projetsy43.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.projetsy43.factory.AddEventViewModelFactory
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.ui.theme.components.EventTextField
import com.example.projetsy43.viewmodel.AddEventViewModel
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults
import network.chaintech.kmp_date_time_picker.utils.now
import androidx.compose.ui.graphics.RectangleShape






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


    //For the DateTimeWheelPicker
    var showDatePicker by remember { mutableStateOf(false) }
    var pickedDate = viewModel.datetime
    val formattedDate = pickedDate?.let {
        "${it.date} ${it.hour.toString().padStart(2, '0')}:${it.minute.toString().padStart(2, '0')}"
    } ?: "Pick a Date"

    var EventDescHasFocused by remember { mutableStateOf(false) }
    var EventDescText by remember { mutableStateOf("veillez rajouter une description") }

    var EventPriceHasFocused by remember { mutableStateOf(false) }
    var priceText by remember { mutableStateOf(viewModel.capacity.toString()) }

    var EventCapacityHasFocused by remember { mutableStateOf(false) }
    var capacityText by remember { mutableStateOf(viewModel.capacity.toString()) }

    var EventCoverHasFocused by remember { mutableStateOf(false) }
    var EventCoverText by remember { mutableStateOf("Veillez rajouter une image") }

    //verif champs
    val isFormValid = viewModel.name.isNotBlank()
            && viewModel.description.isNotBlank()
            && viewModel.address.isNotBlank()
            && viewModel.type.isNotBlank()
            && viewModel.coverImage.isNotBlank()
            && viewModel.attraction.isNotBlank()
            && viewModel.datetime != null
            && viewModel.price > 0.0
            && viewModel.capacity > 0


    //Page items
    Column (modifier = Modifier.padding(16.dp))
    {

        //Event text field with its arguments
        EventTextField(
            label = "Event name",
            value = viewModel.name,
            onValueChange = { viewModel.name = it }
        )
        EventTextField(
            label ="Description",
            value = EventDescText,
            onValueChange =  {
                viewModel.description = it
                EventDescText = it
            },
            onFocus = {
                if(!EventDescHasFocused) {
                    EventDescText = ""
                    EventDescHasFocused = true
                }
            }

            )
        EventTextField("Address", viewModel.address, { viewModel.address = it})
        Dropdown("Type", listOf("Concert", "Conference", "Festival"), viewModel.type, {viewModel.type = it})
        EventTextField(
            label = "Price",
            value = priceText,
            onValueChange = { newText ->
                priceText = newText
                val parsed = newText.toDoubleOrNull()
                if (parsed != null && parsed >= 0.0) {
                    viewModel.price = parsed
                } else {
                    viewModel.price = -1.0
                }
            },
            onFocus = {
                if (!EventPriceHasFocused) {
                    priceText = ""
                    EventPriceHasFocused = true
                }
            }
        )
        EventTextField("Capacity", capacityText, {
            newText ->
            capacityText = newText
            val parsed = newText.toIntOrNull()
            if (parsed != null && parsed > 0) {
                viewModel.capacity = parsed
            } else {
                viewModel.capacity = -1
            }

        },
            {
                if (!EventCapacityHasFocused) {
                    capacityText = ""
                    EventCapacityHasFocused = true
                }
            })
        //TODO: Decide what to do with image adding and check if logic works, for now do not touch the defaultRoute value for image
        EventTextField(
            "Cover Image URL",
            EventCoverText,
            {
                EventCoverText = it
                viewModel.coverImage = it },
            {
                if (!EventCoverHasFocused) {
                    EventCoverText = ""
                    EventCoverHasFocused = true
                }
            }
        )
        EventTextField("Attraction", viewModel.attraction, { viewModel.attraction = it })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(formattedDate)
            }

            if (showDatePicker) {
                WheelDateTimePickerView(
                    showDatePicker = true,
                    height = 170.dp,
                    rowCount = 5,
                    startDate = LocalDateTime.now(),
                    dateTimePickerView = DateTimePickerView.DIALOG_VIEW,
                    onDoneClick = { date ->
                        Log.d("DateTimeWheelPicker", "date is ${date}")
                        viewModel.datetime = date
                        showDatePicker = false
                    },
                    onDismiss = {
                        showDatePicker = false
                    },
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        borderColor = Color.Gray
                    ),
                    title = "Choose a Date",
                    doneLabel = "OK"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = viewModel::onSubmitEvent,
                modifier = Modifier
                    .fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(
                    "Create Event"
                )
            }
        }



    }


}
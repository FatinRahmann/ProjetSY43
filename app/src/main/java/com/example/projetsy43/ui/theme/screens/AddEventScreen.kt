
package com.example.projetsy43.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.projetsy43.R
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import com.example.projetsy43.ui.theme.components.AppToast
import com.example.projetsy43.ui.theme.components.ToastType
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.zIndex


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

    var useDefaultImage by remember { mutableStateOf(false) }
    val defaultImageUrl = "https://cdn.pixabay.com/photo/2016/11/23/15/48/audience-1853662_1280.jpg" // change cette URL selon ton cas

    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri.value = uri
            viewModel.coverImage = uri.toString()
        }
    }

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

    val showSuccessToast = remember { mutableStateOf(false) }

    LaunchedEffect(showSuccessToast.value) {
        if (showSuccessToast.value) {
            delay(3000)
            navController.navigate("home")

        }
    }

    Box(modifier = Modifier.fillMaxSize()) {




        //Page items
        Column(modifier = Modifier.padding(16.dp).zIndex(0f))
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Back Icon aligned top start
                Icon(
                    painter = painterResource(id = R.drawable.ic_retour),
                    contentDescription = "return",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(28.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    tint = Color.Black
                )

                Text(
                    text = "Add event",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            //Event text field with its arguments
            EventTextField(
                label = "Event name",
                value = viewModel.name,
                onValueChange = { viewModel.name = it }
            )
            EventTextField(
                label = "Description",
                value = EventDescText,
                onValueChange = {
                    viewModel.description = it
                    EventDescText = it
                },
                onFocus = {
                    if (!EventDescHasFocused) {
                        EventDescText = ""
                        EventDescHasFocused = true
                    }
                }

            )
            EventTextField("Address", viewModel.address, { viewModel.address = it })
            Dropdown(
                "Type",
                listOf("Concert", "Conference", "Festival"),
                viewModel.type,
                { viewModel.type = it })
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
            EventTextField(
                "Capacity", capacityText, { newText ->
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

            //Checkbox to use default image
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            ) {
                Checkbox(
                    checked = useDefaultImage,
                    onCheckedChange = {
                        useDefaultImage = it
                        if (useDefaultImage) {
                            viewModel.coverImage = defaultImageUrl
                            imageUri.value = null
                        } else {
                            viewModel.coverImage = ""
                        }
                    }
                )
                Text("use image par defaut")
            }


            if (!useDefaultImage) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choisir une image")
                }

                imageUri.value?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Image sélectionnée",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }


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

                //retour au home screen si succes
                Button(
                    onClick = {
                        viewModel.onSubmitEvent(
                            onSuccess = {
                                showSuccessToast.value = true
                            }
                        )
                    },
                    enabled = isFormValid,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color(
                            0xFF007AFF
                        ) else Color.Gray
                    ),
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()

                ) {
                    Text(
                        "Create Event"
                    )
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
        )
        {
            AppToast(
                message = "Event created successfully!",
                visible = showSuccessToast.value,
                type = ToastType.SUCCESS,
                onDismiss = { showSuccessToast.value = false })
        }
    }


}

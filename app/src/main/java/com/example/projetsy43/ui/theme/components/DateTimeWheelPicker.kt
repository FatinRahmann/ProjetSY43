package com.example.projetsy43.ui.theme.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDateTime
import network.chaintech.kmp_date_time_picker.ui.datetimepicker.WheelDateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.WheelPickerDefaults
import network.chaintech.kmp_date_time_picker.utils.now

@Composable
fun DateTimeWheelPicker(
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var pickedDate by remember { mutableStateOf<LocalDateTime?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { showDatePicker = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF))
        ) {
            Text("Open Date Picker")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = pickedDate?.toString() ?: "No date selected",
            style = MaterialTheme.typography.bodyLarge
        )

        if (showDatePicker) {
            WheelDateTimePickerView(
                showDatePicker = true,
                height = 170.dp,
                rowCount = 5,
                startDate = LocalDateTime.now(),
                dateTimePickerView = DateTimePickerView.DIALOG_VIEW,
                onDoneClick = { date ->
                    Log.d("DateTimeWheelPicker", "date is ${date}")
                    pickedDate = date
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
    }
}
package com.example.projetsy43.ui.theme.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.projetsy43.R
import com.example.projetsy43.factory.EventDetailsViewModelFactory
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.OrderRepository
import com.example.projetsy43.model.repository.TicketRepository
import com.example.projetsy43.ui.theme.components.AppToast
import com.example.projetsy43.ui.theme.components.ToastType
import com.example.projetsy43.viewmodel.EventDetailsViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FakePayment(
    eventId: String,
    amount: Int,
    price: Float,
    navController: NavHostController,
    viewModel: EventDetailsViewModel
){

    LaunchedEffect(Unit) {
        viewModel.prepareForPayment(eventId, amount, price)
    }
    val event = viewModel.eventState.value


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Payment",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_retour),
                            contentDescription = "Back",
                            modifier = Modifier.size(34.dp).padding(bottom = 10.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

        })
    {
        paddingValue -> Column(
            modifier = Modifier
                .padding(paddingValue)
                .padding(16.dp)

        ) {

        Text("Order Details", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        //  Show event info only if loaded
        if(event!=null){

        Text("Event : ${event.name}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Address : ${event.address}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        }
        else{Text("Loading event details..." , fontSize = 16.sp)}

        Spacer(modifier = Modifier.height(12.dp))

        Divider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 2.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tickets:", fontWeight = FontWeight.SemiBold , fontSize = 16.sp)
            Text("x$amount")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Total Price:", fontWeight = FontWeight.SemiBold , fontSize = 16.sp)
            Text("$price€")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Card Form
        CardLayout(navController, eventId, amount, price, viewModel)
    }
    }
}

// To have space between 4 digit of card number
class CreditCardVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(16)
        val formatted = trimmed.chunked(4).joinToString(" ")

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var spaces = 0
                for (i in 1..offset) {
                    if (i % 4 == 0 && i != offset) spaces++
                }
                return offset + spaces
            }

            override fun transformedToOriginal(offset: Int): Int {
                return offset - (offset / 5)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

@Composable
fun CardLayout(
    navController: NavHostController,
    eventId: String,
    amount: Int,
    price: Float,
    viewModel: EventDetailsViewModel
) {
    // Toast state
    val showToast = remember { mutableStateOf(false) }
    val toastMessage = remember { mutableStateOf("") }
    val toastType = remember { mutableStateOf(ToastType.SUCCESS) }

    // Input State
    var cardNumber by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }

    // Create and remember repositories
    val eventRepo = remember { EventRepository() }
    val ticketRepo = remember { TicketRepository() }
    val orderRepo = remember { OrderRepository() }

    val coroutineScope = rememberCoroutineScope()

    // Load event data into ViewModel when screen starts
    LaunchedEffect(Unit) {
        viewModel.prepareForPayment(eventId, amount, price)
    }

    if (showToast.value) {
        AppToast(
            message = toastMessage.value,
            visible = true,
            type = toastType.value,
            modifier = Modifier.fillMaxWidth()
        ) {
            showToast.value = false
        }
    }


        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),

            ) {
            // Title
            Text(
                "Card Information",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Card UI container
            Card(
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = {
                            cardNumber = it.filter { char -> char.isDigit() }.take(16)
                        },
                        label = { Text("Card Number") },
                        visualTransformation = CreditCardVisualTransformation(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_creditcard),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = expiryDate,
                            onValueChange = { expiryDate = it },
                            label = { Text("Expiry Date") },
                            placeholder = { Text("MM/YY") },
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = cvc,
                            onValueChange = { cvc = it },
                            label = { Text("CVC") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cardHolder,
                        onValueChange = { cardHolder = it },
                        label = { Text("Cardholder Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Pay Now button
            Button(
                onClick = {
                    if (viewModel.eventState.value != null) {
                        toastMessage.value = "Payment successful!"
                        toastType.value = ToastType.SUCCESS
                        showToast.value = true

                        viewModel.buyTicket()

                        // Wait 1s to allow toast to appear before navigating
                        coroutineScope.launch {
                            delay(1000)
                            navController.navigate("ticketslist")
                        }

                    } else {
                        toastMessage.value = "Please wait, loading event..."
                        toastType.value = ToastType.ERROR
                        showToast.value = true
                    }
                },

                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Pay Now", fontSize = 16.sp)
            }

            Spacer(Modifier.height(24.dp))
        }

        }






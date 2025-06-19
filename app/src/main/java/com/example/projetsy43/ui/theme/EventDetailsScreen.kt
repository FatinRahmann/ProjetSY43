package com.example.projetsy43.ui.theme

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.projetsy43.R
import com.example.projetsy43.factory.EventDetailsViewModelFactory
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.OrderRepository
import com.example.projetsy43.model.repository.TicketRepository
import com.example.projetsy43.ui.theme.components.AppToast
import com.example.projetsy43.ui.theme.components.CircularButtonComponent
import com.example.projetsy43.ui.theme.components.ToastType
import com.example.projetsy43.viewmodel.EventDetailsViewModel

// Page to view information of events and buying ticket :

@OptIn( ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    navController: NavHostController
) {
    // Create and remember repositories
    val eventRepo = remember { EventRepository() }
    val ticketRepo = remember { TicketRepository() }
    val orderRepo = remember { OrderRepository() }

    // Inject ViewModel using custom factory
    val viewModel: EventDetailsViewModel = viewModel(
        factory = EventDetailsViewModelFactory(eventRepo, ticketRepo, orderRepo)
    )

    // Load event data once when the screen first appears
    LaunchedEffect(Unit) {
        viewModel.loadEventById(eventId)
    }

    // Observe event from the ViewModel
    val event = viewModel.eventState.value


    Scaffold(
        // Top bar with return arrow
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Event Details",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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

        },


        bottomBar = {
            Column {
                // Toast message
                if (viewModel.isToastVisible && viewModel.alertMessage != null) {
                    AppToast(
                        message = viewModel.alertMessage!!,
                        visible = true,
                        type = ToastType.ERROR,
                        onDismiss = { viewModel.dismissToast() },
                        modifier = Modifier
                            .fillMaxWidth()

                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Only show delete button if current user is the seller
                if (event != null && UserSession.currentUser?.uid == event.seller_id) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                viewModel.deleteEvent(eventId) {
                                    navController.popBackStack()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Text("Delete Event", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                //  the bottom bar
                BottomPurchaseBar(viewModel, navController , eventId)
            }
        }


    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            //  Main content
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Show cover image if available
                event?.let {
                    if (it.cover_image.isNotBlank()) {
                        AsyncImage(
                            model = it.cover_image,
                            contentDescription = it.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // Event title
                    Text(
                        text = it.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Address Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lugar),
                            contentDescription = "address",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = it.address,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    //  Date Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = "Date",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = it.datetime,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    // Description
                    Text(
                        text = it.description,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 24.dp),
                        textAlign = TextAlign.Justify
                    )
                }
            }
        }
    }
}

@Composable
fun BottomPurchaseBar(viewModel: EventDetailsViewModel,
                      navController: NavHostController,
                      eventId: String
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        // Buyer layout
        when (UserSession.currentUser?.role) {
            "buyer" -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularButtonComponent("-") {
                        viewModel.decrement()
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = { viewModel.prepareForPayment(eventId, viewModel.amount, viewModel.price.toFloat())
                            navController.navigate("fakepayment/$eventId/${viewModel.amount}/${viewModel.price}")},
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.Black
                        ),
                        modifier = Modifier
                            .height(50.dp)
                            .weight(1f)
                    ) {
                        Text(viewModel.getPurchaseButtonTest(), fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    CircularButtonComponent("+") {
                        viewModel.increment()
                        if (viewModel.setAlert()) {
                            Log.d("Attention", "Capacity = ${viewModel.avaliablecapacity}")
                            viewModel.alertMessage?.let { viewModel.showToast(it) }
                        }
                    }
                }
            }

            // Seller layout
            "seller" -> {
                Text(
                    text = "Sellers cannot purchase tickets.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.DarkGray
                )
            }
        }
    }
}


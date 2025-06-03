package com.example.projetsy43.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projetsy43.model.UserSession
import com.example.projetsy43.model.repository.EventRepository
import com.example.projetsy43.model.repository.OrderRepository
import kotlinx.coroutines.launch

class SalesSumaryViewModel(
    orderRepo: OrderRepository,
    eventRepo: EventRepository
) : ViewModel() {

    val ordersFetched = UserSession.currentUser?.let {
        viewModelScope.launch {
            orderRepo.getOrdersBySellerId(it.uid)
        }
    }
}
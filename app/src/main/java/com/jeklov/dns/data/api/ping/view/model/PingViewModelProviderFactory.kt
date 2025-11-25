package com.jeklov.dns.data.api.ping.view.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeklov.dns.data.api.ping.repository.PingRepository

class PingViewModelProviderFactory(private val application: Application, private val repository: PingRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PingViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PingViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
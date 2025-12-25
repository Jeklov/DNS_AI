package com.jeklov.dns.data.api.ai.chat.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jeklov.dns.data.api.ai.aiChat.model.AIChatViewModel
import com.jeklov.dns.data.api.ai.chat.repository.AIChatRepository
import com.jeklov.dns.data.api.chat.repository.ChatRepository

class AIChatViewModelProviderFactory(private val application: Application, private val repository: AIChatRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AIChatViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AIChatViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
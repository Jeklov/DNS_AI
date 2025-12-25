package com.jeklov.dns.data.user

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.GsonBuilder // Import this
import com.jeklov.dns.data.api.ai.chat.models.AIChatResponse
import com.jeklov.dns.data.api.ai.chat.models.AIProductObject // Import this

class SharedPreference {

    class SaveData(private val context: Context) {

        // We use GsonBuilder to register the adapters explicitly for saving
        private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(AIChatResponse::class.java, AIChatResponseAdapter())
            .registerTypeAdapter(AIProductObject::class.java, AIProductObjectAdapter())
            .create()

        fun saveAIChatResponse(response: AIChatResponse) {
            val sharedPreferences =
                context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)

            try {
                // This now uses the custom serialize method
                val jsonString = gson.toJson(response)

                sharedPreferences.edit {
                    putString("AI_CHAT_RESPONSE", jsonString)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log error to see what happens
            }
        }
    }

    class LoadData(private val context: Context) {

        // Register adapters for loading as well
        private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(AIChatResponse::class.java, AIChatResponseAdapter())
            .registerTypeAdapter(AIProductObject::class.java, AIProductObjectAdapter())
            .create()

        fun getAIChatResponse(): AIChatResponse? {
            val sharedPreferences =
                context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)

            val jsonString = sharedPreferences.getString("AI_CHAT_RESPONSE", null)

            return if (jsonString != null) {
                try {
                    gson.fromJson(jsonString, AIChatResponse::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } else {
                null
            }
        }
    }
}

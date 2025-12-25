package com.jeklov.dns.data.api.ai.chat.models

import com.jeklov.dns.R

data class AIChatRequest(
    val prompt: String,
    val mode: AIMode,
)

sealed class AIMode(
    val titleRes: Int,
    val name: String,
    val prompt: String,
) {
    data object Text : AIMode(
        titleRes = R.string.text_mode,
        name = "Text",
        prompt = "",
    )
    data object Configuration : AIMode(
        titleRes = R.string.configuration_mode,
        name = "Configuration",
        prompt = "Режим сборки конфигурации ПК, соберем сборку по требованиям: ",
    )
    data object SmartSearch : AIMode(
        titleRes = R.string.smart_search_mode,
        name = "SmartSearch",
        prompt = "Режим поиска, найдем товар по требованиям: ",
    )
}

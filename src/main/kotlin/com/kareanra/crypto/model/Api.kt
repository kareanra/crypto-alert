package com.kareanra.crypto.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PriceData(
    val data: Map<String, Coin>
)

data class Coin(
    val id: Int,
    val name: String,
    val symbol: String,
    val quote: Map<String, Quote>,
)

data class Quote(
    val price: Double,
    @JsonProperty("percent_change_1h")
    val pctChange1h: Double,
    @JsonProperty("percent_change_24h")
    val pctChange24h: Double,
)
<<<<<<< Updated upstream
=======

data class VaxData(val responsePayloadData: VaxPayload)

data class VaxPayload(
    val currentTime: String,
    val data: Map<String, List<VaxStatus>>
)

data class VaxStatus(
    val city: String,
    val status: String
)

data class VaxAppointmentAvailability(
    val clinicId: String,
    val date: String
)

data class VaxAppointmentAvailabilityResponse(
    val header: VaxAppointmentAvailabilityHeader
)

data class VaxAppointmentAvailabilityHeader(
    val statusCode: String
) {
    val isAvailable
        get() = statusCode != "6007"
}

data class TranslateResponse(
    val data: NestedTranslations
)

data class NestedTranslations(
    val translations: List<Translation>
) {
    val translation: Translation
        get() = translations.single()
}

data class Translation(
    val translatedText: String
)

data class LanguagesResponse(
    val data: NestedLanguages
)

data class NestedLanguages(
    val languages: List<Language>
)

data class Language(
    val language: String,
    val name: String,
    val country: String,
)

data class LanguageWithTranslation(
    val language: Language,
    val translation: String,
)

data class StockData(
    val symbol: String,
    val price: Double,
    val lastPrice: Double?,
)
>>>>>>> Stashed changes

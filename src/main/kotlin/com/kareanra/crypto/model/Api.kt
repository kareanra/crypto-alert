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

package com.kareanra.crypto

data class Configuration(
    val apiKey: String,
    val baseUrl: String,
    val apiBaseUrl: String,
    val cvsApiKey: String,
    val clinicIds: List<String>,
    val appointmentDates: List<String>,
    val state: String,
    val recipients: List<String>,
    val emailFrom: String,
    val coins: Map<String, CoinThresholds>,
)

data class CoinThresholds(
    val priceThresholdLow: Double,
    val priceThresholdHigh: Double,
    val oneHrPctThreshold: Double,
)

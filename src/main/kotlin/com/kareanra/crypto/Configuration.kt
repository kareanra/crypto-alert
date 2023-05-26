package com.kareanra.crypto

data class Configuration(
    val apiKey: String,
    val baseUrl: String,
<<<<<<< Updated upstream
=======
    val googleBaseUrl: String,
    val apiBaseUrl: String,
    val cvsApiKey: String,
    val clinicIds: List<String>,
    val appointmentDates: List<String>,
    val state: String,
>>>>>>> Stashed changes
    val recipients: List<String>,
    val emailFrom: String,
    val coins: Map<String, CoinThresholds>,
    val stocks: Map<String, StockThreshold>,
)

data class CoinThresholds(
    val priceThresholdLow: Double,
    val priceThresholdHigh: Double,
    val oneHrPctThreshold: Double,
)

data class StockThreshold(
    val min: Double,
)

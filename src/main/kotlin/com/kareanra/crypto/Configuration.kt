package com.kareanra.crypto

data class Configuration(
    val apiKey: String,
    val baseUrl: String,
    val recipients: List<String>,
    val emailFrom: String,
    val coins: Map<String, CoinThresholds>,
)

data class CoinThresholds(
    val priceThresholdLow: Double,
    val priceThresholdHigh: Double,
    val oneHrPctThreshold: Double,
)

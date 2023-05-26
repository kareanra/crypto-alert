package com.kareanra.crypto

import com.kareanra.crypto.model.StockData
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration

class StockService(private val config: Configuration) {
    private val logger = KotlinLogging.logger { }

    private val regex = """data-last-price="(\d*\.\d*)"""".toRegex()

    private val client = OkHttpClient.Builder()
        .readTimeout(Duration.ofSeconds(30))
        .writeTimeout(Duration.ofSeconds(30))
        .build()

    private val s3Service = S3StockPriceService()

    fun getData(symbol: String): StockData {
        val lastPrice = s3Service.fetchLatestPrice(symbol)

        return try {
            val response = client.newCall(
                Request.Builder()
                    .url("${config.googleBaseUrl}/$symbol".toHttpUrl())
                    .get()
                    .build()
            ).execute().body!!.string()

            val priceString = regex.find(response)?.groupValues?.get(1) ?: throw IllegalStateException("No price found")

            StockData(
                symbol = symbol,
                price = priceString.toDouble(),
                lastPrice = lastPrice,
            ).also {
                s3Service.uploadLatestPrice(it)
            }
        } catch (e: Exception) {
            logger.error(e) { "Error fetching data from Google Finance API" }
            throw e
        }
    }
}

package com.kareanra.crypto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kareanra.crypto.model.PriceData
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration

class CryptoService(private val config: Configuration) {
    private val logger = KotlinLogging.logger { }

    private val mapper = jacksonObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    private val client = OkHttpClient.Builder()
        .readTimeout(Duration.ofSeconds(30))
        .writeTimeout(Duration.ofSeconds(30))
        .build()
    private val url = "${config.baseUrl}/cryptocurrency/quotes/latest?symbol=${config.coins.keys.joinToString(",")}"

    fun getPriceData(): PriceData {
        try {
            val response = client.newCall(
                Request.Builder()
                    .url(url.toHttpUrl())
                    .addHeader("X-CMC_PRO_API_KEY", config.apiKey)
                    .get()
                    .build()
            ).execute().body!!.byteStream()

            return mapper.readValue(response)
        } catch (e: Exception) {
            logger.error(e) { "Error fetching data from Crypto API" }
            throw e
        }
    }
}

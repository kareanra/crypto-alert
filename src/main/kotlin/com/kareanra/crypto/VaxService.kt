package com.kareanra.crypto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kareanra.crypto.model.VaxData
import mu.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration

class VaxService(config: Configuration) {
    private val logger = KotlinLogging.logger { }

    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    private val client = OkHttpClient.Builder()
        .readTimeout(Duration.ofSeconds(30))
        .writeTimeout(Duration.ofSeconds(30))
        .build()
    private val url = "${config.baseUrl}/covid-19-vaccine.vaccine-status.${config.state}.json?vaccineinfo"

    fun getData(): VaxData =
        try {
            val response = client.newCall(
                Request.Builder()
                    .url(url.toHttpUrl())
                    .addHeader("Referer", "https://www.cvs.com/immunizations/covid-19-vaccine")
                    .get()
                    .build()
            ).execute().body!!.byteStream()

            mapper.readValue(response)
        } catch (e: Exception) {
            logger.error(e) { "Error fetching data from CVS Vax API" }
            throw e
        }
}

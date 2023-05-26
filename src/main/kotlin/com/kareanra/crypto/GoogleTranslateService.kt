package com.kareanra.crypto

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kareanra.crypto.model.LanguagesResponse
import com.kareanra.crypto.model.TranslateResponse
import mu.KotlinLogging
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.File
import java.time.Duration

class GoogleTranslateService(private val config: GoogleConfiguration) {
    private val logger = KotlinLogging.logger { }

    private val geneticDistanceRegex = """Genetic distance: (\d+,\d+)""".toRegex()

    private val mapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    private val client = OkHttpClient.Builder()
        .readTimeout(Duration.ofSeconds(30))
        .writeTimeout(Duration.ofSeconds(30))
        .build()

    fun getLanguages(): LanguagesResponse =
        try {
            val url = "https://google-translate1.p.rapidapi.com/language/translate/v2/languages?target=en".toHttpUrl()
            val response = client.newCall(
                Request.Builder()
                    .url(url)
                    .addHeader("x-rapidapi-key", config.apiKey)
                    .addHeader("x-rapidapi-host", config.apiHost)
                    .addHeader("useQueryString", config.useQueryString.toString())
                    .get()
                    .build()
            ).execute().body!!.byteStream()

            mapper.readValue(response)
        } catch (e: Exception) {
            logger.error(e) { "Error fetching languages" }
            throw e
        }

    fun translate(language: String): TranslateResponse =
        try {
            val response = client.newCall(
                Request.Builder()
                    .url(config.baseUrl.toHttpUrl())
                    .addHeader("x-rapidapi-key", config.apiKey)
                    .addHeader("x-rapidapi-host", config.apiHost)
                    .addHeader("useQueryString", config.useQueryString.toString())
                    .post(
                        FormBody.Builder()
                            .addEncoded("q", config.text)
                            .addEncoded("target", language)
                            .addEncoded("source", "en")
                            .build()
                    )
                    .build()
            ).execute().body!!.byteStream()

            mapper.readValue(response)
        } catch (e: Exception) {
            logger.error(e) { "Error fetching data from Google Translate API for language $language" }
            throw e
        }

    fun downloadVoiceFile(text: String, language: String): File? =
        try {
            if (File("/Users/kyleareanraines/dev/personal/voice/${language}.mpga").exists()) {
                logger.info { "Skipping $language because file exists" }
                null
            } else {
                val url = HttpUrl.Builder()
                    .scheme("https")
                    .host("translate.google.com")
                    .addPathSegments("translate_tts")
                    .addQueryParameter("ie", Charsets.UTF_8.name())
                    .addQueryParameter("client", "tw-ob")
                    .addQueryParameter("tl", language)
                    .addQueryParameter("q", text)
                    .build()

//                logger.info { url }

                val response = client.newCall(
                    Request.Builder()
                        .url(url)
                        .get()
                        .build()
                ).execute().body!!.byteStream()

                val file = File("/Users/kyleareanraines/dev/personal/voice/${language}.mpga")

                response.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                file
            }
        } catch (e: Exception) {
            logger.error(e) { "Error downloading voice data from Google Translate API for language $language" }
            null
        }

    fun getLexicalSimilarity(l1: String, l2: String): Double? {
        val url = "http://www.elinguistics.net/Compare_Languages.aspx?Language1=${l1}&Language2=${l2}&Order=Details".toHttpUrl()
        val response = client.newCall(
            Request.Builder()
                .url(url)
                .get()
                .build()
        ).execute().body!!.byteStream()

        val str = response.use {
            String(it.readBytes())
        }

        if (str.contains("Server Error")) {
            return null
        }

        val match = geneticDistanceRegex.find(str)?.groupValues?.get(1)
        return match?.replace(",", ".")?.toDouble()
    }

    fun downloadFlag(country: String) {
        try {
            if (File("/Users/kyleareanraines/dev/personal/lingual/flags/${country}.png").exists()) {
                logger.info { "Skipping $country because file exists" }
            } else {
                val url = "https://www.countries-ofthe-world.com/flags-normal/flag-of-${country}.png".toHttpUrl()

                val response = client.newCall(
                    Request.Builder()
                        .url(url)
                        .get()
                        .build()
                ).execute()

//                val file = File("/Users/kyleareanraines/dev/personal/lingual/flags/${country}.png")
//
//                response.use { input ->
//                    file.outputStream().use { output ->
//                        input.copyTo(output)
//                    }
//                }
                logger.info { response.code }
            }
        } catch (e: Exception) {
            logger.error(e) { "Error downloading flag for $country" }
        }
    }
}

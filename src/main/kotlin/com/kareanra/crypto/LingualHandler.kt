package com.kareanra.crypto

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kareanra.crypto.model.Language
import com.kareanra.crypto.model.LanguageWithTranslation
import mu.KotlinLogging
import java.io.File

class LingualHandler : RequestHandler<Any, String> {
    private val logger = KotlinLogging.logger { }
    private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    override fun handleRequest(input: Any, context: Context): String = run()

    fun run(): String {
        val resource = requireNotNull(
            Thread.currentThread().contextClassLoader
                .getResourceAsStream("google-config.yml")
        ) {
            "config not found"
        }

        val config = mapper.readValue<GoogleConfiguration>(resource)
        val service = GoogleTranslateService(config)
//        val languages = service.getLanguages().data.languages.filterNot { it.language == "en" }.shuffled()

        val languagesResource = requireNotNull(
            Thread.currentThread().contextClassLoader
                .getResourceAsStream("translations.json")
        ) {
            "translations file not found"
        }

        val translations = jacksonObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .readValue<List<LanguageWithTranslation>>(languagesResource)
//        translations.forEachIndexed { index, languageWithTranslation ->
//            logger.info { "Language: ${languageWithTranslation.language} index: $index" }
//
//            service.downloadVoiceFile(
//                text = languageWithTranslation.translation,
//                language = languageWithTranslation.language.language
//            )
//
//            Thread.sleep(2000)
//        }

//        translations
//            .drop(55)
//            .take(8)
        listOf(
            Language("my", "Burmese", "Myanmar"),
            Language("pa", "Punjabi", "India"),
            Language("vi", "Vietnamese", "Vietnam"),
            Language("haw", "Hawaiian", "Hawaii"),
        )
            .forEach { l1 ->
                val file = File("/Users/kyleareanraines/dev/personal/lingual/src/lexical-similarity-data-${l1.language}.json")
                if (file.exists()) {
                    logger.info { "Skipping ${l1.language}" }
                } else {
                    val results = translations.mapNotNull { l2 ->
                        logger.info { "Getting similarity for ${l1.name} + ${l2.language.name}" }

                        service.getLexicalSimilarity(l1.name, l2.language.name)?.let {
                            logger.info { "result: $it" }

                            SimilarityIndex(
                                l1 = l1.name,
                                l2 = l2.language.name,
                                value = it
                            )
                        }
                    }
                    jacksonObjectMapper().writeValue(file, results.sortedBy { it.value })
                }
            }

        return ""
    }

    data class SimilarityIndex(
        val l1: String,
        val l2: String,
        val value: Double
    )
}

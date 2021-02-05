package com.kareanra.crypto

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.kareanra.crypto.model.Alert
import com.kareanra.crypto.model.Change
import com.kareanra.crypto.model.PriceData
import mu.KotlinLogging
import kotlin.math.abs

class Handler : RequestHandler<Map<String, String>, PriceData> {
    private val logger = KotlinLogging.logger { }
    private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

    override fun handleRequest(input: Map<String, String>, context: Context): PriceData = run()

    fun run(): PriceData {
        val resource = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream("config.yml")) {
            "config not found"
        }

        val config = mapper.readValue<Configuration>(resource)
        val priceData = CryptoService(config).getPriceData().also {
            logger.info { "Price data: $it" }
        }

        val alerts = evaluateThresholds(config, priceData)
        val emailService = EmailService(config)

        alerts.forEach {
            logger.info { "Sending email for alert $it" }
            emailService.sendAlert(it)
        }

        return priceData
    }

    private fun evaluateThresholds(config: Configuration, priceData: PriceData): List<Alert> =
        priceData.data.keys.mapNotNull {
            val thresholds = config.coins.getValue(it)
            val coinData = priceData.data.getValue(it)
            val price = coinData.quote.getValue("USD").price
            val pctChange1h = coinData.quote.getValue("USD").pctChange1h

            when {
                price > thresholds.priceThresholdHigh ->
                    Alert(coinData.name, Change.AbsoluteIncrease(price), price)
                price < thresholds.priceThresholdLow ->
                    Alert(coinData.name, Change.AbsoluteDecrease(price), price)
                abs(pctChange1h) > thresholds.oneHrPctThreshold ->
                    Alert(coinData.name, Change.PercentIncrease(pctChange1h), price)
                else -> null
            }
        }
}

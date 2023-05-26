package com.kareanra.crypto

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
<<<<<<< Updated upstream
import com.kareanra.crypto.model.Alert
import com.kareanra.crypto.model.Change
import com.kareanra.crypto.model.PriceData
=======
>>>>>>> Stashed changes
import mu.KotlinLogging
import kotlin.math.abs

<<<<<<< Updated upstream
class Handler : RequestHandler<Any, PriceData> {
    private val logger = KotlinLogging.logger { }
    private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

    override fun handleRequest(input: Any, context: Context): PriceData = run()

    fun run(): PriceData {
=======
class Handler : RequestHandler<Any, Unit> {
    private val logger = KotlinLogging.logger { }
    private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

    override fun handleRequest(input: Any, context: Context) = run()

    fun run() {
>>>>>>> Stashed changes
        val resource = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream("config.yml")) {
            "config not found"
        }
        val config = mapper.readValue<Configuration>(resource)
<<<<<<< Updated upstream
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
=======
        val emailService = EmailService(config)

        config.stocks.forEach { (symbol, priceThreshold) ->
            val data = StockService(config).getData(symbol)
            logger.info { "Data: $data; threshold: $priceThreshold" }

            when {
                data.price <= priceThreshold.min -> {
                    logger.info { "Price ${data.price} has not exceeded threshold" }
                }
                data.lastPrice != null && data.lastPrice >= priceThreshold.min -> {
                    logger.info { "Last price ${data.lastPrice} already exceeded threshold" }
                }
                else -> {
                    logger.info { "Sending price threshold exceeded email for $symbol" }
                    emailService.sendStockNotificationEmail(data)
                }
            }
        }
    }
>>>>>>> Stashed changes
}

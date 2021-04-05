package com.kareanra.crypto

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.kareanra.crypto.model.VaxAlert
import com.kareanra.crypto.model.VaxAppointmentAvailability
import com.kareanra.crypto.model.VaxAppointmentAvailabilityResponse
import com.kareanra.crypto.model.VaxData
import mu.KotlinLogging

class Handler : RequestHandler<Any, VaxData> {
    private val logger = KotlinLogging.logger { }
    private val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())

    override fun handleRequest(input: Any, context: Context): VaxData = run2()

    fun run(): VaxAppointmentAvailability? {
        val resource = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream("config.yml")) {
            "config not found"
        }

        val config = mapper.readValue<Configuration>(resource)
        config.clinicIds.forEach { c ->
            config.appointmentDates.forEach { ad ->
                VaxService(config).getAvailability(c, ad)?.run {
                    val emailService = EmailService(config)
                    emailService.sendAppointmentAvailabilityAlert(this)

                    logger.info { "Availability found!!!" }

                    return this
                }
            }
        }

        logger.info { "No availability" }

        return null
    }

    fun run2(): VaxData {
        val resource = requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream("config.yml")) {
            "config not found"
        }

        val config = mapper.readValue<Configuration>(resource)
        val vaxData = VaxService(config).getData().also {
            logger.info { "Vax data: $it" }
        }

        val alerts = checkAvailability(vaxData, config)
        val emailService = EmailService(config)

        if (alerts.isNotEmpty()) {
            logger.info { "Sending email for Vax alert $alerts" }
            emailService.sendVaxAlerts(alerts)
        } else {
            logger.info { "No availability! :(" }
        }

        return vaxData
    }

    private fun checkAvailability(vaxData: VaxData, config: Configuration): List<VaxAlert> =
        vaxData.responsePayloadData.data.getValue(config.state.toUpperCase()).filter {
            it.status != "Fully Booked"
        }.map {
            VaxAlert(it.city, it.status)
        }
}

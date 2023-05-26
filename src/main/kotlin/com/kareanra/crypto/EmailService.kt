package com.kareanra.crypto

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
<<<<<<< Updated upstream
import com.kareanra.crypto.model.Alert
=======
import com.kareanra.crypto.model.*
>>>>>>> Stashed changes
import mu.KotlinLogging

class EmailService(private val config: Configuration) {
    private val logger = KotlinLogging.logger { }

    private val client = AmazonSimpleEmailServiceClientBuilder.standard()
        .withRegion(Regions.US_EAST_1)
        .build()

    fun sendAlert(alert: Alert) {
        val request = SendEmailRequest().withDestination(Destination().withBccAddresses(config.recipients))
            .withMessage(
                Message()
                    .withBody(
                        Body()
                            .withHtml(
                                Content().withCharset("UTF-8").withData(
                                    """
                                        <h2>New price is $${alert.newPrice}</h2>
                                        <h2>Links</h2>
                                        <p>https://www.kraken.com</p>
                                        <p>https://www.coinbase.com</p>
                                    """.trimIndent()
                                )
                            )
                    )
                    .withSubject(
                        Content().withCharset("UTF-8").withData("[${alert.coin}] ${alert.change.display}: ${alert.amountFormatted}")
                    )
            )
            .withSource(config.emailFrom)

        try {
            client.sendEmail(request)
        } catch (e: Exception) {
            logger.error(e) { "Error sending email" }
        }
    }
<<<<<<< Updated upstream
=======

    fun sendAppointmentAvailabilityAlert(vaxAppointmentAvailability: VaxAppointmentAvailability) {
        val request = SendEmailRequest().withDestination(Destination().withBccAddresses(config.recipients))
            .withMessage(
                Message()
                    .withBody(
                        Body()
                            .withHtml(
                                Content().withCharset("UTF-8").withData(
                                    "Availability found!"
                                )
                            )
                    )
                    .withSubject(
                        Content().withCharset("UTF-8").withData("[VAX-ALERT] Availability for ${vaxAppointmentAvailability.clinicId} on ${vaxAppointmentAvailability.date}")
                    )
            )
            .withSource(config.emailFrom)

        try {
            client.sendEmail(request)
        } catch (e: Exception) {
            logger.error(e) { "Error sending email" }
        }
    }

    fun sendVaxAlerts(alerts: List<VaxAlert>) {
        val request = SendEmailRequest().withDestination(Destination().withBccAddresses(config.recipients))
            .withMessage(
                Message()
                    .withBody(
                        Body()
                            .withHtml(
                                Content().withCharset("UTF-8").withData(
                                    alerts.joinToString {
                                        """
                                            <h2>${it.city}: ${it.availability}</h2>
                                        """.trimIndent()
                                    } + "<p>https://www.cvs.com/immunizations/covid-19-vaccine</p>"
                                )
                            )
                    )
                    .withSubject(
                        Content().withCharset("UTF-8").withData("[VAX-ALERT] ${alerts.size} locations have available appointments!")
                    )
            )
            .withSource(config.emailFrom)

        try {
            client.sendEmail(request)
        } catch (e: Exception) {
            logger.error(e) { "Error sending email" }
        }
    }

    fun sendStockNotificationEmail(stockData: StockData) {
        val request = SendEmailRequest().withDestination(Destination().withBccAddresses(config.recipients))
            .withMessage(
                Message()
                    .withBody(
                        Body()
                            .withHtml(
                                Content().withCharset("UTF-8").withData(
                                        """
                                            <h2>Threshold exceeded!</h2>
                                            <p>https://digital.fidelity.com/ftgw/digital/portfolio/summary</p>
                                        """.trimIndent()
                                )
                            )
                    )
                    .withSubject(
                        Content()
                            .withCharset("UTF-8")
                            .withData("[${stockData.symbol}] exceeded price threshold: ${stockData.price}")
                    )
            )
            .withSource(config.emailFrom)

        try {
            client.sendEmail(request)
        } catch (e: Exception) {
            logger.error(e) { "Error sending stock notification email for ${stockData.symbol}" }
        }
    }
>>>>>>> Stashed changes
}

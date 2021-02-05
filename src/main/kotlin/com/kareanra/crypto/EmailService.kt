package com.kareanra.crypto

import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder
import com.amazonaws.services.simpleemail.model.Body
import com.amazonaws.services.simpleemail.model.Content
import com.amazonaws.services.simpleemail.model.Destination
import com.amazonaws.services.simpleemail.model.Message
import com.amazonaws.services.simpleemail.model.SendEmailRequest
import com.kareanra.crypto.model.Alert
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
}

package com.kareanra.crypto

import com.kareanra.crypto.model.StockData
import mu.KotlinLogging
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest

class S3StockPriceService {
    private val logger = KotlinLogging.logger { }

    private val bucket = "kiki-stock-prices"

    private val s3Client = S3Client.builder()
        .region(Region.US_EAST_1)
        .build()

    fun uploadLatestPrice(stockData: StockData) {
        try {
            val request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(stockData.symbol)
                .contentType("text/html")
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build()
            val body = RequestBody.fromString(stockData.price.toString())

            s3Client.putObject(request, body)
        } catch (e: Exception) {
            logger.error(e) { "Error uploading latest stock price: $stockData" }
        }
    }

    fun fetchLatestPrice(symbol: String): Double? =
        try {
            val request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(symbol)
                .build()

            val response = s3Client.getObject(request).use {
                String(it.readAllBytes())
            }

            logger.info { "Latest price: $response" }

            response.toDouble()
        } catch (e: Exception) {
            logger.error(e) { "Error fetching latest stock data for $symbol" }
            null
        }
}

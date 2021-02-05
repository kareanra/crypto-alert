package com.kareanra.crypto.model

data class Alert(
    val coin: String,
    val change: Change,
    val newPrice: Double,
) {
    val amountFormatted = when (change) {
        is Change.AbsoluteDecrease -> "$${change.amount}"
        is Change.AbsoluteIncrease -> "$${change.amount}"
        is Change.PercentDecrease -> "%${change.amount}"
        is Change.PercentIncrease -> "%${change.amount}"
    }
}

sealed class Change(val display: String) {
    abstract val amount: Double

    data class PercentIncrease(override val amount: Double) : Change("Percent Increase")
    data class PercentDecrease(override val amount: Double) : Change("Percent Decrease")
    data class AbsoluteIncrease(override val amount: Double) : Change("Price Increase")
    data class AbsoluteDecrease(override val amount: Double) : Change("Price Decrease")
}

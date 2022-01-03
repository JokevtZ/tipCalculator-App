package nl.learningtocode.tipcalculator.util

fun calculateTotalTip(totalBillAmount : Double, tipPercentage : Int): Double {
    return if (totalBillAmount > 1 && totalBillAmount.toString().isNotEmpty())
        (totalBillAmount * tipPercentage) / 100 else 0.0
}

fun calculateTotalAmountPerPerson(totalBillAmount: Double, splitByPerson : Int, tipPercentage: Int): Double {

    val bill = calculateTotalTip(totalBillAmount = totalBillAmount, tipPercentage = tipPercentage) + totalBillAmount
    return (bill / splitByPerson)
}
package nl.learningtocode.tipcalculator

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.learningtocode.tipcalculator.components.InputComponentField
import nl.learningtocode.tipcalculator.ui.theme.TipCalculatorTheme
import nl.learningtocode.tipcalculator.util.calculateTotalAmountPerPerson
import nl.learningtocode.tipcalculator.util.calculateTotalTip
import nl.learningtocode.tipcalculator.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContentView()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {

    TipCalculatorTheme {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFF8392E4)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Amount Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "€$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview
@Composable
fun MainContentView() {

    val splitByState = remember {
        mutableStateOf(1)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }


    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm(
            splitByState = splitByState,
            range = range,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState
        ) {}
    }
}

@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValueChanged: (String) -> Unit = {},
) {

    val totalBillAmount = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillAmount.value) {
        totalBillAmount.value.trim().isNotEmpty()
    }

    val focusManager = LocalFocusManager.current
    focusManager.moveFocus(FocusDirection.Next)


    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()


    TopHeader(totalPerPerson = totalPerPersonState.value)

    Surface(
        modifier = modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            InputComponentField(
                valueState = totalBillAmount,
                labelId = "Enter Bill Amount",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValueChanged(totalBillAmount.value.trim())

                    focusManager.clearFocus()
                })
            if (validState) {
                Row(
                    modifier = modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )
                    Spacer(
                        modifier = Modifier.width(120.dp)
                    )
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1)
                                        splitByState.value - 1
                                    else 1

                                totalPerPersonState.value =
                                    calculateTotalAmountPerPerson(
                                        totalBillAmount = totalBillAmount.value.toDouble(),
                                        splitByPerson = splitByState.value,
                                        tipPercentage = tipPercentage
                                    )

                            })

                        Text(
                            text = "${splitByState.value}",
                            modifier = modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last) {
                                    splitByState.value = splitByState.value + 1

                                    totalPerPersonState.value =
                                        calculateTotalAmountPerPerson(
                                            totalBillAmount = totalBillAmount.value.toDouble(),
                                            splitByPerson = splitByState.value,
                                            tipPercentage = tipPercentage
                                        )

                                }
                            })
                    }
                }

                // Tip Row
                Row(
                    modifier = modifier
                        .padding(
                            horizontal = 3.dp,
                            vertical = 12.dp
                        )
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(200.dp))

                    Text(
                        text = "€ ${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(text = "$tipPercentage %")

                    Spacer(modifier = Modifier.height(4.dp))

                    //Slider
                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newValue ->
                            sliderPositionState.value = newValue
                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBillAmount = totalBillAmount.value.toDouble(),
                                    tipPercentage = tipPercentage
                                )

                            totalPerPersonState.value =
                                calculateTotalAmountPerPerson(
                                    totalBillAmount = totalBillAmount.value.toDouble(),
                                    splitByPerson = splitByState.value,
                                    tipPercentage = tipPercentage
                                )

                        },
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp
                        ),
                        steps = 50,
                        onValueChangeFinished = {

                        })
                }

            } else {
                Box() {

                }
            }
        }
    }
}


//@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipCalculatorTheme {
        MyApp() {
            TopHeader()
        }
        MainContentView()
    }
}
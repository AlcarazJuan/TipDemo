package com.example.tipdemo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tipdemo.components.InputField
import com.example.tipdemo.ui.theme.TIPDEMOTheme
import com.example.tipdemo.utils.CalculateTotalTip
import com.example.tipdemo.utils.calculateTotalPerson
import com.example.tipdemo.widgets.RoundIconButton

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.padding(15.dp)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainContent()
                }

            }

        }
    }
   // @Preview (showBackground = true)
    @Composable
    fun DefaultPreview () { //content we are passing a TEXT
        TIPDEMOTheme {
            MyApplication {
                Text("Whut")
            }

        }


    }
    // CONTAINER FUNCTION
    @Composable
    fun MyApplication(content: @Composable () -> Unit) { //content we are passing a TEXT
        TIPDEMOTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colors.background,

            ) {
                //PASSING CONTENT HERE
                content()

            }
        }
    }
}

//@Preview(showBackground = false )
@Composable
fun TopHeader(totalPerPerson: Double = 1250.44){

    val total = "%.2f".format(totalPerPerson)

    Surface(modifier = Modifier
        .fillMaxWidth(0.9f)
        .height(150.dp)
        .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7) ){
        //.clip(shape = RoundedCornerShape(corner = CornerSize(540.dp)))) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {

            Text(
                text = "Total Per Person",
                style =
                MaterialTheme.typography.h5)
            Text(
                text = "$${total}",

                style =
                MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold)

        }
    }

}
@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent(){
//BECOMES TRAILING LAMBDA
BillForm(){
    billAmt ->
    Log.d("AMT", "MainContent: ${billAmt.toInt() * 100}")
}


}

//STATE HOSTING --
//HOST INPUT FIELD TO PASS THE VALUE WE GET THROUGH
@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {}
){
    // create a state to recall numbers

    val totalBillState = remember{
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val splitByState = remember {
        mutableStateOf(1)
    }
    val sliderPositionState  = remember{
        mutableStateOf(0f)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }



    val tipPercentage = (sliderPositionState.value * 100).toInt()

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,


        ) {
            TopHeader(totalPerPerson = totalPerPersonState.value)
            InputField(
                valueState = totalBillState ,
                labelId = "Enter Bill", //Enter Amount
                enabled = true ,
                isSingleLine = true,
                onAction = KeyboardActions{
                    if(!validState) return@KeyboardActions

                    onValChange( totalBillState.value.trim() ) //why?
                    keyboardController?.hide()



                }
            )

            if(validState){
                Row(
                    modifier = Modifier
                    .padding(3.dp),
                horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split",
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))

                    Spacer(modifier = Modifier.width(120.dp))
                    Row(modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End) {
                    RoundIconButton(
                        imageVector = Icons.Default.Remove,
                        onClick = {
                            if(splitByState.value > 1){
                                splitByState.value -= 1

                                totalPerPersonState.value = calculateTotalPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage)
                            }else{
                                Log.d("Remove", "BillForm: Invalid")
                            }


                        })

                    }
                    Text(text = "${splitByState.value}",
                    modifier = Modifier
                        .padding(start = 9.dp, end = 9.dp)
                        .align(Alignment.CenterVertically))


                    RoundIconButton(
                        imageVector = Icons.Default.Add,
                        onClick = {  splitByState.value += 1
                            totalPerPersonState.value = calculateTotalPerson(
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value,
                                tipPercentage = tipPercentage)})

                }
                //TIP ROW

            Row(
                modifier = Modifier
                    .padding(horizontal = 3.dp, vertical = 12.dp)

            ) {
                Text(text = "Tip",
                modifier = Modifier.align(alignment = Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(200.dp) )
                Text(text = "$${tipAmountState.value}",
                    modifier = Modifier.align(alignment = Alignment.CenterVertically))


            }
            Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text =  "${tipPercentage} %") //random Input
                Spacer(modifier = Modifier.height(15.dp))
                //Slider
                Slider(
                    value = sliderPositionState.value,
                    onValueChange =  { newVal ->
                        sliderPositionState.value = newVal
                        // Log.d("SliderChange", "BillForm: $newVal")
                        tipAmountState.value =
                            CalculateTotalTip(
                                totalBill = totalBillState.value.toDouble(),
                                tipPercentage = tipPercentage )
                        totalPerPersonState.value = calculateTotalPerson(
                            totalBill = totalBillState.value.toDouble(),
                            splitBy = splitByState.value,
                            tipPercentage = tipPercentage)
                    },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    steps = 7,
                    onValueChangeFinished = {
                        Log.d("test", "BillForm: rich? ")
                    }
                )

            }

            }else {
                Box() {
                }
            }



                }
            }

    }






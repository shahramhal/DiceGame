package com.example.dicegame_v10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

class Start : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GUI()
        }
    }
}

@Composable
fun GUI() {
    var humanScore by remember { mutableStateOf(0) }
    var compScore by remember { mutableStateOf(0) }
    var humanDice by remember { mutableStateOf(List(5) { 1 }) }
    var compDice by remember { mutableStateOf(List(5) { 1 }) }
    var rerolleft by remember { mutableStateOf(2) }
    var winnerMessage by remember { mutableStateOf("") }
    var canScore by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var selectedDice by remember { mutableStateOf(List(5) { false }) }
    var computerRerollsLeft by remember { mutableStateOf(2) } // Track computer's rerolls



    val diceImages = listOf(
        R.drawable.dice1, R.drawable.dice2, R.drawable.dice3,
        R.drawable.dice4, R.drawable.dice5, R.drawable.dice6
    )

    fun Diceroll(): List<Int> {
        return List(5) { Random.nextInt(1, 7) }
    }

    fun computerReroll(dice: List<Int>): List<Int> {
        val keepDice = List(5) { Random.nextBoolean() } // Randomly decide which dice to keep
        return dice.mapIndexed { index, value ->
            if (keepDice[index]) value // Keep the dice
            else Random.nextInt(1, 7) // Reroll the dice
        }
    }
    fun Score() {

        humanScore += humanDice.sum()
        compScore += compDice.sum()
        rerolleft = 2
        selectedDice = List(5){false}
    }

    fun checkWinner() {
        if (humanScore >= 101 || compScore >= 101) {
            winnerMessage = if (humanScore >= 101) "You win" else "You lose"
        }
    }

    fun resetGame() {
        humanScore = 0
        compScore = 0
        humanDice = List(5) { 1 }
        compDice = List(5) { 1 }
        rerolleft = 2
        winnerMessage = ""
        canScore = false
        errorMessage = ""
        computerRerollsLeft = 2

    }



    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current

        // Display Scores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Human : $humanScore", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Computer : $compScore", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Human Dice
        Text("Your dice", fontSize = 18.sp)
        Row {
            humanDice.forEachIndexed {index, value ->
                val isSelected= selectedDice[index]
                Box( modifier = Modifier.clickable {
                    selectedDice=selectedDice.toMutableList().also {
                        it[index]=!it[index] // toggle selection
                    }

                }
                    // Highlight selected dice
                    .border(2.dp, if (isSelected) Color.Green else Color.Transparent)
                    .padding(4.dp)

                ){
                    Image(
                        painterResource(id = diceImages[value - 1]),
                        contentDescription = "Dice $value",
                        modifier = Modifier.size(64.dp)
                    )

                }

            }
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Computer Dice
        Text("Computer", fontSize = 18.sp)
        Row {
            compDice.forEach { value ->
                Image(
                    painterResource(id = diceImages[value - 1]),
                    contentDescription = "Dice $value",
                    modifier = Modifier.size(66.dp)
                )
            }
        }
        Row {
            // Throw Button
            Button(
                onClick = {
                    //modify human dice to keep clicked dice
                    if (rerolleft >= 0) {
                        humanDice = humanDice.mapIndexed { index, value ->
                            if (selectedDice[index]) value
                            else Random.nextInt(1, 7)
                        }
                        // Comp first turn
                        compDice = Diceroll()
                        canScore = true
                        rerolleft--
                        // computer wait until player finish his turn
                        if (rerolleft < 0) {
//                            while (computerRerollsLeft>=0){
//                                compDice=computerReroll(compDice)
//                                computerRerollsLeft--
//                            }
                            Score()
                            checkWinner()
                            canScore=false

                        }
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Throw (${rerolleft + 1}/3)")
            }
            // Score Button
            Button(
                onClick = {
                    if (canScore) {
                        // Computer uses its remaining rerolls
                        while (computerRerollsLeft >= 0) {
                            compDice = computerReroll(compDice)
                            computerRerollsLeft--
                        }
                        Score()
                        checkWinner()
                        canScore = false
                    } else {
                        errorMessage = "Please throw the dice"
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Score")
            }
            Button(
                onClick = { resetGame() },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Reset")
            }
        }
        Row {
            Button(
                onClick = {
                    // Handle "Go back" action#
                    (context as ComponentActivity).finish()

                }
            ) {
                Text("Go back")
            }
        }

        // Display the error message if any
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        // Display the winner message if any
        if (winnerMessage.isNotEmpty()) {
            Text(text = winnerMessage, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
    }

    // Show the AlertDialog when showDialog is true

}
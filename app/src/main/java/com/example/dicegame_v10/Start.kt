
package com.example.dicegame_v10

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegame_v10.ui.theme.DiceGame_V10Theme
import kotlin.random.Random

/**
 * Computer Strategy Documentation:
 *
 * This implementation uses an advanced strategy for the computer player instead of purely random behavior.
 * The strategy adapts based on the current state of the game by considering:
 *
 * 1. The quality of the current roll (high values vs low values)
 * 2. The point gap between the computer and human player
 * 3. How close the computer is to reaching the target score
 *
 * Decision-making logic:
 * - If the current dice total is high (>20), keep all dice to secure good points
 * - If we're falling behind the human player and have a low roll, reroll aggressively, keeping only high-value dice
 * - If we're close to the target score, play more conservatively, keeping dice with values 4 or higher
 * - In other situations, make strategic decisions based on the specific values shown
 *
 * Justification:
 * This strategy significantly outperforms random play because:
 * 1. It recognizes and keeps high-value rolls rather than rerolling them by chance
 * 2. It adjusts risk-taking based on the state of the game (score difference and proximity to target)
 * 3. It makes more optimal choices about which specific dice to keep
 *
 * Advantages:
 * - Adaptive to the game state and current roll
 * - Risk assessment based on multiple factors
 * - Balances between aggressive play when behind and conservative play when ahead
 *
 * Disadvantages:
 * - Not perfect - cannot predict future rolls
 * - May sometimes make suboptimal choices in edge cases
 * - Does not account for the human player's strategy or patterns
 */

class Start : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DiceGame_V10Theme {
                GUI()
            }
        }
    }
}

@Composable
fun GUI() {
    var humanScore by rememberSaveable { mutableStateOf(0) }
    var compScore by rememberSaveable { mutableStateOf(0) }
    var humanDice by rememberSaveable { mutableStateOf(List(5) { 1 }) }
    var compDice by rememberSaveable { mutableStateOf(List(5) { 1 }) }
    var rerolleft by rememberSaveable { mutableStateOf(2) }
    var isFirstThrow by rememberSaveable { mutableStateOf(true) }
    var winnerMessage by rememberSaveable { mutableStateOf("") }
    var canScore by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var selectedDice by rememberSaveable { mutableStateOf(List(5) { false }) }
    var computerRerollsLeft by rememberSaveable { mutableStateOf(2) } // Track computer's rerolls
    var showMessage by rememberSaveable { mutableStateOf(false) }
    var showWinnerDialog by rememberSaveable { mutableStateOf(false) }
    var playerWon by rememberSaveable { mutableStateOf(false) }
    var lockedDice by rememberSaveable { mutableStateOf(List(5) { false }) }
    var gameActive by rememberSaveable { mutableStateOf(true) } //task 8:  Add flag to track if game is active

    // Task9 :  Track attempts for each player
    var humanAttempts by rememberSaveable { mutableStateOf(0) }
    var compAttempts by rememberSaveable { mutableStateOf(0) }
    //For tie-breaking
    var isTieBreaking by rememberSaveable { mutableStateOf(false)}
    var showTieBreakDialog by rememberSaveable{ mutableStateOf(false) }
    var tieBreakHumanScore by rememberSaveable { mutableStateOf(0) }
    var tieBreakCompScore by rememberSaveable { mutableStateOf(0) }
    var tieBreakRound by rememberSaveable{ mutableStateOf(0) }
    // Task10  Target Score
    var targetScore by rememberSaveable { mutableStateOf(101) }
    var targetInput by rememberSaveable { mutableStateOf("101") }
    var isTargetSet by rememberSaveable { mutableStateOf(false) }
    var showTargetDialog by rememberSaveable { mutableStateOf(false) }
    var firstTimeSetup by rememberSaveable { mutableStateOf(true) }
    // Task 11
    var humanTotalWins by rememberSaveable { mutableStateOf(0) }
    var compTotalWins by rememberSaveable { mutableStateOf(0) }
    // Task 12
    var useSmartStrategy by rememberSaveable { mutableStateOf(true) } // Use smart strategy by default

    val diceImages = listOf(
        R.drawable.dice1, R.drawable.dice2, R.drawable.dice3,
        R.drawable.dice4, R.drawable.dice5, R.drawable.dice6
    )

    fun Diceroll(): List<Int> {
        return List(5) { Random.nextInt(1, 7) }
    }
    fun Score() {
        humanScore += humanDice.sum()
        compScore += compDice.sum()
        rerolleft = 2
        selectedDice = List(5){false}
        humanAttempts++
        compAttempts++
    }

    // Task 12: Optimized computer strategy
    fun computerStrategy(dice: List<Int>, humanScore: Int, compScore: Int, targetScore: Int): List<Boolean> {
        // Current sum of computer's dice
        val currentSum = dice.sum()

        // Score difference between players
        val scoreGap = humanScore - compScore

        // How many points needed to reach target
        val remainingToTarget = targetScore - compScore

        // List of dice to keep (true = keep, false = reroll)
        val keepDice = MutableList(5) { false }

        // If current dice total is very good (more than 20), keep all dice
        if (currentSum > 20) {
            return List(5) { true }
        }

        // If we're behind the human player and have low values, try to reroll aggressively
        if (scoreGap > 10 && currentSum < 15) {
            // Keep only high-value dice (5 or 6)
            for (i in dice.indices) {
                keepDice[i] = dice[i] >= 5
            }
            return keepDice
        }

        // If close to target, play more conservatively
        if (remainingToTarget <= 25) {
            // Keep dice with values 4 and above
            for (i in dice.indices) {
                keepDice[i] = dice[i] >= 4
            }
            return keepDice
        }

        // For other situations, make specific value-based decisions
        // Keep any 6s, and keep 5s unless we need a big score boost
        for (i in dice.indices) {
            when (dice[i]) {
                6 -> keepDice[i] = true
                5 -> keepDice[i] = scoreGap <= 15
                4 -> keepDice[i] = currentSum >= 15 || remainingToTarget < 40
                else -> keepDice[i] = false // Always reroll low values (1-3)
            }
        }

        return keepDice
    }

    fun computerReroll(dice: List<Int>): List<Int> {
        val keepDice = if (useSmartStrategy) {
            // Use the optimized strategy
            computerStrategy(dice, humanScore, compScore, targetScore)
        } else {
            // Use the original random strategy
            List(5) { Random.nextBoolean() }
        }

        // Apply the decision by keeping or rerolling dice
        return dice.mapIndexed { index, value ->
            if (keepDice[index]) value // Keep the dice
            else Random.nextInt(1, 7) // Reroll the dice
        }
    }




    fun checkWinner() {
        // Task 10 : Target Score
        if (humanScore >= targetScore || compScore >= targetScore) {
            // Task 9: Both players have reached the target
            if (humanScore >= targetScore && compScore >= targetScore) {
                // Check if they reached it in the same number of attempts
                if (humanAttempts == compAttempts) {
                    //check if they have the same score
                    if (humanScore == compScore) {
                        //it is a tie -start tie-breaking process
                        isTieBreaking = true
                        showTieBreakDialog = true
                        tieBreakRound = 0
                        return
                    }
                    //both reached target in same attempts but different scores
                    playerWon = (humanScore > compScore)
                } else {
                    //first to reach the target wins
                    playerWon = (humanAttempts < compAttempts)
                }

            } else {
                playerWon = (humanScore >= targetScore)
            }
            winnerMessage = if (playerWon) "You win" else "You lose"
            //Task 11
            if (playerWon) humanTotalWins++ else compTotalWins++
            gameActive = false // Task8:  Set game as inactive when there's a winner
            showWinnerDialog = true
        }
    }

    fun handleTieBreak(){
        //TASK 9:  Roll dice for both players (no rerolls allowed in tie-breaking)
        humanDice=Diceroll()
        compDice=Diceroll()

        tieBreakHumanScore=humanDice.sum()
        tieBreakCompScore=compDice.sum()

        tieBreakRound++

        // Check if tie is broken
        if(tieBreakHumanScore!=tieBreakCompScore){
            isTieBreaking=false
            playerWon= (tieBreakHumanScore>tieBreakCompScore)
            winnerMessage= if (playerWon) "You win" else "You lose "
            // Task 11
            if (playerWon) humanTotalWins++ else compTotalWins++
            showTieBreakDialog =false
            showWinnerDialog=true
            gameActive=false
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
        showWinnerDialog = false
        humanAttempts=0
        compAttempts=0
        isTieBreaking=false
        showTieBreakDialog= false
        gameActive = true // Ensure game is active after reset
        // Modified: Don't reset isTargetSet to allow target score to persist
//         isTargetSet = false
//         targetInput = "101"
//         targetScore = 101
    }

    // returning to the main module
    fun returnToMainScreen(context: ComponentActivity) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        context.finish()
    }
    // Only show target score setting on first launch
    if (firstTimeSetup) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Set Target Score", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = targetInput,
                onValueChange = { targetInput = it },
                label = { Text("Target Score") },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Add option to select strategy (Task 12)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Checkbox(
                    checked = useSmartStrategy,
                    onCheckedChange = { useSmartStrategy = it }
                )
                Text("Use optimized computer strategy")
            }

            Button(onClick = {
                val inputScore = targetInput.toIntOrNull()
                if (inputScore != null && inputScore > 0) {
                    targetScore = inputScore
                    isTargetSet = true
                    firstTimeSetup = false  // Mark that initial setup is complete
                }
            }) {
                Text("Start Game")
            }
        }
        return
    }

    // Task 10 Target Score
    // Target Score dialog for changing between games
    if (showTargetDialog) {
        AlertDialog(
            onDismissRequest = { showTargetDialog = false },
            title = { Text("Change Target Score") },
            text = {
                Column {
                    TextField(
                        value = targetInput,
                        onValueChange = { targetInput = it },
                        label = { Text("New Target Score") },
                        singleLine = true
                    )

                    // Add option to change strategy
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Checkbox(
                            checked = useSmartStrategy,
                            onCheckedChange = { useSmartStrategy = it }
                        )
                        Text("Use optimized computer strategy")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val inputScore = targetInput.toIntOrNull()
                    if (inputScore != null && inputScore > 0) {
                        targetScore = inputScore
                        isTargetSet = true
                    }
                    showTargetDialog = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = { showTargetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        // Task 11
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "H:$humanTotalWins / C:$compTotalWins",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // Display which strategy is being used (Task 12)
            Text(
                text = if (useSmartStrategy) "Smart Strategy" else "Random Strategy",
                fontSize = 16.sp,
                color = if (useSmartStrategy) Color.Blue else Color.Gray
            )
        }

        // Display Scores
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Human : $humanScore", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            // Make target score clickable only when game is not active
            Text(
                text = "Target: $targetScore",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(enabled = !canScore && gameActive) {
                    // Only allow changing target when the game isn't in progress
                    if (!canScore && gameActive) {
                        showTargetDialog = true
                    }
                }
            )
            Text("Computer : $compScore", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Human Dice
        Text("Your dice", fontSize = 18.sp)
        Row {
            humanDice.forEachIndexed {index, value ->
                val isSelected= selectedDice[index]
                // Only allow dice selection //if game is active and not tie
                Box(modifier = Modifier.clickable(enabled = !isFirstThrow&& !lockedDice[index] && gameActive && !isTieBreaking) {
                    selectedDice = selectedDice.toMutableList().also {
                        it[index] = !it[index]
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
                Box(modifier = Modifier.padding(4.dp)){


                    Image(
                        painterResource(id = diceImages[value - 1]),
                        contentDescription = "Dice $value",
                        modifier = Modifier.size(66.dp)
                    )
                }
            }
        }
        Row {
            // Throw Button
            Button(
                onClick = {


                    if (isTieBreaking){
                        // Section9: Handle tie-breaking throw (both players roll all dice, no rerolls)
                        handleTieBreak()
                    }
                    else if (gameActive && rerolleft >= 0) { // Only allow throwing if game is active
                        // Reset computer's rerolls at the start of a new round
                        if (rerolleft == 2) {
                            computerRerollsLeft = 2
                        }

                        if (rerolleft == 2) {
                            selectedDice = List(5) { false }
                            isFirstThrow = true
                        }


                        if (isFirstThrow) {
                            selectedDice = List(5) { false }
                            isFirstThrow =
                                false
                        }

                        humanDice = humanDice.mapIndexed { index, value ->
                            if (selectedDice[index]) value
                            else Random.nextInt(1, 7)
                        }
                        // Comp first turn
                        compDice = Diceroll()
                        canScore = true
                        rerolleft--
                        // if 0 attempts left
                        if (rerolleft < 0) {
                            while (computerRerollsLeft>=0){
                                compDice=computerReroll(compDice)
                                computerRerollsLeft--
                            }
                            Score()
                            checkWinner()
                            canScore=false
                            isFirstThrow=true                        }
                    }
                },
                modifier = Modifier.padding(16.dp),
                // Disable button when game is over or 0 attempt left
                enabled = gameActive && rerolleft >= 0
            ) {
                if (isTieBreaking) {
                    Text(text = "Throw for tie-break")
                } else {
                    Text(text = "Throw (${rerolleft + 1}/3)")
                }
            }
            // Score Button
            Button(
                onClick = {
                    // Only allow scoring if game is active and not tie
                    if (gameActive && !isTieBreaking) {
                        if (canScore) {
                            // Computer uses its remaining rerolls
                            while (computerRerollsLeft >= 0) {
                                compDice = computerReroll(compDice)
                                computerRerollsLeft--
                            }
                            Score()
                            checkWinner()
                            canScore = false
                            isFirstThrow = true
                        } else {
                            showMessage = true
                        }
                    }
                },
                modifier = Modifier.padding(16.dp),
                enabled = gameActive // Disable button when game is over
            ) {
                Text(text = "Score")
            }
            
        }
        Row {
            Button(
                onClick = {
                    // Handle "Go back" action
                    (context as ComponentActivity).finish()
                }
            ) {
                Text("Go back")
            }
        }
        Row {
            Text(
                text = "Attempts ${rerolleft + 1}/3",
                fontSize = 18.sp
            )
        }


        // Display the error message if any
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        // "Please throw the dice" Error Dialog
        if (showMessage) {
            AlertDialog(
                onDismissRequest = { showMessage = false },
                title = { Text(text = "Error") },
                text = {
                    Text(
                        text = """
                        Please throw the dice
                        """.trimIndent()
                    )
                },
                confirmButton = {
                    Button(onClick = { showMessage = false }) {
                        Text(text = "OK")
                    }
                }
            )
        }
        if (showTieBreakDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = "It's a Tie!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Both players reached ${humanScore} points in ${humanAttempts} attempts.\n\n" +
                                    "Keep throwing the dice until someone wins!\n\n" +
                                    if (tieBreakRound > 0) "Round $tieBreakRound:\nHuman: $tieBreakHumanScore\nComputer: $tieBreakCompScore" else "",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        showTieBreakDialog = false
                    }) {
                        Text(text = "Continue")
                    }
                }
            )
        }


        // Winner Dialog with appropriate color
        if (showWinnerDialog) {
            AlertDialog(
                onDismissRequest = { },
                title = {
                    Text(
                        text = winnerMessage,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (playerWon) Color.Green else Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Human: $humanScore\nComputer: $compScore",
                            textAlign = TextAlign.Center
                        )
                    }
                },
                confirmButton = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = {
                            resetGame()
                        }) {
                            Text(text = "Play Again")
                        }
                        Button(onClick = {
                            // Return to main screen
                            (context as? ComponentActivity)?.let { returnToMainScreen(it) }
                        }) {
                            Text(text = "Main Menu")
                        }
                    }
                }
            )
        }
    }
}

package com.example.dicegame_v10

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegame_v10.ui.theme.DiceGame_V10Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiceGame_V10Theme{
                HomeScreen()

            }
        }
    }
}


@Composable
fun HomeScreen() {

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val context = LocalContext.current
        Text(
            text = "Welcome to Dice Game",
            fontSize = 30.sp,
            modifier = Modifier.padding(8.dp)
        )
        // 1.Create New Game button
        Button(
            onClick = {
                //By the user will be presented with the game screen which
                //they interact with.
                val i = Intent(context, Start::class.java)
                context.startActivity(i)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "New Game")
        }
        // 2.Create About button
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "About")
        }

        // 2.Add a popup window
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "About the Author") },
                text = {
                    Text(
                        text = """
                            Name: Shahram Halimzoda
                            Student ID: w2064610
                            
                            I confirm that I understand what plagiarism is and have read and
                            understood the section on Assessment Offences in the Essential
                            Information for Students. The work that I have submitted is
                            entirely my own. Any work from other authors is duly referenced
                            and acknowledged.
                        """.trimIndent()
                    )
                },
                confirmButton = {
                    Button(onClick = { showDialog = false }) {
                        Text(text = "OK")
                    }
                }
            )
        }
    }
}

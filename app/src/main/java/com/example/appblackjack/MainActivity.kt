package com.example.appblackjack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appblackjack.ui.theme.AppBlackJackTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppBlackJackTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BlackJackApp()
                }
            }
        }
    }
}

@Composable
fun BlackJackApp() {

    val scope = rememberCoroutineScope()

    var wins by remember {
        mutableStateOf(0)
    }
    var draws by remember {
        mutableStateOf(0)
    }
    var losses by remember {
        mutableStateOf(0)
    }
    var dealerCards by remember {
        mutableStateOf(
            listOf(
                (1..13).random(),
                (1..13).random(),
            )
        )
    }
    var showDealerHand by remember {
        mutableStateOf(false)
    }

    var turnResult by remember {
        mutableStateOf("")
    }
    var endOfTurn by remember {
        mutableStateOf(false)
    }

    var playerHand by remember {
        mutableStateOf(
            listOf(
                (1..13).random(),
                (1..13).random(),
            )
        )
    }

    var playerPoints by remember {
        mutableStateOf(0)
    }
    var dealerPoints by remember {
        mutableStateOf(0)
    }

    val checkwinner: () -> Unit = {
        showDealerHand = true
        scope.launch {
            if(playerPoints>21){
                turnResult = "You lost by esceeding 21 points!"
                losses++
            }else {
                dealerPoints = 0
                var dealerAces = 0
                dealerCards.forEach {
                    if (it == 1){
                        dealerPoints += 11
                        dealerAces++
                    }else if (it<10)
                        dealerPoints += it
                    else
                        dealerPoints += 10
                }
                while (dealerPoints < 17) {
                    delay(200)
                    val newCard = (1..13).random()
                    if (newCard == 1){
                        dealerPoints += 11
                        dealerAces++
                    }else if (newCard<10)
                        dealerPoints += newCard
                    else
                        dealerPoints += 10
                    dealerCards = dealerCards + newCard
                    if (dealerPoints >21 && dealerAces > 0){
                        dealerPoints -= 10
                        dealerAces--
                    }
                }
                
            }
            delay(100)
            turnResult = if (playerPoints == 21){
                "BlackJack "
            }else{
                ""
            }
            if (dealerPoints>21){
                turnResult+="You Won, dealer execeed 21 points!"
                wins++
            }else if (dealerPoints == playerPoints){
                turnResult+= "Its a Draw, both got $playerPoints points"
                draws++
            }else if(dealerPoints< playerPoints){
                turnResult+= "You Won, $playerPoints x $dealerPoints "
                wins++
            }else{
                turnResult+= "You Lost, $playerPoints x $dealerPoints"
                losses++
            }
        }
    }

    val hit = {
        playerHand = playerHand + (1 .. 13).random()
        playerPoints = 0
        var playersAces = 0
        playerHand.forEach {
            if (it == 1){
                playerPoints += 11
                playersAces++
            }else if (it<10)
                playerPoints += it
            else
                playerPoints += 10
        }
        if (playerPoints>=21) {
            if (playerPoints > 21 && playersAces >0){
                playerPoints -=10
                playersAces--
            }else{
                endOfTurn=true
                checkwinner()
            }
        }
    }

    val hold = {
        playerPoints = 0
        var playersAces = 0
        playerHand.forEach {
            if (it == 1){
                playerPoints += 11
                playersAces++
            }else if (it<10)
                playerPoints += it
            else
                playerPoints += 10
        }
        endOfTurn=true
        checkwinner()
    }

    val playAgain = {
        playerHand = listOf((1..13).random(),(1..13).random())
        dealerCards = listOf((1..13).random(),(1..13).random())
        showDealerHand = false
        endOfTurn = false
        playerPoints=0
        dealerPoints=0
        turnResult = ""
    }

    MainScreen(
        wins = wins,
        draws = draws,
        losses = losses,
        dealerCards = dealerCards,
        showDealerHand = showDealerHand,
        turnResult = turnResult,
        endOfTurn = endOfTurn,
        playerHand = playerHand,
        hold = hold,
        hit = hit,
        playAgain = playAgain,
    )
}

@Composable
fun MainScreen(
    wins: Int,
    draws: Int,
    losses: Int,
    dealerCards: List<Int>,
    showDealerHand: Boolean,
    turnResult: String,
    endOfTurn: Boolean,
    playerHand: List<Int>,
    hold: () -> Unit,
    hit: () -> Unit,
    playAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize(),

            )
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(0.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            ScoreBoard(
                wins = wins,
                draws = draws,
                losses = losses,
                modifier = modifier,
            )
            Cards(dealerCards, showAllCards = showDealerHand, modifier = modifier)
            if (endOfTurn){
                RestartGame(turnResult = turnResult, playAgain = playAgain ,modifier = modifier)
            }

            Cards(playerHand, showAllCards = true, modifier = modifier)

            PlayersAction(modifier=modifier, hold = hold, hit = hit , endOfTurn= endOfTurn)
        }
    }
}

@Composable
fun ScoreBoard(wins: Int, draws: Int, losses: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            text = "Wins: $wins",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Draws: $draws",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Loss: $losses",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

    }
}

@Composable
fun Cards(cards: List<Int>, showAllCards: Boolean, modifier: Modifier = Modifier) {
    val offset = 25
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        cards.forEachIndexed { index, card ->
            val cardDrawble = if (index == 1 && !showAllCards) {
                R.drawable.back
            } else when (card) {
                1 -> R.drawable.ace
                2 -> R.drawable.two
                3 -> R.drawable.three
                4 -> R.drawable.four
                5 -> R.drawable.five
                6 -> R.drawable.six
                7 -> R.drawable.seven
                8 -> R.drawable.eight
                9 -> R.drawable.nine
                10 -> R.drawable.ten
                11 -> R.drawable.jack
                12 -> R.drawable.queen
                else -> R.drawable.king
            }
            Image(
                painter = painterResource(id = cardDrawble),
                contentDescription = "$card",
                contentScale = ContentScale.Fit,
                modifier = modifier
                    .height(234.dp)
                    .width(168.dp)
                    .offset(x = (offset * index).dp)
            )
        }
    }
}

@Composable
fun RestartGame(
    turnResult: String,
    playAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = turnResult,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Button(onClick = playAgain) {
            Text(text = "Play Again")

        }
    }

}

@Composable
fun PlayersAction(
    hold: () -> Unit,
    hit: () -> Unit,
    endOfTurn: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = hold, enabled = !endOfTurn) {
            Text(text = "Hold")
        }
        Button(onClick = hit, enabled = !endOfTurn) {
            Text(text = "Hit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppBlackJackTheme {
        BlackJackApp()
    }
}
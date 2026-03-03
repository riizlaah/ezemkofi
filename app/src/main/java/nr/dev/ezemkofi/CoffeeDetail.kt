package nr.dev.ezemkofi

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

@Composable
fun CoffeeDetailScreen(modifier: Modifier, controller: NavHostController, coffeeId: Int) {
    var coffee by remember { mutableStateOf<Coffee?>(null) }
    var qty by remember { mutableIntStateOf(1) }
    val options = mapOf("S" to 0.85f, "M" to 1f, "L" to 1.15f)
    var selectedOpt by remember { mutableStateOf("M") }
    val radius: Dp = 200.dp

    LaunchedEffect(Unit) {
        if (coffee == null) coffee = HttpClient.getCoffeeById(coffeeId)
    }

    if (coffee == null) return
    Box(modifier) {
        Box(
            Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.chevron_left_regular_24),
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xff37765D))
                        .padding(6.dp)
                        .size(18.dp)
                        .clickable(onClick = { controller.popBackStack() }),
                    contentDescription = "Back"
                )
                TextP(
                    "Details",
                    modifier = Modifier.fillMaxWidth(),
                    alignment = TextAlign.Center,
                    weight = FontWeight.SemiBold,
                    color = Color.White,
                    size = MaterialTheme.typography.headlineMedium.fontSize
                )
            }
            LazyColumn(
                Modifier
                    .weight(1f)
                    .padding(24.dp)
            ) {
                item {
                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(radius * 2.5f)
                            .padding(6.dp)
                    ) {
                        NetImage(
                            HttpClient.ADDRESS + "images/${coffee!!.imgPath}",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .width(radius)
                        )
                        Box(modifier = Modifier.align(Alignment.CenterEnd).size(40.dp).clip(CircleShape).background(Color(0xfff9853a))) {
                            TextP("${coffee!!.rating}", alignment = TextAlign.Center, color = Color.White, weight = FontWeight.Medium, modifier = Modifier.align(Alignment.Center))
                        }
                        var i = 0
                        options.forEach { (k, v) ->
                            val angle = (230f + 80f * (i.toFloat() / (options.size - 1))).toDouble()

                            val x = ((radius * 0.85f) * cos(Math.toRadians(angle)).toFloat())
                            val y = -((radius * 0.85f) * sin(Math.toRadians(angle)).toFloat())

                            if (selectedOpt == k) {
                                Button(
                                    onClick = {selectedOpt = k},
                                    modifier = Modifier
                                        .offset(x, y)
                                        .align(Alignment.Center)
                                        .size(40.dp),
                                    contentPadding = PaddingValues(6.dp),
                                    shape = CircleShape
                                ) {
                                    TextP(k, color = Color.White)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {selectedOpt = k},
                                    modifier = Modifier
                                        .offset(x, y)
                                        .align(Alignment.Center)
                                        .size(40.dp),
                                    contentPadding = PaddingValues(6.dp),
                                    shape = CircleShape,
                                    border = BorderStroke(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary,
                                    )
                                ) {
                                    TextP(k)
                                }
                            }
                            i += 1
                        }
                    }
                }
                item {
                    TextP(
                        coffee!!.name,
                        weight = FontWeight.Bold,
                        size = MaterialTheme.typography.headlineMedium.fontSize,
                        modifier = Modifier.fillMaxWidth(),
                        softWrap = false
                    )
                    Spacer(Modifier.height(6.dp))
                    TextP(
                        coffee!!.description,
                        size = MaterialTheme.typography.bodySmall.fontSize,
                        color = Color.Gray
                    )
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextP(
                            "$${coffee!!.price}",
                            weight = FontWeight.SemiBold,
                        )
                        Row(
                            Modifier
                                .border(1.dp, Color.LightGray, CircleShape)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                painterResource(R.drawable.minus_regular_24),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Decrease",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable(onClick = {
                                        qty = max(1, qty - 1)
                                    })
                            )
                            TextP("$qty", modifier = Modifier.padding(horizontal = 12.dp))
                            Icon(
                                painterResource(R.drawable.plus_regular_24),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Increase",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable(onClick = {
                                        qty += 1
                                    })
                            )
                        }
                    }
                    Button(
                        onClick = {},
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextP("ADD TO CART", weight = FontWeight.SemiBold, color = Color.White)
                    }
                }

            }
        }
    }
}
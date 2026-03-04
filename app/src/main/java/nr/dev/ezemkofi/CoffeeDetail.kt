package nr.dev.ezemkofi

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
    val options = listOf(CoffeeSize("S", 0.85), CoffeeSize("M", 1.0), CoffeeSize("L", 1.15))
    var selectedOpt by remember { mutableIntStateOf(1) }
    var targetRotation by remember { mutableFloatStateOf(0f) }
    val radius: Dp = 300.dp
    val imgSize by animateDpAsState(radius * options[selectedOpt].scale.toFloat(), animationSpec = tween(500))
    val imgRotation by animateFloatAsState(
        targetRotation,
        animationSpec = tween(500)
    )


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
                            .height(radius * 1.75f)
                            .padding(6.dp)
                    ) {
                        NetImage(
                            HttpClient.ADDRESS + "images/${coffee!!.imgPath}",
                            modifier = Modifier
                                .align(Alignment.Center)
                                .rotate(imgRotation)
                                .size(imgSize)
                        )
                        Box(modifier = Modifier.align(Alignment.CenterEnd).size(40.dp).clip(CircleShape).background(Color(0xfff9853a))) {
                            TextP("${coffee!!.rating}", alignment = TextAlign.Center, color = Color.White, weight = FontWeight.Medium, modifier = Modifier.align(Alignment.Center))
                        }
                        options.forEachIndexed { i, size ->
                            val angle = (230f + 80f * (i.toFloat() / (options.size - 1))).toDouble()

                            val x = ((radius * 0.65f) * cos(Math.toRadians(angle)).toFloat())
                            val y = -((radius * 0.65f) * sin(Math.toRadians(angle)).toFloat())
                            val rot = 45f + (i * -45f)

                            if (selectedOpt == i) {
                                Button(
                                    onClick = {selectedOpt = i},
                                    modifier = Modifier
                                        .offset(x, y)
                                        .align(Alignment.Center)
                                        .size(40.dp)
                                        .rotate(rot),
                                    contentPadding = PaddingValues(6.dp),
                                    shape = CircleShape
                                ) {
                                    TextP(size.name, color = Color.White)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        selectedOpt = i
                                        targetRotation += 360f
                                    },
                                    modifier = Modifier
                                        .offset(x, y)
                                        .align(Alignment.Center)
                                        .size(40.dp)
                                        .rotate(rot),
                                    contentPadding = PaddingValues(6.dp),
                                    shape = CircleShape,
                                    border = BorderStroke(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary,
                                    )
                                ) {
                                    TextP(size.name, color = MaterialTheme.colorScheme.primary)
                                }
                            }
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
                            "$%.2f".format(coffee!!.price * options[selectedOpt].scale),
                            weight = FontWeight.SemiBold,
                        )
                        Row(
                            Modifier
                                .border(1.dp, Color.LightGray, CircleShape)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
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
                        onClick = {
                            HttpClient.addToCart(CartItem(coffeeId = coffee!!.id, coffee = coffee!!, coffeeSize = options[selectedOpt], qty = qty))
                            controller.navigate(Route.CART)
                        },
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
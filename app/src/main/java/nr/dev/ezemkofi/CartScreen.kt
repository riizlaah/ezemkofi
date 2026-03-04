package nr.dev.ezemkofi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun CartScreen(modifier: Modifier, controller: NavHostController) {
    var loading by remember { mutableStateOf(false) }
    var errMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.chevron_left_regular_24),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .dropShadow(CircleShape, Shadow(4.dp, Color(0x40000000)))
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(6.dp)
                    .size(22.dp)
                    .clickable(onClick = { controller.popBackStack() }),
                contentDescription = "Back"
            )
            TextP(
                "Details",
                modifier = Modifier.fillMaxWidth(),
                alignment = TextAlign.Center,
                weight = FontWeight.SemiBold,
                size = MaterialTheme.typography.headlineSmall.fontSize
            )
        }
        if(HttpClient.carts.isEmpty()) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                TextP(
                    "Empty",
                    weight = FontWeight.SemiBold,
                    color = Color.Gray,
                    size = MaterialTheme.typography.headlineSmall.fontSize,
                    modifier = Modifier.fillMaxWidth(),
                    alignment = TextAlign.Center
                )
            }
            return@Column
        }
        LazyColumn(Modifier.weight(1f)) {
            items(HttpClient.carts) { item ->
                Row(
                    Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NetImage(
                        HttpClient.ADDRESS + "images/${item.coffee.imgPath}",
                        modifier = Modifier.width(116.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                TextP(
                                    item.coffee.name,
                                    weight = FontWeight.SemiBold,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                TextP(
                                    item.coffee.categoryName,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Icon(painterResource(R.drawable.delete), tint = Color.Gray, modifier = Modifier.padding(6.dp).size(18.dp).clickable(onClick = { HttpClient.delCartItem(item.coffeeId, item.coffeeSize.name)}), contentDescription = "Delete")
                        }
                        Spacer(Modifier.height(16.dp))
                        TextP(
                            "Size: ${item.coffeeSize.name}",
                        )
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
                                            HttpClient.addCartItemQty(
                                                item.coffeeId,
                                                item.coffeeSize.name,
                                                -1
                                            )
                                        })
                                )
                                TextP(
                                    "${item.qty}",
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                Icon(
                                    painterResource(R.drawable.plus_regular_24),
                                    tint = MaterialTheme.colorScheme.primary,
                                    contentDescription = "Increase",
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable(onClick = {
                                            HttpClient.addCartItemQty(
                                                item.coffeeId,
                                                item.coffeeSize.name,
                                                1
                                            )
                                        })
                                )
                            }
                            TextP("$%.2f".format(item.price), weight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
        if (HttpClient.carts.isNotEmpty()) {
            Column(Modifier.padding(6.dp)) {
                if (errMsg.isNotEmpty()) {
                    TextP(
                        errMsg,
                        color = Color.Red,
                        weight = FontWeight.Medium,
                        modifier = Modifier.fillMaxWidth(),
                        alignment = TextAlign.Center
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if(HttpClient.carts.isEmpty()) return@Button
                        scope.launch {
                            loading = true
                            errMsg = ""
                            if (!HttpClient.checkout()) {
                                errMsg = "Failed to checkout"
                            } else {
                                HttpClient.carts.clear()
                            }
                            loading = false
                        }
                    }) {
                    if (loading) {
                        LoadingIndicator()
                        return@Button
                    }
                    TextP("CHECKOUT", weight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
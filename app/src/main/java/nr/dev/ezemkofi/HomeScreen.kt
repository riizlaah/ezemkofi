package nr.dev.ezemkofi

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(modifier: Modifier, controller: NavHostController) {
    var search by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var categories by remember { mutableStateOf(listOf<Category>()) }
    var coffees by remember { mutableStateOf(listOf<Coffee>()) }
    var categorizedCoffees by remember { mutableStateOf(listOf<Coffee>()) }
    var topCoffees by remember { mutableStateOf(listOf<Coffee>()) }
    val pagerState = rememberPagerState() { coffees.size }

    LaunchedEffect(Unit) {
        if (categories.isEmpty()) {
            categories = HttpClient.getCategories()
            selectedCategory = categories[0]
            coffees = HttpClient.getCoffees()
            topCoffees = coffees.sortedByDescending { it.rating }
            categorizedCoffees = coffees.filter { it.categoryName == selectedCategory!!.name }
        }
    }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != null) {
            categorizedCoffees = coffees.filter { it.categoryName == selectedCategory!!.name }
        }
    }

    if (HttpClient.user == null) return
    Column(modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                TextP("Good Morning")
                TextP(HttpClient.user!!.fullname, weight = FontWeight.Bold)
            }
            Icon(
                painterResource(R.drawable.shopping_bag_regular_24),
                contentDescription = "Cart",
                modifier = Modifier
                    .size(32.dp)
                    .clickable(onClick = {
                        controller.navigate(Route.CART)
                    })
            )
        }
        LazyColumn(Modifier
            .fillMaxWidth()
            .weight(1f)) {
            item {
                Spacer(Modifier.height(16.dp))
                SearchField(search, { search = it }, modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                TextP("Categories", modifier = Modifier.padding(start = 24.dp))
                LazyRow(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { item ->
                        if (item.id == selectedCategory!!.id) {
                            Button(
                                onClick = { selectedCategory = item },
                                contentPadding = PaddingValues(12.dp)
                            ) {
                                TextP(item.name, color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = { selectedCategory = item },
                                contentPadding = PaddingValues(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                            ) {
                                TextP(item.name)
                            }
                        }
                    }
                }
                LazyRow(Modifier
                    .padding(vertical = 32.dp)
                    .fillMaxWidth()) {
                    items(categorizedCoffees) { item ->
                        Column(Modifier
                            .padding(horizontal = 24.dp)
                            .drawBehind({ drawRoundRect(Color(0xff156545), topLeft = Offset(0f, size.height * 0.35f), cornerRadius = CornerRadius(16.dp.toPx())) })
                            .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val width = 192.dp
                            NetImage(
                                HttpClient.ADDRESS + "images/${item.imgPath}",
                                modifier = Modifier.width(width)
                            )
                            Spacer(Modifier.height(6.dp))
                            Column(Modifier.width(width), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextP(
                                    item.name,
                                    color = Color.White,
                                    weight = FontWeight.Bold,
                                    softWrap = false,
                                    size = MaterialTheme.typography.headlineSmall.fontSize
                                )
                                Row(
                                    Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color(0xff37765D))
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painterResource(R.drawable.star_solid_24),
                                        tint = Color.White,
                                        contentDescription = "Star",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    TextP("${item.rating}", color = Color.White)
                                }
                                TextP(
                                    "$${item.price}",
                                    color = Color.White,
                                    weight = FontWeight.SemiBold,
                                    size = MaterialTheme.typography.headlineSmall.fontSize
                                )
                            }
                        }
                    }
                }
                TextP("Top Picks", weight = FontWeight.Medium, modifier = Modifier.padding(start = 24.dp))
            }
            items(topCoffees) { item ->
                Row(Modifier.padding(vertical = 24.dp, horizontal = 12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        NetImage(HttpClient.ADDRESS + "images/${item.imgPath}", modifier = Modifier.width(108.dp))
                        Row(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painterResource(R.drawable.star_solid_24),
                                tint = Color.White,
                                contentDescription = "Star",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            TextP("${item.rating}", color = Color.White)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.fillMaxWidth()) {
                        TextP(item.name, weight = FontWeight.Medium, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(4.dp))
                        TextP(item.categoryName, color = Color.Gray, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(24.dp))
                        TextP(
                            "$${item.price}",
                            weight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchField(
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    placeholder: String = "Find your perfect coffee..."
) {
    BasicTextField(
        value,
        onValueChanged,
        modifier,
        decorationBox = { tField ->
            Row(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(50))
                    .border(2.dp, Color.Gray, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) TextP(placeholder, color = Color.Gray)
                    tField()
                }
                Icon(
                    painterResource(R.drawable.search_alt_2_regular_24),
                    tint = Color.Gray,
                    contentDescription = "Search",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    )
}
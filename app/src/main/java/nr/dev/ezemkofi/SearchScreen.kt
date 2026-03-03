package nr.dev.ezemkofi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SearchScreen(modifier: Modifier, controller: NavHostController, searchStr: String) {
    var search by remember { mutableStateOf(searchStr) }
    var results by remember { mutableStateOf(listOf<Coffee>()) }

    LaunchedEffect(search) {
        delay(750)
        results = HttpClient.getCoffees(search)
    }

    Column(modifier) {
        Column(Modifier
            .fillMaxWidth()
            .padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(R.drawable.chevron_left_regular_24),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .border(2.dp, Color.LightGray, CircleShape)
                        .padding(6.dp)
                        .size(18.dp)
                        .clickable(onClick = { controller.popBackStack() }),
                    contentDescription = "Back"
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    decorationBox = { tField ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(50))
                                .border(2.dp, Color.Gray, RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                                if (search.isEmpty()) TextP("Find your perfect coffee...", color = Color.Gray)
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
            TextP("Search Result", weight = FontWeight.SemiBold)
        }
        LazyColumn(Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(results) { item ->
                Row(
                    Modifier
                        .padding(vertical = 24.dp, horizontal = 12.dp)
                        .fillMaxWidth()
                        .clickable(onClick = { controller.navigate(Route.COFFEE_DETAIL + "/${item.id}") }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        NetImage(
                            HttpClient.ADDRESS + "images/${item.imgPath}",
                            modifier = Modifier.width(108.dp)
                        )
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
                        TextP(
                            item.name,
                            weight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(4.dp))
                        TextP(
                            item.categoryName,
                            color = Color.Gray,
                            modifier = Modifier.fillMaxWidth()
                        )
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
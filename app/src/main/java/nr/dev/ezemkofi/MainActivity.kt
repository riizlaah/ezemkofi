package nr.dev.ezemkofi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import nr.dev.ezemkofi.ui.theme.EzemkofiTheme
import nr.dev.ezemkofi.ui.theme.poppins

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EzemkofiTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val mod = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary).padding(innerPadding).background(Color.White)
                    val controller = rememberNavController()
                    NavHost(
                        navController = controller,
                        startDestination = Route.LOGIN
                    ) {
                        composable(route = Route.LOGIN) {
                            LoginScreen(mod, controller)
                        }
                        composable(route = Route.REGISTER) {
                            RegisterScreen(mod, controller)
                        }
                        composable(route = Route.HOME) {
                            HomeScreen(mod, controller)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TextP(text: String, modifier: Modifier = Modifier, weight: FontWeight = FontWeight.Normal, alignment: TextAlign = TextAlign.Left, size: TextUnit = MaterialTheme.typography.bodyMedium.fontSize, color: Color = Color.Black, softWrap: Boolean = true) {
    Text(text, modifier = modifier, fontFamily = poppins, fontWeight = weight, textAlign = alignment, fontSize = size, color = color, softWrap = softWrap, overflow = TextOverflow.Ellipsis)
}


fun corner(size: Dp): RoundedCornerShape {
    return RoundedCornerShape(size)
}
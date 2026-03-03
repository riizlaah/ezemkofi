package nr.dev.ezemkofi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(modifier: Modifier, controller: NavHostController) {
    var username by remember { mutableStateOf("") }
    val passState = remember { TextFieldState() }
    var loading by remember { mutableStateOf(false) }
    var errMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LazyColumn(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Image(painterResource(R.drawable.logo_green_with_icon), modifier = Modifier.padding(24.dp), contentDescription = "Logo")
            Spacer(Modifier.height(100.dp))
        }
        item {
            Column(Modifier.padding(24.dp)) {
                TextP("Login", weight = FontWeight.ExtraBold, size = MaterialTheme.typography.headlineMedium.fontSize)
                TextP("Login with your account to continue")
                Spacer(Modifier.height(16.dp))
                BorderedTextField(username, {username = it}, "Username")
                Spacer(Modifier.height(12.dp))
                PasswordField(passState, "Password")
                Spacer(Modifier.height(12.dp))
                if(errMsg.isNotEmpty()) {
                    TextP(errMsg, color = Color.Red, modifier = Modifier.fillMaxWidth(), alignment = TextAlign.Center)
                }
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick = {
                        if(username.isBlank()) {
                            errMsg = "Username can't be empty!"
                            return@Button
                        }
                        if(passState.text.isBlank()) {
                            errMsg = "Password can't be empty!"
                            return@Button
                        }
                        errMsg = ""
                        scope.launch {
                            loading = true
                            if(HttpClient.login(username, passState.text.toString())) {
                                controller.navigate(Route.HOME) {
                                    popUpTo(controller.graph.id) {
                                        inclusive = true
                                    }
                                }
                            } else {
                                errMsg = "User not found!"
                                loading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    if(loading) {
                        CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 2.dp)
                        return@Button
                    }
                    TextP("LOGIN", color = Color.White, weight = FontWeight.Medium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    TextP("Don't have an account?", color = Color.Gray)
                    TextButton(
                        onClick = {controller.navigate(Route.REGISTER)}
                    ) {
                        TextP("Create an account!", weight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(modifier: Modifier, controller: NavHostController) {
    var username by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val passState = remember { TextFieldState() }
    val passState2 = remember { TextFieldState() }
    var loading by remember { mutableStateOf(false) }
    var errMsg by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LazyColumn(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Image(painterResource(R.drawable.logo_green_with_icon), modifier = Modifier.padding(24.dp), contentDescription = "Logo")
            Spacer(Modifier.height(100.dp))
        }
        item {
            Column(Modifier.padding(24.dp)) {
                TextP("Create Account", weight = FontWeight.ExtraBold, size = MaterialTheme.typography.headlineMedium.fontSize)
                TextP("Register yourself to become our member and enjoy all benefits", modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(16.dp))
                BorderedTextField(username, {username = it}, "Username")
                BorderedTextField(fullname, {fullname = it}, "Full Name")
                BorderedTextField(email, {email = it}, "Email")
                Spacer(Modifier.height(12.dp))
                PasswordField(passState, "Password")
                PasswordField(passState2, "Confirm Password")
                Spacer(Modifier.height(12.dp))
                if(errMsg.isNotEmpty()) {
                    TextP(errMsg, color = Color.Red, modifier = Modifier.fillMaxWidth(), alignment = TextAlign.Center)
                }
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick = {
                        if(username.isBlank()) {
                            errMsg = "Username can't be empty!"
                            return@Button
                        }
                        if(fullname.isBlank()) {
                            errMsg = "Full Name can't be empty!"
                            return@Button
                        }
                        if(!email.contains('@')) {
                            errMsg = "Email not valid!"
                            return@Button
                        }
                        if(passState.text.length < 4) {
                            errMsg = "Password must be 4 characters or more!"
                            return@Button
                        }
                        if(passState.text != passState2.text) {
                            errMsg = "Password Confirmation is different!"
                            return@Button
                        }
                        errMsg = ""
                        scope.launch {
                            loading = true
                            if(HttpClient.register(username, passState.text.toString(), fullname, email)) {
                                controller.navigate(Route.LOGIN)
                            } else {
                                loading = false
                                errMsg = "Username already exists!"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    if(loading) {
                        CircularProgressIndicator(Modifier.size(28.dp), strokeWidth = 2.dp)
                        return@Button
                    }
                    TextP("SIGN UP", color = Color.White, weight = FontWeight.Medium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    TextP("Already have an account?", color = Color.Gray)
                    TextButton(
                        onClick = {controller.navigate(Route.LOGIN)}
                    ) {
                        TextP("Login here", weight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BorderedTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        decorationBox = {tField ->
            Column(Modifier.fillMaxWidth().borderBottom().padding(4.dp)) {
                TextP(placeholder, weight = FontWeight.Thin, color = Color.Gray)
                Box(Modifier.fillMaxWidth().padding(6.dp)) {
                    tField()
                }
            }
        }
    )
}

@Composable
fun PasswordField(state: TextFieldState, placeholder: String) {
    BasicSecureTextField(
        state = state,
        modifier = Modifier.fillMaxWidth(),
        decorator = {tField ->
            Column(Modifier.fillMaxWidth().borderBottom().padding(4.dp)) {
                TextP(placeholder, weight = FontWeight.Thin, color = Color.Gray)
                Box(Modifier.fillMaxWidth().padding(6.dp)) {
                    tField()
                }
            }
        }
    )
}

fun Modifier.borderBottom(width: Dp = 2.dp, color: Color = Color.Gray): Modifier =
    this.drawBehind {
        val y = size.height - width.toPx() / 2
        drawLine(
            color,
            Offset(0f, y),
            Offset(size.width, y),
            width.toPx()
        )
    }
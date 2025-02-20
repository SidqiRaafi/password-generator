package com.hnny.passwordgenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            var showSplash by remember { mutableStateOf(true) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.background)),
                contentAlignment = Alignment.Center
            ) {
                if (showSplash) {
                    SplashScreen(onSplashComplete = { showSplash = false })
                } else {
                    PasswordGeneratorApp()
                }
            }
        }
    }
}

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrainsmono_bold, FontWeight.Bold)
)

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    var progress by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        (0..100).forEach {
            delay(20)
            progress = it / 100f
        }
        delay(500)
        onSplashComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Password Generator",
            color = colorResource(id = R.color.accent),
            fontSize = 32.sp,
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(220.dp)
                .padding(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        LinearProgressIndicator(
            modifier = Modifier
                .width(200.dp)
                .height(4.dp),
            progress = { progress },
            color = colorResource(id = R.color.accent),
            trackColor = colorResource(id = R.color.primary)
        )
    }
}

@Composable
fun PasswordGeneratorApp() {
    var password by remember { mutableStateOf("Set Length : 1-16") }
    var isLoading by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    var showCursor by remember { mutableStateOf(false) }
    var passwordLength by remember { mutableStateOf("8") }
    var includeUppercase by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    val context = LocalContext.current

    LaunchedEffect(isFocused) {
        if (isFocused) {
            showCursor = true  // Show cursor when focused
            delay(3000)
            showCursor = false  // Hide after delay
        }
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(900)  // Show loading for 0.9 seconds
            isLoading = false
            password = generateRandomPassword(
                length = passwordLength.toIntOrNull() ?: 12,
                includeUppercase = includeUppercase,
                includeLowercase = true,
                includeNumbers = includeNumbers,
                includeSymbols = includeSymbols
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Password Generator",
            color = colorResource(id = R.color.text_primary),
            fontSize = 24.sp,
            fontFamily = JetBrainsMono
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.primary),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
                .height(36.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = colorResource(id = R.color.accent),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = password,
                    color = colorResource(id = R.color.text_secondary),
                    fontSize = 24.sp,
                    fontFamily = JetBrainsMono
                )
            }
        }

        Button(
            onClick = {
                val clipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("Generated Password", password)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(context, "Password copied!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.accent)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Copy →",
                color = colorResource(id = R.color.background),
                fontSize = 20.sp,
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.primary),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = colorResource(id = R.color.background),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Length    :",
                            color = colorResource(id = R.color.text_primary),
                            fontSize = 20.sp,
                            fontFamily = JetBrainsMono
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                color = colorResource(id = R.color.background),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        BasicTextField(
                            value = passwordLength,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() || (newValue.toIntOrNull() != null && newValue.toInt() in 1..16)) {
                                    passwordLength = newValue
                                    showCursor = true  // Show cursor when typing
                                }
                            },
                            textStyle = TextStyle(
                                color = colorResource(id = R.color.text_primary),
                                fontSize = 20.sp,
                                fontFamily = JetBrainsMono,
                                textAlign = TextAlign.Center
                            ),
                            cursorBrush = if (showCursor) SolidColor(colorResource(id = R.color.text_primary))
                            else SolidColor(colorResource(id = R.color.background)),
                            singleLine = true,
                            modifier = Modifier.onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                if (focusState.isFocused) {
                                    showCursor = true  // Show cursor when clicked
                                }
                            }
                        )
                    }
                }

                ToggleRow(
                    text = "Uppercase :",
                    isChecked = includeUppercase,
                    onCheckedChange = { includeUppercase = it }
                )

                ToggleRow(
                    text = "Symbols   :",
                    isChecked = includeSymbols,
                    onCheckedChange = { includeSymbols = it }
                )

                ToggleRow(
                    text = "Numbers   :",
                    isChecked = includeNumbers,
                    onCheckedChange = { includeNumbers = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (passwordLength.toIntOrNull() != null && passwordLength.toIntOrNull()!! in 1..16) {
                            isLoading = true
                        } else {
                            password = "Enter length (1-16)"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.accent)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "GENERATE →",
                        color = colorResource(id = R.color.background),
                        fontSize = 20.sp,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun ToggleRow(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = colorResource(id = R.color.background),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = text,
                color = colorResource(id = R.color.text_primary),
                fontSize = 20.sp,
                fontFamily = JetBrainsMono
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = colorResource(id = R.color.background),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colorResource(id = R.color.accent),
                    checkedTrackColor = colorResource(id = R.color.primary),
                    uncheckedThumbColor = colorResource(id = R.color.text_secondary),
                    uncheckedTrackColor = colorResource(id = R.color.primary)
                )
            )
        }
    }
}

fun generateRandomPassword(
    length: Int = 12,
    includeUppercase: Boolean = true,
    includeLowercase: Boolean = true,
    includeNumbers: Boolean = true,
    includeSymbols: Boolean = true
): String {
    val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val lowercase = "abcdefghijklmnopqrstuvwxyz"
    val numbers = "0123456789"
    val symbols = "!@#$%^&*()"

    var charset = ""
    if (includeUppercase) charset += uppercase
    if (includeLowercase) charset += lowercase
    if (includeNumbers) charset += numbers
    if (includeSymbols) charset += symbols

    if (charset.isEmpty()) charset = uppercase + lowercase + numbers + symbols

    return (1..length)
        .map { charset.random() }
        .joinToString("")
}
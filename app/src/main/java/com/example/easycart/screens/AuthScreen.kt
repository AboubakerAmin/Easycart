package com.example.easycart.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.easycart.R
import com.example.easycart.ui.theme.EasyRed
import com.example.easycart.viewmodel.AuthViewModel

private val EasyCartOrange = Color(0xFFFF6B00)

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onGuest: () -> Unit,
    onGoogleClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {

    var loginMode by remember { mutableStateOf(true) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(R.drawable.easylogo),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

//        Spacer(modifier = Modifier.height(16.dp))
//
//        Text(
//            text = "EasyCart",
//            style = MaterialTheme.typography.headlineLarge,
//            fontWeight = FontWeight.Bold
//        )

        Text(
            text = "Buy • Sell • Discover",
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth() ,
            shape = RoundedCornerShape(50.dp)
        ) {

            Column(
                modifier = Modifier.background(EasyRed) .padding(20.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {

                    Text(
                        text = "Login",
                        color = if (loginMode) EasyCartOrange else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            loginMode = true
                        }
                    )

                    Text(
                        text = "Register",
                        color = if (!loginMode) EasyCartOrange else Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            loginMode = false
                        }
                    )

                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Email")
                    },
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Email, null)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    label = {
                        Text("Password")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, null)
                    },
                    trailingIcon = {

                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            }
                        ) {

                            Icon(
                                if (passwordVisible)
                                    Icons.Default.VisibilityOff
                                else
                                    Icons.Default.Visibility,
                                null
                            )

                        }

                    },
                    visualTransformation =
                        if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),

                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = {
                        viewModel.resetPassword(email)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?")
                }

                viewModel.errorMessage?.let {

                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                }

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {

                        if (loginMode) {

                            viewModel.login(
                                email = email,
                                password = password,
                                onSuccess = onLoginSuccess
                            )

                        } else {

                            viewModel.register(
                                email = email,
                                password = password,
                                onSuccess = onLoginSuccess
                            )

                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EasyCartOrange
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text(
                        if (loginMode)
                            "Login"
                        else
                            "Create Account"
                    )

                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Divider(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = " OR Continue With",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Divider(
                        modifier = Modifier.weight(1f)
                    )

                }

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedButton(
                    onClick = onGoogleClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text("Continue with Google")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onGuest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),

                    shape = RoundedCornerShape(14.dp)
                ) {

                    Text("Continue as Guest")

                }

                if (viewModel.loading) {

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        CircularProgressIndicator()

                    }

                }

            }

        }

        Spacer(modifier = Modifier.height(30.dp))

    }

}
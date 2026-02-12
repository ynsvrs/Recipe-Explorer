package com.example.recipeapp.ui.auth
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            onNavigateToHome()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(authState.error) {
        authState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isLoginMode) "Welcome Back!" else "Create Account",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = if (isLoginMode) "Sign in to continue" else "Sign up to get started",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !authState.isLoading,
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it) } }
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password")
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.Visibility
                                    else
                                        Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible)
                                        "Hide password"
                                    else
                                        "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (isLoginMode) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (isLoginMode) {
                                    focusManager.clearFocus()
                                    handleSubmit(
                                        isLoginMode = true,
                                        email = email,
                                        password = password,
                                        confirmPassword = "",
                                        onEmailError = { emailError = it },
                                        onPasswordError = { passwordError = it },
                                        onConfirmPasswordError = {},
                                        onSubmit = { e, p -> viewModel.signIn(e, p) }
                                    )
                                } else {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            },
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !authState.isLoading,
                        isError = passwordError != null,
                        supportingText = passwordError?.let { { Text(it) } }
                    )

                    // Confirm Password Field (Sign Up only)
                    if (!isLoginMode) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPasswordError = null
                            },
                            label = { Text("Confirm Password") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = "Confirm Password")
                            },
                            trailingIcon = {
                                IconButton(onClick = {
                                    confirmPasswordVisible = !confirmPasswordVisible
                                }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible)
                                            Icons.Default.Visibility
                                        else
                                            Icons.Default.VisibilityOff,
                                        contentDescription = if (confirmPasswordVisible)
                                            "Hide password"
                                        else
                                            "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    handleSubmit(
                                        isLoginMode = false,
                                        email = email,
                                        password = password,
                                        confirmPassword = confirmPassword,
                                        onEmailError = { emailError = it },
                                        onPasswordError = { passwordError = it },
                                        onConfirmPasswordError = { confirmPasswordError = it },
                                        onSubmit = { e, p -> viewModel.signUp(e, p) }
                                    )
                                }
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !authState.isLoading,
                            isError = confirmPasswordError != null,
                            supportingText = confirmPasswordError?.let { { Text(it) } }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            handleSubmit(
                                isLoginMode = isLoginMode,
                                email = email,
                                password = password,
                                confirmPassword = confirmPassword,
                                onEmailError = { emailError = it },
                                onPasswordError = { passwordError = it },
                                onConfirmPasswordError = { confirmPasswordError = it },
                                onSubmit = { e, p ->
                                    if (isLoginMode) viewModel.signIn(e, p)
                                    else viewModel.signUp(e, p)
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !authState.isLoading
                    ) {
                        if (authState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = if (isLoginMode) "Sign In" else "Sign Up",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    // Toggle Mode
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isLoginMode)
                                "Don't have an account?"
                            else
                                "Already have an account?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(
                            onClick = {
                                isLoginMode = !isLoginMode
                                confirmPassword = ""
                                emailError = null
                                passwordError = null
                                confirmPasswordError = null
                            },
                            enabled = !authState.isLoading
                        ) {
                            Text(
                                text = if (isLoginMode) "Sign Up" else "Sign In",
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun handleSubmit(
    isLoginMode: Boolean,
    email: String,
    password: String,
    confirmPassword: String,
    onEmailError: (String?) -> Unit,
    onPasswordError: (String?) -> Unit,
    onConfirmPasswordError: (String?) -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var hasError = false

    // Validate email
    if (email.isBlank()) {
        onEmailError("Email is required")
        hasError = true
    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onEmailError("Invalid email format")
        hasError = true
    }

    // Validate password
    if (password.isBlank()) {
        onPasswordError("Password is required")
        hasError = true
    } else if (password.length < 6) {
        onPasswordError("Password must be at least 6 characters")
        hasError = true
    }

    // Validate confirm password (sign up only)
    if (!isLoginMode) {
        if (confirmPassword.isBlank()) {
            onConfirmPasswordError("Please confirm your password")
            hasError = true
        } else if (password != confirmPassword) {
            onConfirmPasswordError("Passwords do not match")
            hasError = true
        }
    }

    if (!hasError) {
        onSubmit(email, password)
    }
}
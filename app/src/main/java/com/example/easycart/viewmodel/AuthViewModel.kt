package com.example.easycart.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easycart.auth.GoogleAuthManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var loading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun clearError() {
        errorMessage = null
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }

        loading = true
        errorMessage = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loading = false
                errorMessage = null
                onSuccess()
            }
            .addOnFailureListener {
                loading = false
                errorMessage = it.localizedMessage
            }
    }

    fun register(email: String, password: String, onSuccess: () -> Unit) {

        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }

        if (password.length < 6) {
            errorMessage = "Password must be at least 6 characters"
            return
        }

        loading = true
        errorMessage = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loading = false
                errorMessage = null
                onSuccess()
            }
            .addOnFailureListener {
                loading = false
                errorMessage = it.localizedMessage
            }
    }

    fun loginWithGoogle(
        googleAuthManager: GoogleAuthManager,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            loading = true
            errorMessage = null

            try {

                val result = googleAuthManager.signIn()

                loading = false

                result
                    .onSuccess {
                        errorMessage = null
                        onSuccess()
                    }
                    .onFailure {
                        errorMessage = it.message ?: "Google sign-in failed"
                    }

            } catch (e: Exception) {

                loading = false
                errorMessage = e.message ?: "Unexpected error"
            }
        }
    }

    fun resetPassword(email: String) {

        if (email.isBlank()) {
            errorMessage = "Enter email"
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                errorMessage = "Reset email sent"
            }
            .addOnFailureListener {
                errorMessage = it.localizedMessage
            }
    }

    fun logout() {
        auth.signOut()
    }
}
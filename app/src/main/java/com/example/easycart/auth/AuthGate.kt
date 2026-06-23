package com.example.easycart.auth

import androidx.compose.runtime.*

object AuthGate {

    var showLoginDialog by mutableStateOf(false)
        private set

    var pendingAction: (() -> Unit)? = null

    fun requestLogin(action: () -> Unit) {
        pendingAction = action
        showLoginDialog = true
    }

    fun confirmLogin() {
        pendingAction?.invoke()
        pendingAction = null
        showLoginDialog = false
    }

    fun dismiss() {
        pendingAction = null
        showLoginDialog = false
    }
}
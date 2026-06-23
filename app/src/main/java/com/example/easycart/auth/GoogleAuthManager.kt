package com.example.easycart.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(
    private val context: Context
) {

    private val auth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)

    suspend fun signIn(): Result<Unit> {

        return try {

            val serverClientId = context.getString(
                com.example.easycart.R.string.default_web_client_id
            )

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result: GetCredentialResponse =
                credentialManager.getCredential(
                    request = request,
                    context = context
                )

            val credential = result.credential

            val googleIdToken = when (credential) {

                is CustomCredential -> {

                    if (credential.type ==
                        GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {

                        val googleCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        googleCredential.idToken

                    } else {
                        null
                    }
                }

                else -> null
            }

            if (googleIdToken.isNullOrEmpty()) {
                Log.e("GoogleAuth", "ID token is null")
                return Result.failure(Exception("No Google ID token found"))
            }

            val firebaseCredential =
                GoogleAuthProvider.getCredential(googleIdToken, null)

            auth.signInWithCredential(firebaseCredential).await()

            Log.d("GoogleAuth", "Sign-in successful")

            Result.success(Unit)

        } catch (e: GetCredentialException) {

            Log.e("GoogleAuth", "CredentialManager failed", e)
            Result.failure(Exception("Google Sign-In failed: ${e.message}"))

        } catch (e: Exception) {

            Log.e("GoogleAuth", "Unexpected error", e)
            Result.failure(e)
        }
    }
}
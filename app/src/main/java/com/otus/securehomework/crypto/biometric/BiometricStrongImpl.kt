package com.otus.securehomework.crypto.biometric

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.auth.AuthPromptErrorException
import androidx.biometric.auth.AuthPromptFailureException
import androidx.biometric.auth.AuthPromptHost
import androidx.biometric.auth.Class3BiometricAuthPrompt
import androidx.biometric.auth.authenticate
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.M)
class BiometricStrongImpl() : Biometric.BiometricStrong() {

    override fun getManager(activity: FragmentActivity, onSuccess: () -> Unit) {
        val biometricCipher = BiometricCipher(activity)
        val encryptor = biometricCipher.getEncryptor()

        val authPrompt = Class3BiometricAuthPrompt.Builder("Strong biometry", "dismiss").apply {
            setSubtitle("Input your biometry")
            setDescription("We need your finger")
            setConfirmationRequired(true)
        }.build()

        activity.lifecycleScope.launch {
            try {
                val authResult = authPrompt.authenticate(AuthPromptHost(activity), encryptor)
                val encryptedEntity = authResult.cryptoObject?.cipher?.let { cipher ->
                    biometricCipher.encrypt("Secret data", cipher)
                }
                onSuccess.invoke()
                Log.d(this::class.simpleName, String(encryptedEntity!!.ciphertext))
                Toast.makeText(activity, "Hello from biometry", Toast.LENGTH_LONG)
                    .show()
            } catch (e: AuthPromptErrorException) {
                Log.e("AuthPromptError", e.message ?: "no message")
            } catch (e: AuthPromptFailureException) {
                Log.e("AuthPromptFailure", e.message ?: "no message")
            }
        }
    }
}
package com.otus.securehomework.crypto.biometric

import android.widget.Toast
import androidx.biometric.auth.AuthPromptErrorException
import androidx.biometric.auth.AuthPromptFailureException
import androidx.biometric.auth.AuthPromptHost
import androidx.biometric.auth.Class2BiometricAuthPrompt
import androidx.biometric.auth.authenticate
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class BiometricWeakImpl() : Biometric.BiometricWeak() {

    override fun getManager(activity: FragmentActivity, onSuccess: () -> Unit) {
        val authPrompt = Class2BiometricAuthPrompt.Builder("Weak biometry", "dismiss").apply {
            setSubtitle("Input your biometry")
            setDescription("We need your finger")
            setConfirmationRequired(true)
        }.build()

        activity.lifecycleScope.launch {
            try {
                authPrompt.authenticate(AuthPromptHost(activity))
                onSuccess.invoke()
                Toast.makeText(activity, "Hello from biometry", Toast.LENGTH_LONG)
                    .show()
            } catch (e: AuthPromptErrorException) {
                Toast.makeText(activity, "$e", Toast.LENGTH_LONG).show()
            } catch (e: AuthPromptFailureException) {
                Toast.makeText(activity, "$e", Toast.LENGTH_LONG).show()
            }
        }
    }
}
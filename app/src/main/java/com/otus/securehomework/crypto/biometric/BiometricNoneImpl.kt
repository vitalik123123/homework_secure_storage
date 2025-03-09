package com.otus.securehomework.crypto.biometric

import androidx.fragment.app.FragmentActivity

class BiometricNoneImpl() : Biometric.BiometricNone() {
    override fun getManager(
        activity: FragmentActivity,
        onSuccess: () -> Unit
    ) {
    }
}
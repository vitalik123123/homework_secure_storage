package com.otus.securehomework.crypto.biometric

import androidx.fragment.app.FragmentActivity

sealed class Biometric {

    open class BiometricWeak(): Biometric()
    open class BiometricStrong(): Biometric()
    open class BiometricNone(): Biometric()

    open fun getManager(activity: FragmentActivity, onSuccess: () -> Unit) {}
}

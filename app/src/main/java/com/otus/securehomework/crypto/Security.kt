package com.otus.securehomework.crypto

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
private const val AUTHENTICATION_TAG_LENGTH = 128
private const val GCM_IV_LENGTH = 12

class Security @Inject constructor(
    private val keys: Keys
) {

    fun encryptAes(plainText: String): String {
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        val key = keys.getSecretKey()
        cipher.init(
            Cipher.ENCRYPT_MODE,
            key,
            GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, generateIv())
        )
        val encodedBytes = cipher.doFinal(plainText.toByteArray())
        return Base64.encodeToString(encodedBytes, Base64.NO_WRAP)
    }

    fun decryptAes(encrypted: String): String {
        val cipher = Cipher.getInstance(AES_TRANSFORMATION)
        val key = keys.getSecretKey()
        cipher.init(
            Cipher.DECRYPT_MODE,
            key,
            GCMParameterSpec(AUTHENTICATION_TAG_LENGTH, generateIv())
        )
        val decodedBytes = Base64.decode(encrypted, Base64.NO_WRAP)
        val decoded = cipher.doFinal(decodedBytes)
        return String(decoded, Charsets.UTF_8)
    }

    private fun generateIv(): ByteArray {
        val result = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(result)
        return result
    }
}
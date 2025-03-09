package com.otus.securehomework.crypto.biometric

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProperties.BLOCK_MODE_GCM
import android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE
import android.security.keystore.KeyProperties.KEY_ALGORITHM_AES
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
private const val KEY_SIZE = 256

@RequiresApi(Build.VERSION_CODES.M)
private const val TRANSFORMATION = "$KEY_ALGORITHM_AES/" +
        "$BLOCK_MODE_GCM/" +
        ENCRYPTION_PADDING_NONE

@RequiresApi(Build.VERSION_CODES.M)
class BiometricCipher(
    private val applicationContext: Context
) {
    private val keyAlias by lazy { "${applicationContext.packageName}.biometricKey" }

    fun getEncryptor(): BiometricPrompt.CryptoObject {
        val encryptor = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        }

        return BiometricPrompt.CryptoObject(encryptor)
    }

    fun encrypt(plaintext: String, encryptor: Cipher): EncryptedEntity {
        require(plaintext.isNotEmpty()) { "Plaintext cannot be empty" }
        val ciphertext = encryptor.doFinal(plaintext.toByteArray())
        return EncryptedEntity(
            ciphertext,
            encryptor.iv
        )
    }

    private fun getOrCreateKey(): SecretKey {
        val keystore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
            load(null)
        }

        keystore.getKey(keyAlias, null)?.let { key ->
            return key as SecretKey
        }

        val keySpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(BLOCK_MODE_GCM)
            .setEncryptionPaddings(ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE)
            .setUserAuthenticationRequired(true)
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    setUnlockedDeviceRequired(true)

                    val hasStringBox = applicationContext
                        .packageManager
                        .hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)

                    setIsStrongBoxBacked(hasStringBox)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    setUserAuthenticationParameters(0, KeyProperties.AUTH_BIOMETRIC_STRONG)
                }
            }.build()

        val keyGenerator = KeyGenerator.getInstance(
            KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        ).apply {
            init(keySpec)
        }

        return keyGenerator.generateKey()
    }
}
package com.otus.securehomework.crypto

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val KEY_PROVIDER = "AndroidKeyStore"
private const val KEY_LENGTH = 256

private const val AES_ALGORITHM = "AES"
private const val AES_KEY_ALIAS = "AES_OTUS_DEMO"

@RequiresApi(Build.VERSION_CODES.M)
class KeysMore23Version() : Keys {

    private val keyStore by lazy {
        KeyStore.getInstance(KEY_PROVIDER).apply {
            load(null)
        }
    }

    override fun getSecretKey(): SecretKey =
        keyStore.getKey(AES_KEY_ALIAS, null) as? SecretKey ?: generateAesSecretKey()


    private fun generateAesSecretKey(): SecretKey =
        getKeyGenerator().generateKey()


    private fun getKeyGenerator() = KeyGenerator.getInstance(AES_ALGORITHM, KEY_PROVIDER).apply {
        init(getKeyGenSpec())
    }

    private fun getKeyGenSpec(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            AES_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(false)
            .setKeySize(KEY_LENGTH)
            .build()
    }
}
package com.otus.securehomework.crypto.biometric

data class EncryptedEntity(
    val ciphertext: ByteArray,
    val iv: ByteArray
)
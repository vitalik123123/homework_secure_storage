package com.otus.securehomework.crypto

import javax.crypto.SecretKey

interface Keys {
    fun getSecretKey(): SecretKey
}
@file:Suppress("unused")

package com.development.clean.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.development.clean.BuildConfig
import com.development.clean.data.local.sharedprefs.AES_IV
import com.development.clean.data.local.sharedprefs.AppSharedPrefs
import com.development.clean.util.debug.Timber
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@Suppress("MemberVisibilityCanBePrivate")
object AESEncryption {
    private const val PROVIDER = "AndroidKeyStore"
    private const val KEY_STORE_ALIAS = "AES_${BuildConfig.APPLICATION_ID}"
    private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
    private const val KEY_SIZE = 256

    private val keyStore: KeyStore = KeyStore.getInstance(PROVIDER).apply {
        load(null)
    }

    private val iv: ByteArray = AppSharedPrefs.get<String?>(AES_IV, null)
        .let { base64 ->
            if (base64.isNullOrEmpty()) {
                ByteArray(16).also {
                    SecureRandom().nextBytes(it)
                    AppSharedPrefs.put(AES_IV, Base64.encodeToString(it, Base64.DEFAULT))
                }
            } else {
                Base64.decode(base64, Base64.DEFAULT)
            }
        }

    init {

        if (!keyStore.containsAlias(KEY_STORE_ALIAS)) {
            val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_STORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).run {
                setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                setKeySize(KEY_SIZE)
                setRandomizedEncryptionRequired(false)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                build()
            }

            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER)
                .run {
                    init(parameterSpec)
                    generateKey()
                }
        }
    }

    fun encrypt(data: String): String? = try {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val secretKey = keyStore.getKey(KEY_STORE_ALIAS, null) as SecretKey

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val encrypt: ByteArray = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

        Base64.encodeToString(encrypt, Base64.DEFAULT)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }

    fun decrypt(encryptData: String): String? = try {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val secretKey = keyStore.getKey(KEY_STORE_ALIAS, null) as SecretKey

        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val decrypt: ByteArray = cipher.doFinal(Base64.decode(encryptData, Base64.DEFAULT))

        decrypt.toString(Charsets.UTF_8)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}


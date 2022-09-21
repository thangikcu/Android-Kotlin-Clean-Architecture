@file:Suppress("unused")

package com.development.clean.util

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.development.clean.BuildConfig
import com.development.clean.util.debug.Timber
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import javax.crypto.Cipher

@Suppress("MemberVisibilityCanBePrivate")
object RSAEncryption {
    private const val PROVIDER = "AndroidKeyStore"
    private const val KEY_STORE_ALIAS = "RSA_${BuildConfig.APPLICATION_ID}"
    private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    private const val KEY_SIZE = 4096

    private val keyStore: KeyStore = KeyStore.getInstance(PROVIDER).apply {
        load(null)
    }

    init {
        if (!keyStore.containsAlias(KEY_STORE_ALIAS)) {
            val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_STORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            ).run {
                setKeySize(KEY_SIZE)
                setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                setDigests(KeyProperties.DIGEST_SHA1)
                build()
            }

            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, PROVIDER)
                .run {
                    initialize(parameterSpec)
                    generateKeyPair()
                }
        }
    }

    //    data must not be longer than 501 bytes
    fun encrypt(data: String): String? = try {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val publicKey = keyStore.getCertificate(KEY_STORE_ALIAS).publicKey

        cipher.init(Cipher.ENCRYPT_MODE, publicKey)

        val encrypt: ByteArray = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

        Base64.encodeToString(encrypt, Base64.DEFAULT)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }

    fun decrypt(encryptData: String): String? = try {
        val privateKey = keyStore.getKey(KEY_STORE_ALIAS, null) as PrivateKey

        val cipher = Cipher.getInstance(TRANSFORMATION)

        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val decrypt: ByteArray = cipher.doFinal(Base64.decode(encryptData, Base64.DEFAULT))

        decrypt.toString(Charsets.UTF_8)
    } catch (e: Exception) {
        Timber.e(e)
        null
    }
}


package com.convoxing.convoxing_customer.data.repository.installreferrer

import com.convoxing.convoxing_customer.BuildConfig
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject


class EncryptionHelper @Inject constructor() {

    companion object {
        private const val ALGORITHM = "AES/GCM/NoPadding"
        private const val GCM_TAG_LENGTH = 128
        private const val DECRYPTION_KEY = BuildConfig.EVENTS_ENCRYPTION_KEY // Store this securely!
    }

    fun decrypt(encryptedData: String, nonce: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val keyBytes = hexStringToByteArray(DECRYPTION_KEY)
        val nonceBytes = hexStringToByteArray(nonce)
        val encryptedBytes = hexStringToByteArray(encryptedData)

        val secretKeySpec = SecretKeySpec(keyBytes, "AES")
        val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, nonceBytes)

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmParameterSpec)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes, 0, decryptedBytes.size)
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] =
                ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }
}
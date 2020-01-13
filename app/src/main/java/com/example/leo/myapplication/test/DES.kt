package com.example.leo.myapplication.test

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec

data class DES(private val strKey: String) {
    private var encryptCipher: Cipher
    private var decryptCipher: Cipher

    init {
        val key = generateKey(strKey)
        encryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            this.init(Cipher.ENCRYPT_MODE, key)
        }
        decryptCipher = Cipher.getInstance(TRANSFORMATION).apply {
            this.init(Cipher.DECRYPT_MODE, key)
        }
    }

    fun encrypt(data: String) = byte2Hex(encryptCipher.doFinal(data.toByteArray()))

    fun decrypt(data: String) = String(decryptCipher.doFinal(hex2Byte(data)))

    private companion object {
        const val ALGORITHM = "DES"
        const val TRANSFORMATION = "DES/ECB/PKCS5Padding"

        fun generateKey(strKey: String): SecretKey {
            val keyFactory = SecretKeyFactory.getInstance(ALGORITHM)
            val keySpec = DESKeySpec(strKey.toByteArray())
            return keyFactory.generateSecret(keySpec)
        }

        fun byte2Hex(bytes: ByteArray): String {
            val sb = StringBuilder(bytes.size * 2)
            for (byte in bytes) {
                sb.append(String.format("%02x", byte))
            }
            return sb.toString()
        }

        fun hex2Byte(str: String): ByteArray {
            val bytes = str.toByteArray()
            val len = bytes.size
            val ret = UByteArray(len / 2)
            for (i in 0 until len step 2) {
                ret[i / 2] = String(bytes, i, 2).toUByte(16)
            }
            return ret.asByteArray()
        }
    }
}

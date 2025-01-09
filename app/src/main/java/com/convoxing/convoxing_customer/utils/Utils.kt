package com.convoxing.convoxing_customer.utils

import android.content.Context
import java.io.File
import java.security.MessageDigest

object Utils {

    fun clearCacheDir(context: Context) {
        val cacheDir = context.cacheDir
        if (cacheDir.isDirectory) {
            val files = cacheDir.listFiles()
            if (files != null) {
                for (file in files) {
                    deleteFileOrDir(file)
                }
            }
        }
    }

    fun hashNonce(nonce: String): String {
        val bytes = nonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    fun deleteFileOrDir(fileOrDir: File): Boolean {
        return if (fileOrDir.isDirectory) {
            fileOrDir.listFiles()?.forEach { deleteFileOrDir(it) }
            fileOrDir.delete()
        } else {
            fileOrDir.delete()
        }
    }
}
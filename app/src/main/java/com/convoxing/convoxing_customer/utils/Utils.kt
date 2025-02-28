package com.convoxing.convoxing_customer.utils

import android.content.Context
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object Utils {

    fun String.formatIsoDate(): String {
        val inputFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val outputFormat = SimpleDateFormat("MMMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return try {
            val date = inputFormat.parse(this)
            date?.let { outputFormat.format(it) } ?: "Invalid date"
        } catch (e: Exception) {
            "Invalid date"
        }
    }

    fun String.formatIsoDateToReadableDate(): String? {
        val inputFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val date = inputFormat.parse(this)
            date?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            null
        }
    }

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
package com.example.advogo.utils.extensions

object StringUtils {
    fun String.removeSpecialCharacters(): String {
        return this.replace("[^a-zA-Z0-9 ]".toRegex(), "")
    }
}
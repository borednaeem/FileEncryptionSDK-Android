package com.example.fileencryptor.core.common.utils

fun <E> Collection<E>.validIndex(index: Int?): Boolean {
    return index != null && index >= 0 && index < size
}

fun <E> List<E>.getIfValidIndex(index: Int?): E? {
    return if (validIndex(index)) this[index!!] else null
}
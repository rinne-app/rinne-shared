package com.rinne.libraries.serialization.json

data class RinneJson(val raw: String)


interface RinneJsonParser {
    fun decode(json: String): RinneJson
    fun <T> encode(json: String): RinneJson
}

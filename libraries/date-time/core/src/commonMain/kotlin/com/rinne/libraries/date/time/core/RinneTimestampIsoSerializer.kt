package com.rinne.libraries.date.time.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** Serializes [RinneTimestamp] as an ISO-8601 UTC string. */
object RinneTimestampIsoSerializer : KSerializer<RinneTimestamp> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("com.rinne.libraries.date.time.core.RinneTimestamp", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: RinneTimestamp) {
        encoder.encodeString(value.toIsoString())
    }

    override fun deserialize(decoder: Decoder): RinneTimestamp {
        val input = decoder.decodeString()
        return RinneTimestamp.parseIsoOrNull(input)
            ?: throw SerializationException("'$input' is not an ISO-8601 timestamp")
    }
}

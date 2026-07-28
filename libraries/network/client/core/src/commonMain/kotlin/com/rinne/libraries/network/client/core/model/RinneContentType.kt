package com.rinne.libraries.network.client.core.model

sealed interface RinneContentType {

    data class Custom(
        val contentType: String,
        val contentSubtype: String,
    ) : RinneContentType

    object Application : RinneContentType {
        object Any : RinneContentType
        object Atom : RinneContentType
        object Cbor : RinneContentType
        object Json : RinneContentType
        object HalJson : RinneContentType
        object JavaScript : RinneContentType
        object OctetStream : RinneContentType
        object Rss : RinneContentType
        object Soap : RinneContentType
        object Xml : RinneContentType
        object Xml_Dtd : RinneContentType
        object Yaml : RinneContentType
        object Zip : RinneContentType
        object GZip : RinneContentType
        object FormUrlEncoded : RinneContentType
        object Pdf : RinneContentType
        object Xlsx : RinneContentType
        object Docx : RinneContentType
        object Pptx : RinneContentType
        object ProtoBuf : RinneContentType
        object Wasm : RinneContentType
        object ProblemJson : RinneContentType
        object ProblemXml : RinneContentType
    }

    object Audio {
        object Any : RinneContentType
        object MP4 : RinneContentType
        object MPEG : RinneContentType
        object OGG : RinneContentType
    }

    object Image {
        object Any : RinneContentType
        object APNG : RinneContentType
        object AVIF : RinneContentType
        object BMP : RinneContentType
        object GIF : RinneContentType
        object HEIC : RinneContentType
        object HEIF : RinneContentType
        object JPEG : RinneContentType
        object JXL : RinneContentType
        object PNG : RinneContentType
        object SVG : RinneContentType
        object TIFF : RinneContentType
        object WEBP : RinneContentType
        object XIcon : RinneContentType
    }


    object Message {
        object Any : RinneContentType
        object Http : RinneContentType
    }

    object MultiPart {
        object Any : RinneContentType
        object Mixed : RinneContentType
        object Alternative : RinneContentType
        object Related : RinneContentType
        object FormData : RinneContentType
        object Signed : RinneContentType
        object Encrypted : RinneContentType
        object ByteRanges : RinneContentType
    }

    object Text {
        object Any : RinneContentType
        object Plain : RinneContentType
        object CSS : RinneContentType
        object CSV : RinneContentType
        object Html : RinneContentType
        object JavaScript : RinneContentType
        object VCard : RinneContentType
        object Xml : RinneContentType
        object EventStream : RinneContentType
    }

    object Video {
        object Any : RinneContentType
        object MPEG : RinneContentType
        object MP4 : RinneContentType
        object OGG : RinneContentType
        object QuickTime : RinneContentType
    }

    object Font {
        object Any : RinneContentType
        object Collection : RinneContentType
        object Otf : RinneContentType
        object Sfnt : RinneContentType
        object Ttf : RinneContentType
        object Woff : RinneContentType
        object Woff2 : RinneContentType
    }
}
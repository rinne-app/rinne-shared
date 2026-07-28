package com.rinne.libraries.network.client.ktor.extensions

import com.rinne.libraries.network.client.core.model.RinneContentType
import io.ktor.http.ContentType

internal fun RinneContentType.asKtor() = when (this) {
    is RinneContentType.Custom -> ContentType(contentType, contentSubtype)

    RinneContentType.Application -> ContentType.Application.Any
    RinneContentType.Application.Any -> ContentType.Application.Any
    RinneContentType.Application.Atom -> ContentType.Application.Atom
    RinneContentType.Application.Cbor -> ContentType.Application.Cbor
    RinneContentType.Application.Json -> ContentType.Application.Json
    RinneContentType.Application.HalJson -> ContentType.Application.HalJson
    RinneContentType.Application.JavaScript -> ContentType.Application.JavaScript
    RinneContentType.Application.OctetStream -> ContentType.Application.OctetStream
    RinneContentType.Application.Rss -> ContentType.Application.Rss
    RinneContentType.Application.Soap -> ContentType.Application.Soap
    RinneContentType.Application.Xml -> ContentType.Application.Xml
    RinneContentType.Application.Xml_Dtd -> ContentType.Application.Xml_Dtd
    RinneContentType.Application.Yaml -> ContentType.Application.Yaml
    RinneContentType.Application.Zip -> ContentType.Application.Zip
    RinneContentType.Application.GZip -> ContentType.Application.GZip
    RinneContentType.Application.FormUrlEncoded -> ContentType.Application.FormUrlEncoded
    RinneContentType.Application.Pdf -> ContentType.Application.Pdf
    RinneContentType.Application.Xlsx -> ContentType.Application.Xlsx
    RinneContentType.Application.Docx -> ContentType.Application.Docx
    RinneContentType.Application.Pptx -> ContentType.Application.Pptx
    RinneContentType.Application.ProtoBuf -> ContentType.Application.ProtoBuf
    RinneContentType.Application.Wasm -> ContentType.Application.Wasm
    RinneContentType.Application.ProblemJson -> ContentType.Application.ProblemJson
    RinneContentType.Application.ProblemXml -> ContentType.Application.ProblemXml

    RinneContentType.Audio.Any -> ContentType.Audio.Any
    RinneContentType.Audio.MP4 -> ContentType.Audio.MP4
    RinneContentType.Audio.MPEG -> ContentType.Audio.MPEG
    RinneContentType.Audio.OGG -> ContentType.Audio.OGG

    RinneContentType.Image.Any -> ContentType.Image.Any
    RinneContentType.Image.APNG -> ContentType.Image.APNG
    RinneContentType.Image.AVIF -> ContentType.Image.AVIF
    RinneContentType.Image.BMP -> ContentType.Image.BMP
    RinneContentType.Image.GIF -> ContentType.Image.GIF
    RinneContentType.Image.HEIC -> ContentType.Image.HEIC
    RinneContentType.Image.HEIF -> ContentType.Image.HEIF
    RinneContentType.Image.JPEG -> ContentType.Image.JPEG
    RinneContentType.Image.JXL -> ContentType.Image.JXL
    RinneContentType.Image.PNG -> ContentType.Image.PNG
    RinneContentType.Image.SVG -> ContentType.Image.SVG
    RinneContentType.Image.TIFF -> ContentType.Image.TIFF
    RinneContentType.Image.WEBP -> ContentType.Image.WEBP
    RinneContentType.Image.XIcon -> ContentType.Image.XIcon

    RinneContentType.Message.Any -> ContentType.Message.Any
    RinneContentType.Message.Http -> ContentType.Message.Http

    RinneContentType.MultiPart.Any -> ContentType.MultiPart.Any
    RinneContentType.MultiPart.Mixed -> ContentType.MultiPart.Mixed
    RinneContentType.MultiPart.Alternative -> ContentType.MultiPart.Alternative
    RinneContentType.MultiPart.Related -> ContentType.MultiPart.Related
    RinneContentType.MultiPart.FormData -> ContentType.MultiPart.FormData
    RinneContentType.MultiPart.Signed -> ContentType.MultiPart.Signed
    RinneContentType.MultiPart.Encrypted -> ContentType.MultiPart.Encrypted
    RinneContentType.MultiPart.ByteRanges -> ContentType.MultiPart.ByteRanges

    RinneContentType.Text.Any -> ContentType.Text.Any
    RinneContentType.Text.Plain -> ContentType.Text.Plain
    RinneContentType.Text.CSS -> ContentType.Text.CSS
    RinneContentType.Text.CSV -> ContentType.Text.CSV
    RinneContentType.Text.Html -> ContentType.Text.Html
    RinneContentType.Text.JavaScript -> ContentType.Text.JavaScript
    RinneContentType.Text.VCard -> ContentType.Text.VCard
    RinneContentType.Text.Xml -> ContentType.Text.Xml
    RinneContentType.Text.EventStream -> ContentType.Text.EventStream

    RinneContentType.Video.Any -> ContentType.Video.Any
    RinneContentType.Video.MPEG -> ContentType.Video.MPEG
    RinneContentType.Video.MP4 -> ContentType.Video.MP4
    RinneContentType.Video.OGG -> ContentType.Video.OGG
    RinneContentType.Video.QuickTime -> ContentType.Video.QuickTime

    RinneContentType.Font.Any -> ContentType.Font.Any
    RinneContentType.Font.Collection -> ContentType.Font.Collection
    RinneContentType.Font.Otf -> ContentType.Font.Otf
    RinneContentType.Font.Sfnt -> ContentType.Font.Sfnt
    RinneContentType.Font.Ttf -> ContentType.Font.Ttf
    RinneContentType.Font.Woff -> ContentType.Font.Woff
    RinneContentType.Font.Woff2 -> ContentType.Font.Woff2
}

internal fun ContentType.asRinne(): RinneContentType {
    val contentType = withoutParameters()

    return when (contentType) {
        ContentType.Application.Any -> RinneContentType.Application.Any
        ContentType.Application.Atom -> RinneContentType.Application.Atom
        ContentType.Application.Cbor -> RinneContentType.Application.Cbor
        ContentType.Application.Json -> RinneContentType.Application.Json
        ContentType.Application.HalJson -> RinneContentType.Application.HalJson
        ContentType.Application.JavaScript -> RinneContentType.Application.JavaScript
        ContentType.Application.OctetStream -> RinneContentType.Application.OctetStream
        ContentType.Application.Rss -> RinneContentType.Application.Rss
        ContentType.Application.Soap -> RinneContentType.Application.Soap
        ContentType.Application.Xml -> RinneContentType.Application.Xml
        ContentType.Application.Xml_Dtd -> RinneContentType.Application.Xml_Dtd
        ContentType.Application.Yaml -> RinneContentType.Application.Yaml
        ContentType.Application.Zip -> RinneContentType.Application.Zip
        ContentType.Application.GZip -> RinneContentType.Application.GZip
        ContentType.Application.FormUrlEncoded -> RinneContentType.Application.FormUrlEncoded
        ContentType.Application.Pdf -> RinneContentType.Application.Pdf
        ContentType.Application.Xlsx -> RinneContentType.Application.Xlsx
        ContentType.Application.Docx -> RinneContentType.Application.Docx
        ContentType.Application.Pptx -> RinneContentType.Application.Pptx
        ContentType.Application.ProtoBuf -> RinneContentType.Application.ProtoBuf
        ContentType.Application.Wasm -> RinneContentType.Application.Wasm
        ContentType.Application.ProblemJson -> RinneContentType.Application.ProblemJson
        ContentType.Application.ProblemXml -> RinneContentType.Application.ProblemXml

        ContentType.Audio.Any -> RinneContentType.Audio.Any
        ContentType.Audio.MP4 -> RinneContentType.Audio.MP4
        ContentType.Audio.MPEG -> RinneContentType.Audio.MPEG
        ContentType.Audio.OGG -> RinneContentType.Audio.OGG

        ContentType.Image.Any -> RinneContentType.Image.Any
        ContentType.Image.APNG -> RinneContentType.Image.APNG
        ContentType.Image.AVIF -> RinneContentType.Image.AVIF
        ContentType.Image.BMP -> RinneContentType.Image.BMP
        ContentType.Image.GIF -> RinneContentType.Image.GIF
        ContentType.Image.HEIC -> RinneContentType.Image.HEIC
        ContentType.Image.HEIF -> RinneContentType.Image.HEIF
        ContentType.Image.JPEG -> RinneContentType.Image.JPEG
        ContentType.Image.JXL -> RinneContentType.Image.JXL
        ContentType.Image.PNG -> RinneContentType.Image.PNG
        ContentType.Image.SVG -> RinneContentType.Image.SVG
        ContentType.Image.TIFF -> RinneContentType.Image.TIFF
        ContentType.Image.WEBP -> RinneContentType.Image.WEBP
        ContentType.Image.XIcon -> RinneContentType.Image.XIcon

        ContentType.Message.Any -> RinneContentType.Message.Any
        ContentType.Message.Http -> RinneContentType.Message.Http

        ContentType.MultiPart.Any -> RinneContentType.MultiPart.Any
        ContentType.MultiPart.Mixed -> RinneContentType.MultiPart.Mixed
        ContentType.MultiPart.Alternative -> RinneContentType.MultiPart.Alternative
        ContentType.MultiPart.Related -> RinneContentType.MultiPart.Related
        ContentType.MultiPart.FormData -> RinneContentType.MultiPart.FormData
        ContentType.MultiPart.Signed -> RinneContentType.MultiPart.Signed
        ContentType.MultiPart.Encrypted -> RinneContentType.MultiPart.Encrypted
        ContentType.MultiPart.ByteRanges -> RinneContentType.MultiPart.ByteRanges

        ContentType.Text.Any -> RinneContentType.Text.Any
        ContentType.Text.Plain -> RinneContentType.Text.Plain
        ContentType.Text.CSS -> RinneContentType.Text.CSS
        ContentType.Text.CSV -> RinneContentType.Text.CSV
        ContentType.Text.Html -> RinneContentType.Text.Html
        ContentType.Text.JavaScript -> RinneContentType.Text.JavaScript
        ContentType.Text.VCard -> RinneContentType.Text.VCard
        ContentType.Text.Xml -> RinneContentType.Text.Xml
        ContentType.Text.EventStream -> RinneContentType.Text.EventStream

        ContentType.Video.Any -> RinneContentType.Video.Any
        ContentType.Video.MPEG -> RinneContentType.Video.MPEG
        ContentType.Video.MP4 -> RinneContentType.Video.MP4
        ContentType.Video.OGG -> RinneContentType.Video.OGG
        ContentType.Video.QuickTime -> RinneContentType.Video.QuickTime

        ContentType.Font.Any -> RinneContentType.Font.Any
        ContentType.Font.Collection -> RinneContentType.Font.Collection
        ContentType.Font.Otf -> RinneContentType.Font.Otf
        ContentType.Font.Sfnt -> RinneContentType.Font.Sfnt
        ContentType.Font.Ttf -> RinneContentType.Font.Ttf
        ContentType.Font.Woff -> RinneContentType.Font.Woff
        ContentType.Font.Woff2 -> RinneContentType.Font.Woff2

        else -> RinneContentType.Custom(contentType.contentType, contentType.contentSubtype)
    }
}

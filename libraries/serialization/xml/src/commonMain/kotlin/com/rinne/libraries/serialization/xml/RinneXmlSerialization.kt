package com.rinne.libraries.serialization.xml

class RinneXmlSerialization {

    fun decode(raw: String): XmlTag {
        return XmlPullParser(raw).parse()
//         .decodeFromString(HelloWorld.serializer(), "<HelloWorld user='You!' />")
    }
}

data class XmlTag(
    val tag: String,
    val attributes: List<XmlAttribute>,
    val content: List<XmlTagContent>,
)

sealed interface XmlTagContent {
    data class Text(val value: String) : XmlTagContent
    data class Tag(val tag: XmlTag) : XmlTagContent
}

data class XmlAttribute(
    val name: String,
    val value: String,
)

class XmlPullParser(private val input: String) {
    private var index = 0

    fun parse(): XmlTag {
        skipUntil('<')
        return parseTag()
    }

    private fun parseTag(): XmlTag {
        expect('<')
        val tagName = readUntil { it.isWhitespace() || it == '>' || it == '/' }

        val attributes = mutableListOf<XmlAttribute>()
        while (peek() != '>' && peek() != '/') {
            skipWhitespace()
            if (peek() == '>' || peek() == '/') break

            val attrName = readUntil('=')
            expect('=')
            expect('"')
            val attrValue = readUntil('"')
            expect('"')
            attributes.add(XmlAttribute(attrName, attrValue))
        }

        val selfClosing = if (peek() == '/') {
            expect('/')
            expect('>')
            return XmlTag(tagName, attributes, emptyList())
        } else {
            expect('>')
        }

        val content = mutableListOf<XmlTagContent>()

        while (true) {
            skipWhitespace()
            if (peek() == '<') {
                if (peek(1) == '/') {
                    break // closing tag
                } else {
                    val tag = parseTag()
                    content.add(XmlTagContent.Tag(tag))
                }
            } else {
                val text = readUntil('<')
                if (text.isNotBlank()) {
                    content.add(XmlTagContent.Text(text))
                }
            }
        }

        // Closing tag
        expect('<')
        expect('/')
        val closingTagName = readUntil('>')
        expect('>')

        if (closingTagName != tagName) {
            throw IllegalArgumentException("Expected closing tag </$tagName> but found </$closingTagName>")
        }

        return XmlTag(tagName, attributes, content)
    }

    // Helpers
    private fun peek(offset: Int = 0): Char = input.getOrNull(index + offset) ?: 0.toChar()
    private fun expect(c: Char) {
        if (peek() != c) throw IllegalArgumentException("Expected '$c' but found '${peek()}' at $index")
        index++
    }

    private fun skipUntil(c: Char) {
        while (peek() != c && index < input.length) index++
    }

    private fun skipWhitespace() {
        while (peek().isWhitespace()) index++
    }

    private fun readUntil(stopChar: Char): String {
        val start = index
        while (peek() != stopChar && index < input.length) index++
        return input.substring(start, index)
    }

    private fun readUntil(condition: (Char) -> Boolean): String {
        val start = index
        while (!condition(peek()) && index < input.length) index++
        return input.substring(start, index)
    }
}

package com.rinne.libraries.book.reader.fb2

import com.rinne.libraries.book.reader.core.RinneBook
import com.rinne.libraries.book.reader.core.RinneBookContent
import com.rinne.libraries.logger.core.RinneLogger
import com.rinne.libraries.logger.core.extensions.i
import com.rinne.libraries.serialization.xml.RinneXmlSerialization
import com.rinne.libraries.serialization.xml.XmlTag
import com.rinne.libraries.serialization.xml.XmlTagContent

class RinneBookReaderFb2 {

    fun read(raw: String): RinneBook {
        val result = RinneXmlSerialization().decode(raw)

        val content = buildList<RinneBookContent> {
            result.allContent().forEach {
                when (it) {
                    is XmlTagContent.Tag -> {
//                        val text = it.tag.content.filterIsInstance<XmlTagContent.Text>()
//                            .joinToString("\n\n") { it.value }
//
//                        when (it.tag.tag) {
//                            "strong" -> add(RinneBookContent.Title(text))
//                            else -> add(RinneBookContent.Text(text))
//                        }
                    }

                    is XmlTagContent.Text -> add(RinneBookContent.Text(it.value))
                }
            }
        }

        return RinneBook(content)
    }
}


private fun XmlTag.allContent(): List<XmlTagContent> {
    if (content.isEmpty()) return emptyList()

    return content.flatMap {
        val innerContent = if (it is XmlTagContent.Tag) it.tag.allContent() else emptyList()

        listOf(it) + innerContent
    }
}
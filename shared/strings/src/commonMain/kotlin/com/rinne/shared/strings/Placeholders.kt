package com.rinne.shared.strings

/** What a placeholder takes: `%1$s` a text, `%1$d` a number. */
enum class PlaceholderType { Text, Number }

/**
 * The placeholders of a template by argument position (1-based): `"%1$s has %2$d notes"` →
 * `{1: Text, 2: Number}`. Remote texts must have the signature of the build's English text.
 */
data class PlaceholderSignature(val placeholders: Map<Int, PlaceholderType>) {

    /** Whether [other] uses only placeholders of this signature, each with the same type. */
    fun accepts(other: PlaceholderSignature): Boolean =
        other.placeholders.all { (position, type) -> placeholders[position] == type }

    companion object {
        val Empty = PlaceholderSignature(emptyMap())

        /** @throws IllegalArgumentException for a malformed placeholder or one position with two types. */
        fun of(template: String): PlaceholderSignature {
            val placeholders = mutableMapOf<Int, PlaceholderType>()
            Template.tokens(template).forEach { token ->
                if (token is Template.Token.Placeholder) {
                    val previous = placeholders.put(token.position, token.type)
                    require(previous == null || previous == token.type) {
                        "Placeholder %${token.position} is used as both $previous and ${token.type}"
                    }
                }
            }
            return PlaceholderSignature(placeholders)
        }
    }
}

/**
 * Template syntax: `%1$s`, `%1$d` (explicit positions), `%s` / `%d` (the next position) and `%%` (a
 * percent sign). Texts are plain text — never markup.
 */
object Template {

    /** Substitutes already formatted [arguments] (position 1 is `arguments[0]`). A missing argument stays empty. */
    fun format(template: String, arguments: List<String>): String = buildString {
        tokens(template).forEach { token ->
            when (token) {
                is Token.Literal -> append(token.text)
                is Token.Placeholder -> append(arguments.getOrNull(token.position - 1).orEmpty())
            }
        }
    }

    internal sealed interface Token {
        data class Literal(val text: String) : Token
        data class Placeholder(val position: Int, val type: PlaceholderType) : Token
    }

    internal fun tokens(template: String): List<Token> {
        val tokens = mutableListOf<Token>()
        val literal = StringBuilder()
        var index = 0
        var nextPosition = 1
        while (index < template.length) {
            val char = template[index]
            if (char != '%') {
                literal.append(char)
                index++
                continue
            }
            require(index + 1 < template.length) { "A template can't end with %" }
            if (template[index + 1] == '%') {
                literal.append('%')
                index += 2
                continue
            }
            val digitsEnd = (index + 1 until template.length).firstOrNull { !template[it].isDigit() } ?: template.length
            val position: Int
            val typeIndex: Int
            when (digitsEnd > index + 1) {
                true -> {
                    require(digitsEnd < template.length && template[digitsEnd] == '$') { "Expected \$ after %<position>" }
                    position = template.substring(index + 1, digitsEnd).toInt()
                    typeIndex = digitsEnd + 1
                }
                false -> {
                    position = nextPosition
                    typeIndex = index + 1
                }
            }
            require(position >= 1) { "Placeholder positions start at 1" }
            val type = when (template.getOrNull(typeIndex)) {
                's' -> PlaceholderType.Text
                'd' -> PlaceholderType.Number
                else -> throw IllegalArgumentException("Unknown placeholder type in \"${template.substring(index)}\"")
            }
            if (literal.isNotEmpty()) {
                tokens += Token.Literal(literal.toString())
                literal.clear()
            }
            tokens += Token.Placeholder(position, type)
            nextPosition = position + 1
            index = typeIndex + 1
        }
        if (literal.isNotEmpty()) tokens += Token.Literal(literal.toString())
        return tokens
    }
}

package com.rinne.libraries.network.client.offline.optimistic

/** A path template such as `tasks/group/{groupId}/task`; `{name}` segments capture values. */
class RinnePathPattern(pattern: String) {
    private val segments = pattern.trim('/').split('/')

    fun match(path: String): Map<String, String>? {
        val pathSegments = path.substringBefore('?').trim('/').split('/')
        if (pathSegments.size != segments.size) return null

        val captured = mutableMapOf<String, String>()
        segments.zip(pathSegments).forEach { (template, actual) ->
            when (template.startsWith('{') && template.endsWith('}')) {
                true -> captured[template.substring(1, template.length - 1)] = actual
                false -> if (template != actual) return null
            }
        }
        return captured
    }
}

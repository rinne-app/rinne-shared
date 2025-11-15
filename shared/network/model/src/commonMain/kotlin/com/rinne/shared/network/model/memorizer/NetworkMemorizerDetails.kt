package com.rinne.shared.network.model.memorizer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class NetworkMemorizerFolder(
    @SerialName("id") val id: String,
    @SerialName("type") val type: NetworkMemorizerFolderType,
    @SerialName("sets") val sets: List<NetworkMemorizerSetInfo>,
)

@Serializable
data class NetworkMemorizerSetInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("path") val path: String = "",
    @SerialName("cardsCount") val cardsCount: Int = 0,
    @SerialName("levels") val levels: List<NetworkMemorizerCardLevel> = emptyList(),
    @SerialName("statuses") val statuses: List<NetworkMemorizerCardStatus> = emptyList(),
    @SerialName("associations") val associations: List<NetworkMemorizerCardAssociation> = emptyList(),
)

@Serializable
data class NetworkMemorizerSet(
    @SerialName("info") val info: NetworkMemorizerSetInfo,
    @SerialName("folders") val folders: List<NetworkMemorizerSetFolder>,
)

@Serializable
data class NetworkMemorizerSetFolder(
    @SerialName("id") val id: String,
    @SerialName("type") val type: NetworkMemorizerSetFolderType,
    @SerialName("cards") val cards: List<NetworkMemorizerCardInfo> = emptyList(),
)

@Serializable
data class NetworkMemorizerSetFolderType(
    @SerialName("type") val type: NetworkMemorizerSetFolderTypes = NetworkMemorizerSetFolderTypes.UNSORTED,
    @SerialName("name") val name: String? = null,
) {
    init {
        if (type == NetworkMemorizerSetFolderTypes.CUSTOM) requireNotNull(name)
    }

    companion object {
        val unsorted = NetworkMemorizerSetFolderType(NetworkMemorizerSetFolderTypes.UNSORTED)
    }
}

enum class NetworkMemorizerSetFolderTypes {
    UNSORTED, LEARNED, CUSTOM;

    companion object
}


fun NetworkMemorizerSetFolderTypes.Companion.valueOfOrUnsorted(name: String): NetworkMemorizerSetFolderTypes {
    return NetworkMemorizerSetFolderTypes.entries.firstOrNull { it.name == name }
        ?: NetworkMemorizerSetFolderTypes.UNSORTED
}


@Serializable
data class NetworkMemorizerFolderType(
    @SerialName("type") val type: NetworkMemorizerFolderTypes = NetworkMemorizerFolderTypes.UNSORTED,
    @SerialName("name") val name: String? = null,
) {
    init {
        if (type == NetworkMemorizerFolderTypes.CUSTOM) requireNotNull(name)
    }

    companion object {
        val unsorted = NetworkMemorizerFolderType(NetworkMemorizerFolderTypes.UNSORTED)
    }
}

enum class NetworkMemorizerFolderTypes {
    UNSORTED, CUSTOM;

    companion object
}

fun NetworkMemorizerFolderTypes.Companion.valueOfOrUnsorted(name: String): NetworkMemorizerFolderTypes {
    return NetworkMemorizerFolderTypes.entries.firstOrNull { it.name == name }
        ?: NetworkMemorizerFolderTypes.UNSORTED
}

@Serializable
data class NetworkMemorizerCardInfo(
    @SerialName("id") val id: String,
    @SerialName("front") val front: String,
    @SerialName("back") val back: String,
    @SerialName("notes") val notes: List<NetworkMemorizerCardNoteInfo> = emptyList(),
)

@Serializable
data class NetworkMemorizerCardNoteInfo(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("text") val text: String,
)

@Serializable
data class NetworkMemorizerCardNoteDetails(
    @SerialName("info") val info: NetworkMemorizerCardNoteInfo,
    @SerialName("cardInfo") val cardInfo: NetworkMemorizerCardInfo,
)

@Serializable
data class NetworkMemorizerCardDetails(
    @SerialName("info") val info: NetworkMemorizerCardInfo,
    @SerialName("level") val level: NetworkMemorizerCardLevel,
    @SerialName("status") val status: NetworkMemorizerCardStatus,
    @SerialName("setInfo") val setInfo: NetworkMemorizerSetInfo? = null,
    @SerialName("associations") val associations: List<NetworkMemorizerCardAssociation> = emptyList(),
    @SerialName("notes") val notes: List<NetworkMemorizerCardNoteInfo> = emptyList(),
)

@Serializable
data class NetworkMemorizerCardLevel(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
) {
    companion object {
        val languageDefault = listOf(
            NetworkMemorizerCardLevel("A1", "A1"),
            NetworkMemorizerCardLevel("A2", "A2"),
            NetworkMemorizerCardLevel("B1", "B1"),
            NetworkMemorizerCardLevel("B2", "B2"),
            NetworkMemorizerCardLevel("C1", "C1"),
            NetworkMemorizerCardLevel("C2", "C2"),
        )
    }
}

@Serializable
data class NetworkMemorizerCardStatus(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
) {
    companion object {
        val default = listOf(
            NetworkMemorizerCardStatus("New", "New"),
            NetworkMemorizerCardStatus("Weak Learning", "Weak Learning"),
            NetworkMemorizerCardStatus("Moderate Learning", "Moderate Learning"),
            NetworkMemorizerCardStatus("Strong Remembered", "Strong Remembered"),
            NetworkMemorizerCardStatus("Excellent Mastered", "Excellent Mastered"),
        )
    }
}

@Serializable
data class NetworkMemorizerCardAssociation(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
)
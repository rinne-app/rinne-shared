package com.rinne.shared.network.model.deprecated.memorizer

import com.rinne.shared.network.model.deprecated.ProfileInfo

data class MemorizerDetails(
    val userInfo: ProfileInfo,
    val folders: List<MemorizerFolderInfo>,
)

data class MemorizerSetInfo(
    val id: String,
    val name: String,
    val path: String,
    val cardsCount: Int,
)

data class MemorizerFolderInfo(
    val id: String,
    val sets: List<MemorizerSetInfo>,
    val type: MemorizerFolderType,
)

sealed interface MemorizerFolderType {
    data object Unsorted : MemorizerFolderType
    data class Default(val name: String) : MemorizerFolderType
}

data class MemorizerSetDetails(
    val id: String,
    val name: String,
    val user: ProfileInfo,
    val path: String,
    val cards: List<MemorizerCardInfo>,
)


data class MemorizerCardInfo(
    val id: String,
    val front: String,
    val back: String,
)

data class MemorizerCardDetails(
    val id: String,
    val front: String,
    val back: String,
    val setInfo: MemorizerSetInfo,  //TODO to MemorizerCardDetails
)

fun MemorizerSetDetails.asInfo() = MemorizerSetInfo(
    id = id,
    name = name,
    cardsCount = cards.size,
    path = path,
)

fun MemorizerCardInfo.asDetails(setInfo: MemorizerSetInfo) = MemorizerCardDetails(
    id = id,
    front = front,
    back = back,
    setInfo = setInfo,
)

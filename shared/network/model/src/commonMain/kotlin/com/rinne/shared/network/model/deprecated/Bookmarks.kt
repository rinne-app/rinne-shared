package com.rinne.shared.network.model.deprecated

data class BookmarksInfo(
    val owner: ProfileInfo,
    val folders: List<BookmarkFolder>,
)

data class BookmarkFolder(
    val id: String,
    val bookmarks: List<BookmarkInfo>,
    val type: BookmarkFolderType,
)

data class BookmarkInfo(
    val id: String,
    val name: String,
    val description: String,
    val type: BookmarkType,
)

sealed interface BookmarkType {
    data class Page(val id: String) : BookmarkType
    data class Article(val id: String) : BookmarkType
    data class Link(val url: String) : BookmarkType
}

sealed interface BookmarkFolderType {
    data object Unsorted : BookmarkFolderType
    data class Default(val name: String) : BookmarkFolderType
}
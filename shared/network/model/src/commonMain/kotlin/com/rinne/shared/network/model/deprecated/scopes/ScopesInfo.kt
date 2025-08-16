package com.rinne.shared.network.model.deprecated.scopes

import com.rinne.shared.network.model.deprecated.PageShortInfo
import com.rinne.shared.network.model.deprecated.ProfileInfo
import com.rinne.shared.network.model.deprecated.memorizer.MemorizerSetInfo

data class ScopesInfo(
    val items: List<ScopeInfo>,
    val owner: ProfileInfo,
)

data class ScopeInfo(
    val id: String,
    val name: String,
    val cover: String?,
    val owner: ProfileInfo,
)

data class ScopeDetails(
    val id: String,
    val name: String,
    val cover: String?,
    val owner: ProfileInfo,
    val pages: List<PageShortInfo>,
    val memorizer: List<MemorizerSetInfo>,
)

fun ScopeDetails.asInfo() = ScopeInfo(
    id = id,
    name = name,
    cover = cover,
    owner = owner,
)
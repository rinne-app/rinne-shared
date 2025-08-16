package com.rinne.shared.network.model.deprecated

import com.rinne.shared.network.model.deprecated.courses.CourseInfo
import com.rinne.shared.network.model.deprecated.courses.CourseThemeInfo

data class HomeInfo(
    val subscriptions: List<HomeSubscription>,
)

sealed interface HomeSubscription {
    data class Page(
        val info: PageShortInfo,
        val articles: List<ArticleInfo>,
    ) : HomeSubscription

    data class Community(
        val info: CommunityInfo,
        val articles: List<ArticleInfo>,
    ) : HomeSubscription

    data class Course(
        val info: CourseInfo,
        val activeTheme: CourseThemeInfo,
    ) : HomeSubscription
}

data class HomePageInfo(
    val info: PageShortInfo,
    val articles: List<ArticleInfo>,
)
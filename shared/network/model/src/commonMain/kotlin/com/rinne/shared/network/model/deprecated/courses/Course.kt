package com.rinne.shared.network.model.deprecated.courses

data class CourseDetails(
    val id: String,
    val cover: String,
    val name: String,
    val accessStatus: CourseAccessStatus,
    val completionStatus: CourseCompletionStatus,
    val sections: List<CourseSection>,
) {
    //TODO
    val activeTheme = sections.flatMap { it.content.filterIsInstance<CourseContent.Theme>() }.first()
}

data class CourseInfo(
    val id: String,
    val cover: String,
    val name: String,
    val accessStatus: CourseAccessStatus,
    val completionStatus: CourseCompletionStatus,
)

fun CourseDetails.asInfo() = CourseInfo(
    id = id,
    cover = cover,
    name = name,
    accessStatus = accessStatus,
    completionStatus = completionStatus,
)

sealed interface CourseSection {
    val content: List<CourseContent>

    data class Default(
        val info: CourseSectionInfo,
        override val content: List<CourseContent>
    ) : CourseSection
}

data class CourseSectionInfo(
    val id: String,
    val name: String,
)

sealed interface CourseAccessStatus {
    data object UserCourse : CourseAccessStatus
    data object None : CourseAccessStatus
}

sealed interface CourseContent {
    val id: String

    data class Theme(
        override val id: String,
        val info: CourseThemeInfo
    ) : CourseContent
}

data class CourseThemeInfo(
    val id: String,
    val name: String,
    val completionStatus: CourseThemeCompletionStatus,
)

data class CourseThemeDetails(
    val id: String,
    val name: String,
    val content: String,
    val memorizerSetId: String,
    val completionStatus: CourseThemeCompletionStatus,
)

data class CourseThemePracticeInfo(
    val id: String,
    val theme: CourseThemeInfo,
    val completionStatus: CoursePracticeCompletionStatus,
)

sealed interface CourseThemeCompletionStatus {
    data object Available : CourseThemeCompletionStatus
    data object Completed : CourseThemeCompletionStatus
    data object InProgress : CourseThemeCompletionStatus
}

sealed interface CoursePracticeCompletionStatus {
    data object Available : CoursePracticeCompletionStatus
    data object Completed : CoursePracticeCompletionStatus
    data class InProgress(val max: Int, val current: Int) : CoursePracticeCompletionStatus
}

sealed interface CourseCompletionStatus {
    data object Available : CourseCompletionStatus
    data object Completed : CourseCompletionStatus
    data object InProgress : CourseCompletionStatus
}
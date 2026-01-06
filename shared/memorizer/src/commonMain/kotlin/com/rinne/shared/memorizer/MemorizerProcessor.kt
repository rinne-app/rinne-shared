package com.rinne.shared.memorizer

import com.rinne.libraries.date.time.core.RinneDateTime

enum class MemorizerProcessorCardStatus {
    NEW,        // Новая, еще не видели
    LEARNING,   // В процессе первичного изучения (проход по шагам)
    REVIEW,     // Выучена, находится в режиме повторения (SM-2)
    RELEARNING, // Забыли (Lapsed), учим заново
}

enum class MemorizerProcessorCardGrade(val value: Int) {
    AGAIN(1), // Полный сброс / Не помню
    HARD(2),  // Вспомнил с трудом
    GOOD(3),  // Нормально
    EASY(4);   // Легко

    companion object {
        fun findBy(value: Int) = when {
            value > 4 -> EASY
            value < 1 -> AGAIN
            else -> entries.firstOrNull { it.value == value } ?: entries.first()
        }
    }
}

data class MemorizerProcessorCardSessionInfo(
    val date: RinneDateTime,
    val grade: MemorizerProcessorCardGrade
)

data class MemorizerProcessorCard(
    val id: String,
    val level: Int, // 1=A1, 2=A2...
    val tags: Set<String> = emptySet(),
    val history: List<MemorizerProcessorCardSessionInfo> = emptyList(),
    val priority: Double = 1.0, // Базовый вес
    val state: MemorizerCardState = MemorizerCardState.empty,
)

data class MemorizerCardState(
    val status: MemorizerProcessorCardStatus = MemorizerProcessorCardStatus.NEW,

    // SM-2 Параметры
    val interval: Int = 0,      // Интервал в днях (для REVIEW)
    val easeFactor: Double = 2.5, // E-Factor (стандарт 2.5)
    val repetitions: Int = 0,     // Кол-во успешных повторений подряд

    // Learning Steps
    val learningStepIndex: Int? = 0, // Текущий шаг внутри learningSteps
    val nextReviewDate: RinneDateTime? = null // Когда показывать снова
) {
    companion object {
        val empty = MemorizerCardState()
    }
}
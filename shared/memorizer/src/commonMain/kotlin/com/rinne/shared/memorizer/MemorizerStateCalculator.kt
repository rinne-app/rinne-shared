package com.rinne.shared.memorizer

import com.rinne.libraries.date.time.core.RinneDateTime
import com.rinne.libraries.date.time.core.RinneDateTimeUnit
import com.rinne.libraries.date.time.core.inWholeMinutes
import kotlin.math.roundToLong

class MemorizerStateCalculator(
    private val config: MemorizerCardsProcessorConfig
) {

    private companion object {
        const val GRADUATE_EASY_INTERVAL_DAYS = 4
        const val GRADUATE_NORMAL_INTERVAL_DAYS = 1
        const val HARD_STEP_MULTIPLIER = 1.5
    }

    fun calculate(history: List<MemorizerProcessorCardSessionInfo>): MemorizerCardState {
        // 1. Сортируем историю по дате, чтобы расчет был последовательным
        val sortedHistory = history.sortedBy { it.date.epochMillis }

        // 2. Исходное состояние (Initial State)
        var currentState = MemorizerCardState(
            status = MemorizerProcessorCardStatus.NEW,
            interval = 0,
            easeFactor = config.initialEaseFactor,
            repetitions = 0,
            learningStepIndex = 0,
            nextReviewDate = null
        )

        val stepsDurations = config.learningStepsDurations.ifEmpty {
            MemorizerCardsProcessorConfig.DefaultLearningSteps
        }
        val stepsMinutes = stepsDurations.map { it.inWholeMinutes }

        for (session in sortedHistory) {
            currentState = applySession(currentState, session, stepsMinutes)
        }

        return currentState
    }

    private fun applySession(
        state: MemorizerCardState,
        session: MemorizerProcessorCardSessionInfo,
        steps: List<Long>
    ): MemorizerCardState {
        return when (state.status) {
            MemorizerProcessorCardStatus.NEW -> handleNew(state, session, steps)
            MemorizerProcessorCardStatus.LEARNING, MemorizerProcessorCardStatus.RELEARNING -> handleLearning(state, session, steps)
            MemorizerProcessorCardStatus.REVIEW -> handleReview(state, session)
        }
    }

    private fun handleReview(
        state: MemorizerCardState,
        session: MemorizerProcessorCardSessionInfo
    ): MemorizerCardState {
        var newInterval = state.interval
        var newEase = state.easeFactor
        var newRepetitions = state.repetitions
        var newStatus = MemorizerProcessorCardStatus.REVIEW
        var nextDate: RinneDateTime

        when (session.grade) {
            MemorizerProcessorCardGrade.AGAIN -> {
                // Забыл -> Lapse -> Relearning
                newStatus = MemorizerProcessorCardStatus.RELEARNING
                newRepetitions = 0
                newInterval = 1
                newEase = (newEase - 0.2).coerceAtLeast(1.3)
                // Ставим дату review в ближайшее будущее (как первый шаг обучения)
                nextDate = session.date.plus(10, RinneDateTimeUnit.TimeUnit.Minute)
                return state.copy(
                    status = newStatus,
                    interval = newInterval,
                    easeFactor = newEase,
                    repetitions = newRepetitions,
                    learningStepIndex = 0,
                    nextReviewDate = nextDate
                )
            }

            MemorizerProcessorCardGrade.HARD -> {
                newInterval = (newInterval * 1.2).toInt().coerceAtLeast(newInterval + 1)
                newEase = (newEase - 0.15).coerceAtLeast(1.3)
            }

            MemorizerProcessorCardGrade.GOOD -> {
                newInterval = (newInterval * newEase).toInt().coerceAtLeast(newInterval + 1)
            }

            MemorizerProcessorCardGrade.EASY -> {
                newInterval = (newInterval * newEase * 1.3).toInt().coerceAtLeast(newInterval + 1)
                newEase += 0.15
            }
        }

        nextDate = session.date.plus(newInterval.toLong(), RinneDateTimeUnit.DateUnit.Day)

        return state.copy(
            status = newStatus,
            interval = newInterval,
            easeFactor = newEase,
            repetitions = newRepetitions + 1,
            nextReviewDate = nextDate
        )
    }

    private fun handleNew(
        state: MemorizerCardState,
        session: MemorizerProcessorCardSessionInfo,
        steps: List<Long>
    ): MemorizerCardState {
        // 2.2. Статус NEW
        return when (session.grade) {
            MemorizerProcessorCardGrade.EASY -> {
                // Если Оценка EASY (4): GRADUATE немедленно
                graduate(state, session.date, isEasy = true)
            }
            MemorizerProcessorCardGrade.GOOD -> {
                // Если Оценка GOOD (3): Переход ко второму шагу обучения (если он есть)
                val stepIndex = if (steps.size > 1) 1 else 0
                val minutes = steps.getOrElse(stepIndex) { steps.first() }
                state.copy(
                    status = MemorizerProcessorCardStatus.LEARNING,
                    learningStepIndex = stepIndex,
                    nextReviewDate = session.date.plus(minutes, RinneDateTimeUnit.TimeUnit.Minute)
                )
            }
            MemorizerProcessorCardGrade.HARD -> {
                // Если Оценка HARD (2): Первый шаг, но с увеличенным интервалом (среднее между 1 и 2 шагом)
                val firstStep = steps.first()
                val secondStep = steps.getOrNull(1) ?: (firstStep * HARD_STEP_MULTIPLIER).toLong()
                val minutes = (firstStep + secondStep) / 2
                state.copy(
                    status = MemorizerProcessorCardStatus.LEARNING,
                    learningStepIndex = 0,
                    nextReviewDate = session.date.plus(minutes, RinneDateTimeUnit.TimeUnit.Minute)
                )
            }
            else -> {
                // AGAIN (1): Переход в LEARNING на первый шаг (самый короткий интервал)
                val firstStepMinutes = steps.first()
                state.copy(
                    status = MemorizerProcessorCardStatus.LEARNING,
                    learningStepIndex = 0,
                    nextReviewDate = session.date.plus(firstStepMinutes, RinneDateTimeUnit.TimeUnit.Minute)
                )
            }
        }
    }

    private fun handleLearning(
        state: MemorizerCardState,
        session: MemorizerProcessorCardSessionInfo,
        steps: List<Long>
    ): MemorizerCardState {
        // 2.3. Статусы LEARNING / RELEARNING
        val currentStepIndex = state.learningStepIndex ?: 0

        return when (session.grade) {
            MemorizerProcessorCardGrade.AGAIN -> {
                // Если Оценка AGAIN (1): Сброс обучения. learningStepIndex -> 0.
                // nextReviewDate устанавливается согласно первому шагу (steps[0]).
                val firstStepMinutes = steps.first()
                state.copy(
                    learningStepIndex = 0,
                    nextReviewDate = session.date.plus(firstStepMinutes, RinneDateTimeUnit.TimeUnit.Minute)
                )
            }
            MemorizerProcessorCardGrade.HARD -> {
                // Если Оценка HARD (2): Повтор текущего шага.
                // Чтобы интервал был между текущим и следующим шагом, берем среднее значение.
                val currentStepMinutes = steps.getOrElse(currentStepIndex) { steps.first() }
                val nextStepMinutes = steps.getOrNull(currentStepIndex + 1) ?: (currentStepMinutes * HARD_STEP_MULTIPLIER).toLong()

                val nextDelayMinutes = (currentStepMinutes + nextStepMinutes) / 2
                state.copy(
                    learningStepIndex = currentStepIndex,
                    nextReviewDate = session.date.plus(nextDelayMinutes, RinneDateTimeUnit.TimeUnit.Minute)
                )
            }
            MemorizerProcessorCardGrade.GOOD -> {
                // Если Оценка GOOD (3): Переход к следующему шагу. learningStepIndex -> Index + 1.
                val nextIndex = currentStepIndex + 1
                if (nextIndex < steps.size) {
                    // Если шаги еще есть
                    val nextStepMinutes = steps[nextIndex]
                    state.copy(
                        learningStepIndex = nextIndex,
                        nextReviewDate = session.date.plus(nextStepMinutes, RinneDateTimeUnit.TimeUnit.Minute)
                    )
                } else {
                    // Если шагов больше нет, вызывается GRADUATE.
                    graduate(state, session.date, isEasy = false)
                }
            }
            MemorizerProcessorCardGrade.EASY -> {
                // Если Оценка EASY (4): GRADUATE немедленно.
                graduate(state, session.date, isEasy = true)
            }
        }
    }

    private fun graduate(
        state: MemorizerCardState,
        date: RinneDateTime,
        isEasy: Boolean
    ): MemorizerCardState {
        // 2.4. Переход GRADUATE (Learning -> Review)
        val newInterval = if (isEasy) GRADUATE_EASY_INTERVAL_DAYS else GRADUATE_NORMAL_INTERVAL_DAYS

        return state.copy(
            status = MemorizerProcessorCardStatus.REVIEW,
            repetitions = 1,
            interval = newInterval,
            learningStepIndex = null,
            nextReviewDate = date.plus(newInterval.toLong(), RinneDateTimeUnit.DateUnit.Day)
        )
    }
}

package com.rinne.shared.memorizer

import com.rinne.libraries.date.time.core.*
import kotlin.math.max
import kotlin.math.roundToInt

object CardScheduler {

    // Стандартные константы SM-2
    private const val MIN_ISE = 1.3

    /**
     * Рассчитывает следующее состояние карточки на основе текущего состояния и оценки.
     */
    fun calculateNextState(
        card: MemorizerProcessorCard,
        grade: MemorizerProcessorCardGrade,
        learningStepsMinutes: List<RinneDuration>,
        now: RinneDateTime = RinneDateTime.now()
    ): MemorizerProcessorCard {
        // Добавляем запись в историю
        val newHistory = card.history + MemorizerProcessorCardSessionInfo(now, grade)

        // В зависимости от текущего статуса применяем разную логику
        return when (card.state.status) {
            MemorizerProcessorCardStatus.NEW -> handleNew(
                card,
                grade,
                learningStepsMinutes,
                now
            ).copy(history = newHistory)

            MemorizerProcessorCardStatus.LEARNING, MemorizerProcessorCardStatus.RELEARNING -> handleLearning(
                card,
                grade,
                learningStepsMinutes,
                now
            ).copy(
                history = newHistory
            )

            MemorizerProcessorCardStatus.REVIEW -> handleReview(card, grade, now).copy(history = newHistory)
            else -> card.copy(history = newHistory) // SUSPENDED/BURIED без изменений логики пока
        }
    }

    private fun handleNew(
        card: MemorizerProcessorCard,
        grade: MemorizerProcessorCardGrade,
        steps: List<RinneDuration>,
        now: RinneDateTime
    ): MemorizerProcessorCard {
        // Любая оценка переводит NEW -> LEARNING (или сразу REVIEW, если EASY)
        return if (grade == MemorizerProcessorCardGrade.EASY) {
            // Сразу выучил -> Graduate
            graduateCard(card, grade, now)
        } else {
            // Начинаем учить с 1-го шага
            val firstStepMin = steps.first()
            card.copy(
                state = card.state.copy(
                    status = MemorizerProcessorCardStatus.LEARNING,
                    learningStepIndex = 0,
                    interval = 0,
                    nextReviewDate = now.plus(firstStepMin)
                )
            )
        }
    }

    private fun handleLearning(
        card: MemorizerProcessorCard,
        grade: MemorizerProcessorCardGrade,
        steps: List<RinneDuration>,
        now: RinneDateTime
    ): MemorizerProcessorCard {
        return when (grade) {
            MemorizerProcessorCardGrade.AGAIN -> {
                // Сброс шагов, начинаем сначала (но статус остается LEARNING/RELEARNING)
                val firstStep = steps.first()
                card.copy(
                    state = card.state.copy(
                        learningStepIndex = 0,
                        nextReviewDate = now.plus(firstStep)
                    )
                )
            }

            MemorizerProcessorCardGrade.HARD -> {
                // Повторить этот же шаг (в Anki обычно среднее между текущим и следующим, здесь упростим: через 1.5 времени текущего шага)
                val currentStepMin = steps.getOrNull(card.state.learningStepIndex ?: 0) ?: steps.first()
                card.copy(
                    state = card.state.copy(
                        nextReviewDate = now.plus(currentStepMin * 1.5)
                    )
                )
            }

            MemorizerProcessorCardGrade.GOOD -> {
                // Переход к следующему шагу
                val nextIndex = (card.state.learningStepIndex ?: 0) + 1
                if (nextIndex >= steps.size) {
                    // Шаги закончились -> Graduate
                    graduateCard(card, grade, now)
                } else {
                    val nextStepMin = steps[nextIndex]
                    card.copy(
                        state = card.state.copy(
                            learningStepIndex = nextIndex,
                            nextReviewDate = now.plus(nextStepMin)
                        )
                    )
                }
            }

            MemorizerProcessorCardGrade.EASY -> graduateCard(card, grade, now)
        }
    }

    private fun handleReview(
        card: MemorizerProcessorCard,
        grade: MemorizerProcessorCardGrade,
        now: RinneDateTime
    ): MemorizerProcessorCard {
        var newInterval = card.state.interval
        var newEase = card.state.easeFactor
        var newRepetitions = card.state.repetitions
        var newStatus = MemorizerProcessorCardStatus.REVIEW
        var nextDate: RinneDateTime

        when (grade) {
            MemorizerProcessorCardGrade.AGAIN -> {
                // Забыл -> Lapse -> Relearning
                newStatus = MemorizerProcessorCardStatus.RELEARNING
                newRepetitions = 0
                newInterval = 1 // Сброс интервала
                newEase = max(MIN_ISE, newEase - 0.2) // Штраф к Ease
                // Ставим дату review в ближайшее будущее (как первый шаг обучения)
                nextDate = now.plusMinutes(10) // Хардкод для примера, можно брать из steps
                return card.copy(
                    state = card.state.copy(
                        status = newStatus,
                        interval = newInterval,
                        easeFactor = newEase,
                        repetitions = newRepetitions,
                        learningStepIndex = 0,
                        nextReviewDate = nextDate
                    )
                )
            }

            MemorizerProcessorCardGrade.HARD -> {
                newInterval = (newInterval * 1.2).roundToInt().coerceAtLeast(newInterval + 1)
                newEase = max(MIN_ISE, newEase - 0.15)
            }

            MemorizerProcessorCardGrade.GOOD -> {
                newInterval = (newInterval * newEase).roundToInt().coerceAtLeast(newInterval + 1)
                // Ease не меняется или немного растет (стандарт SM-2 не меняет на Good)
            }

            MemorizerProcessorCardGrade.EASY -> {
                newInterval = (newInterval * newEase * 1.3).roundToInt().coerceAtLeast(newInterval + 1)
                newEase += 0.15
            }
        }

        nextDate = now.plusDays(newInterval.toLong())

        return card.copy(
            state = card.state.copy(
                status = newStatus,
                interval = newInterval,
                easeFactor = newEase,
                repetitions = newRepetitions + 1,
                nextReviewDate = nextDate
            )
        )
    }

    // Перевод карточки в "Выученные" (REVIEW)
    private fun graduateCard(
        card: MemorizerProcessorCard,
        grade: MemorizerProcessorCardGrade,
        now: RinneDateTime
    ): MemorizerProcessorCard {
        val initialInterval = if (grade == MemorizerProcessorCardGrade.EASY) 4 else 1
        return card.copy(
            state = card.state.copy(
                status = MemorizerProcessorCardStatus.REVIEW,
                learningStepIndex = null,
                interval = initialInterval,
                repetitions = 1,
                nextReviewDate = now.plusDays(initialInterval.toLong())
            )
        )
    }
}
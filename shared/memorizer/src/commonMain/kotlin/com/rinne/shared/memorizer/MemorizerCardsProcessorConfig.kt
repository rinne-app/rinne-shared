package com.rinne.shared.memorizer

import com.rinne.libraries.date.time.core.RinneDuration
import com.rinne.libraries.date.time.core.minutes

data class MemorizerCardsProcessorConfig(
    var maxNewCardsPerDay: Int,
    var maxReviewCardsPerDay: Int,
    var currentLevel: Int,
    var levelsMixFactor: Double,
    var learningStepsDurations: List<RinneDuration>,
    var initialEaseFactor: Double = 2.5,
) {
    companion object {
        val DefaultLearningSteps = listOf(1.minutes, 10.minutes)
    }
}
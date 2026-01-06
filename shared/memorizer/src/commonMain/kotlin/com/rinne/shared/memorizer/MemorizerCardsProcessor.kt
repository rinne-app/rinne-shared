package com.rinne.shared.memorizer

import com.rinne.libraries.date.time.core.RinneDuration

/*
save card answer
takeNextCard(previousCard:MemorizerProcessorCard? = null):MemorizerProcessorCard?

//or

session ID
*/

interface MemorizerCardsProcessor {

    val config: MemorizerCardsProcessorConfig
    val cards: List<MemorizerProcessorCard>

    val stateCalculator: MemorizerStateCalculator

    fun setCards(cards: List<MemorizerProcessorCard>)

    /**
     * Получить список карточек для обучения на основе дневных лимитов.
     * Метод сам решает, сколько и каких карточек вернуть.
     * Использует динамическую группировку по тегам для удержания контекста.
     */
    fun getPackForLearning(): List<MemorizerProcessorCard>

    fun nextAvailableCardIn(): RinneDuration

    fun processAnswer(card: MemorizerProcessorCard, grade: MemorizerProcessorCardGrade): MemorizerProcessorCard

    companion object {
        @Suppress("FunctionName")
        fun Default(
            config: MemorizerCardsProcessorConfig,
            allCards: List<MemorizerProcessorCard> = emptyList(),
        ): MemorizerCardsProcessor = MemorizerCardsProcessorImpl(allCards, config)
    }
}
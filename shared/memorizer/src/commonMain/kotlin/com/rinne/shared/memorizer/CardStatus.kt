package com.rinne.shared.memorizer

import com.rinne.libraries.date.time.core.*
import kotlin.math.abs
import kotlin.math.exp

// --- Enums ---


// --- Data Classes ---

//data class CardLearnSessionResult(
//    val date: RinneDateTime,
//    val grade: CardGrade
//)

//data class Card(
//    val id: String,
//    val text: String,
//    val translate: String,
//    val history: List<CardLearnSessionResult> = emptyList(),
//    val level: Int, // 1=A1, 2=A2...
//    val tags: Set<String> = emptySet(),
//    val priority: Double = 1.0, // Базовый вес
//
//    // Состояние (вычисляется/обновляется движком)
//    val status: CardStatus = CardStatus.NEW,
//
//    // SM-2 Параметры
//    val interval: Int = 0,      // Интервал в днях (для REVIEW)
//    val easeFactor: Double = 2.5, // E-Factor (стандарт 2.5)
//    val repetitions: Int = 0,     // Кол-во успешных повторений подряд
//
//    // Learning Steps
//    val learningStepIndex: Int? = 0, // Текущий шаг внутри learningSteps
//    val nextReviewDate: RinneDateTime? = null // Когда показывать снова
//)

// --- Interface ---


internal class MemorizerCardsProcessorImpl(
    allCards: List<MemorizerProcessorCard>,
    override val config: MemorizerCardsProcessorConfig,
) : MemorizerCardsProcessor {

    private var allCards = allCards.toMutableList()
    override val cards: List<MemorizerProcessorCard>
        get() = allCards

    override val stateCalculator = MemorizerStateCalculator(config)

    // Для отслеживания дневных лимитов нам нужно знать, когда началась "сегодня"
    // В реальном приложении это состояние лучше хранить в БД или UserSession
    private val todayNewCardsCount: Int
        get() {
            val startOfDay = RinneDateTime.now().truncatedTo(RinneDateTimeUnit.TimeUnit.Hour)
            // Считаем карточки, у которых первая запись в истории сделана сегодня
            return allCards.count { card ->
                card.history.firstOrNull()?.date?.isAfter(startOfDay) == true
            }
        }

    // Подсчет сделанных review сегодня (для лимита maxReviewCardsPerDay)
    private val todayReviewCount: Int
        get() {
            val startOfDay = RinneDateTime.now().truncatedTo(RinneDateTimeUnit.TimeUnit.Hour)
            return allCards.sumOf { card ->
                card.history.count { it.date.isAfter(startOfDay) && card.state.status != MemorizerProcessorCardStatus.NEW }
            }
        }

    override fun setCards(cards: List<MemorizerProcessorCard>) {
        allCards.apply {
            clear()
            addAll(cards)
        }
    }

    override fun getPackForLearning(): List<MemorizerProcessorCard> {
        val now = RinneDateTime.now()
        val sessionResult = mutableListOf<MemorizerProcessorCard>()

        // Множество тегов, которые уже попали в текущую выдачу.
        // Будем стараться подбирать следующие карточки, похожие на эти.
        val currentSessionTags = mutableSetOf<String>()

        // -------------------------------------------------------------
        // 1. Сбор карточек для повторения (REVIEW + LEARNING + RELEARNING)
        // -------------------------------------------------------------

        // Считаем доступный остаток лимита на сегодня
        val reviewsLeft = (config.maxReviewCardsPerDay - todayReviewCount).coerceAtLeast(0)

        if (reviewsLeft > 0) {
            // Находим всех кандидатов, у которых наступил срок
            val dueCandidates = allCards.filterTo(mutableListOf()) { card ->
                val isActive = card.state.status != MemorizerProcessorCardStatus.NEW
                val isDue = card.state.nextReviewDate?.isBefore(now) == true
                isActive && isDue
            }

            // Жадный выбор: заполняем лимит, каждый раз пересчитывая приоритет
            // с учетом того, какие теги мы уже набрали.
            while (sessionResult.size < reviewsLeft && dueCandidates.isNotEmpty()) {

                // Ищем лучшего кандидата из оставшихся
                val bestCandidate = dueCandidates.maxByOrNull { card ->
                    calculateReviewScore(card, now, currentSessionTags)
                }!! // !! безопасно, так как проверили isNotEmpty

                dueCandidates.remove(bestCandidate)
                sessionResult.add(bestCandidate)
                currentSessionTags.addAll(bestCandidate.tags)
            }
        }

        // -------------------------------------------------------------
        // 2. Сбор НОВЫХ карточек (NEW)
        // -------------------------------------------------------------

        val newCardsLeft = (config.maxNewCardsPerDay - todayNewCardsCount).coerceAtLeast(0)

        if (newCardsLeft > 0) {
            val newCandidates = allCards.filterTo(mutableListOf()) {
                it.state.status == MemorizerProcessorCardStatus.NEW
            }

            var addedNewCount = 0
            while (addedNewCount < newCardsLeft && newCandidates.isNotEmpty()) {

                val bestNew = newCandidates.maxByOrNull { card ->
                    calculateNewCardScore(card, currentSessionTags)
                }!!

                newCandidates.remove(bestNew)
                sessionResult.add(bestNew)
                currentSessionTags.addAll(bestNew.tags)
                addedNewCount++
            }
        }

        return sessionResult
    }

    override fun nextAvailableCardIn(): RinneDuration {
        val now = RinneDateTime.now()
        val reviewsLeft = (config.maxReviewCardsPerDay - todayReviewCount).coerceAtLeast(0)
        val newCardsLeft = (config.maxNewCardsPerDay - todayNewCardsCount).coerceAtLeast(0)

        val hasNewCards = allCards.any { it.state.status == MemorizerProcessorCardStatus.NEW }

        // 1. Проверяем, доступно ли что-то прямо сейчас
        val hasDueReviews = allCards.any { card ->
            card.state.status != MemorizerProcessorCardStatus.NEW &&
                    card.state.nextReviewDate?.isBefore(now) == true
        }

        if (reviewsLeft > 0 && hasDueReviews) return RinneDuration.ZERO
        if (newCardsLeft > 0 && hasNewCards) return RinneDuration.ZERO

        // 2. Если нет, считаем время до следующего события
        val startOfNextDay = now.truncatedTo(RinneDateTimeUnit.TimeUnit.Hour).plus(1, RinneDateTimeUnit.DateUnit.Day)

        val waitTimes = mutableListOf<RinneDateTime>()

        // Время до следующего запланированного повторения
        val earliestReviewDate = allCards
            .filter { it.state.status != MemorizerProcessorCardStatus.NEW }
            .mapNotNull { it.state.nextReviewDate }
            .minOrNull()

        if (reviewsLeft > 0) {
            // Лимит на повторения еще не исчерпан, ждем ближайшего в расписании
            if (earliestReviewDate != null) {
                waitTimes.add(earliestReviewDate)
            }
        } else {
            // Лимит на повторения исчерпан, ждем начала следующего дня
            waitTimes.add(startOfNextDay)
        }

        if (newCardsLeft == 0 && hasNewCards) {
            // Лимит на новые карточки исчерпан, они появятся только завтра
            waitTimes.add(startOfNextDay)
        }

        val earliestNext = waitTimes.minOrNull() ?: return RinneDuration.INFINITE

        return if (now.isAfter(earliestNext)) RinneDuration.ZERO else now.durationBetween(earliestNext)
    }

// --- Вспомогательные функции для расчета "веса" карточки ---

    /**
     * Рассчитывает приоритет для карточек повторения (Review/Learning).
     * Логика:
     * 1. Learning/Relearning всегда важнее Review.
     * 2. Чем больше просрочена дата, тем важнее.
     * 3. Если теги совпадают с уже выбранными (currentSessionTags), даем бонус.
     */
    private fun calculateReviewScore(
        card: MemorizerProcessorCard,
        now: RinneDateTime,
        sessionTags: Set<String>
    ): Double {
        var score = 0.0

        // База: время просрочки в минутах
        val minutesOverdue = card.state.nextReviewDate?.durationBetween(now)?.inWholeMinutes?.toDouble() ?: 0.0
        score += minutesOverdue

        // Приоритет типов: Learning > Review
        if (card.state.status == MemorizerProcessorCardStatus.LEARNING || card.state.status == MemorizerProcessorCardStatus.RELEARNING) {
            score += 1_000_000.0 // Безусловный приоритет перед обычными Review
        }

        // Бонус за контекст (теги)
        if (card.tags.any { it in sessionTags }) {
            // Добавляем эквивалент 2 часов просрочки, чтобы сгруппировать темы,
            // но не перебивать карточки, которые просрочены на дни (они все равно всплывут).
            score += 120.0
        }

        return score
    }

    /**
     * Рассчитывает приоритет для НОВЫХ карточек.
     * Логика:
     * 1. Учитываем уровень (mixFactor) и priority карточки.
     * 2. Сильный бонус за совпадение тегов с текущей сессией.
     */
    private fun calculateNewCardScore(card: MemorizerProcessorCard, sessionTags: Set<String>): Double {
        // 1. Адаптивный вес (SM-2 / Level matching)
        val levelDiff = abs(card.level - config.currentLevel).toDouble()
        // Чем больше разница уровней, тем меньше множитель (при mixFactor > 0)
        val levelPenalty = exp(-levelDiff * config.levelsMixFactor)

        var finalWeight = card.priority * levelPenalty

        // 2. Бонус за контекст
        if (card.tags.any { it in sessionTags }) {
            finalWeight *= 2.5 // Значительный буст (x2.5), чтобы подтянуть карточки той же темы
        }

        return finalWeight
    }

    override fun processAnswer(
        card: MemorizerProcessorCard,
        grade: MemorizerProcessorCardGrade
    ): MemorizerProcessorCard {
        // 1. Вычисляем новое состояние через Scheduler
        val updatedCard = CardScheduler.calculateNextState(
            card,
            grade,
            config.learningStepsDurations
        )

        // 2. Обновляем карточку в "базе данных" (списке)
        val index = allCards.indexOfFirst { it.id == card.id }
        if (index != -1) {
            allCards[index] = updatedCard
        } else {
            // Если карточки нет (странно), можно выбросить ошибку или добавить
            allCards.add(updatedCard)
        }

        return updatedCard
    }
}
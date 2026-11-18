package com.rinne.shared.network.model.memorizer

import com.rinne.libraries.date.time.core.RinneTimestamp
import com.rinne.shared.network.model.sync.SyncField
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer

/** What every Memorizer entity shares: the entity kinds of links and tags, limits. */
object NetworkMemorizer {
    /** The kind of a set in entity references (`rinne://memorizer_set/<id>`), links and tags. */
    const val KindSet = "memorizer_set"

    /** The kind of a card in entity references (`rinne://memorizer_card/<id>`), links and tags. */
    const val KindCard = "memorizer_card"

    /** The most fields a set may define. */
    const val MaxFields = 32

    /** The lowest and highest grade of a `rate` review (Again, Hard, Good, Easy). */
    const val MinGrade = 1
    const val MaxGrade = 4
}

/**
 * A folder of sets, as synced. Writable fields: `name`, `position` (its place in the user's order: a key
 * compared as text). Folders are flat; a set without a folder is in "Unsorted" (not an entity).
 * Deleting a folder moves its sets to Unsorted.
 */
@Serializable
data class NetworkMemorizerFolder(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val position: String? = null,
) {
    companion object {
        const val SyncType = "memorizer_folder"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val Position = SyncField("position", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(Name, Position)
    }
}

/**
 * A set of cards, as synced. Writable fields: everything but `id` and `createdAt`. `badge` (1–3
 * characters; `null` = derived by the apps), `frontLanguage`/`backLanguage` (language tags), `folderId`
 * (`null` = Unsorted), `position` (order inside the folder), `fields` (the ordered field definitions of
 * its cards; the fields [NetworkMemorizerField.Ids.Front] and [NetworkMemorizerField.Ids.Back] always
 * exist), `settings` (overrides of the defaults; `null` members follow them). Deleting a set deletes its
 * cards and their reviews.
 */
@Serializable
data class NetworkMemorizerSet(
    val id: String,
    val name: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val badge: String? = null,
    val frontLanguage: String? = null,
    val backLanguage: String? = null,
    val folderId: String? = null,
    val position: String? = null,
    val pinned: Boolean = false,
    val onHome: Boolean = false,
    val fields: List<NetworkMemorizerField> = emptyList(),
    val settings: NetworkMemorizerSettings = NetworkMemorizerSettings(),
) {
    companion object {
        const val SyncType = "memorizer_set"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Name = SyncField("name", String.serializer())
        val Badge = SyncField("badge", String.serializer().nullable)
        val FrontLanguage = SyncField("frontLanguage", String.serializer().nullable)
        val BackLanguage = SyncField("backLanguage", String.serializer().nullable)
        val FolderId = SyncField("folderId", String.serializer().nullable)
        val Position = SyncField("position", String.serializer().nullable)
        val Pinned = SyncField("pinned", Boolean.serializer())
        val OnHome = SyncField("onHome", Boolean.serializer())
        val CardFields = SyncField("fields", ListSerializer(NetworkMemorizerField.serializer()))
        val Settings = SyncField("settings", NetworkMemorizerSettings.serializer())

        val All: List<SyncField<*>> = listOf(Name, Badge, FrontLanguage, BackLanguage, FolderId, Position, Pinned, OnHome, CardFields, Settings)
    }
}

/**
 * One field of a set's cards: [id] (unique in the set; the keys of a card's `values`), [name], [hint]
 * (the placeholder), [kind] (one of [Kinds]), [side] (one of [Sides]: where practice shows it),
 * [required], [aiFills] (filled by the card's AI button), [prompt] (the AI instruction; empty = from the name),
 * [reverse] (shown on the question side of a reverse card; `back` always is).
 */
@Serializable
data class NetworkMemorizerField(
    val id: String,
    val name: String,
    val kind: String = Kinds.Text,
    val side: String = Sides.Back,
    val hint: String = "",
    val required: Boolean = false,
    val aiFills: Boolean = false,
    val prompt: String = "",
    val reverse: Boolean = false,
) {
    /** The fields every set has: renamable, never removed, always required. */
    object Ids {
        const val Front = "front"
        const val Back = "back"

        val Fixed: List<String> = listOf(Front, Back)
    }

    object Kinds {
        const val Text = "text"
        const val LongText = "long_text"
        const val Audio = "audio"
        const val Image = "image"
        const val Link = "link"

        val All: List<String> = listOf(Text, LongText, Audio, Image, Link)
    }

    object Sides {
        const val Front = "front"
        const val Back = "back"

        val All: List<String> = listOf(Front, Back)
    }
}

/**
 * Scheduling and practice settings: a set's overrides or the account's defaults. Every member is
 * optional: `null` follows the next level (set → defaults → the apps' built-in values). Steps are in
 * seconds, [retention] is 0…1.
 */
@Serializable
data class NetworkMemorizerSettings(
    val newPerDay: Int? = null,
    val reviewsPerDay: Int? = null,
    val retention: Double? = null,
    val learningSteps: List<Int>? = null,
    val relearningSteps: List<Int>? = null,
    val maxIntervalDays: Int? = null,
    val reverse: Boolean? = null,
    val showExampleOnFront: Boolean? = null,
    val showLinksOnBack: Boolean? = null,
    val autoPlay: Boolean? = null,
    val mixSetsInReviewAll: Boolean? = null,
    val buryRelated: Boolean? = null,
    val postponeDays: Int? = null,
)

/**
 * The account's Memorizer settings (the defaults every set follows), as synced: one per space, whose id is the space's id (sync ids
 * must be UUIDv7, which space ids are). Writable field: `settings`.
 */
@Serializable
data class NetworkMemorizerAccountSettings(
    val id: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val settings: NetworkMemorizerSettings = NetworkMemorizerSettings(),
) {
    companion object {
        const val SyncType = "memorizer_account_settings"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val Settings = SyncField("settings", NetworkMemorizerSettings.serializer())

        val All: List<SyncField<*>> = listOf(Settings)
    }
}

/**
 * A card, as synced. Writable fields: `setId` (moving it to another set), `values` (field id → text),
 * `level` (free text such as `A1`; `null` = none). Tags are its associations, links are entity links.
 * Deleting a card deletes its reviews.
 */
@Serializable
data class NetworkMemorizerCard(
    val id: String,
    val setId: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val values: Map<String, String> = emptyMap(),
    val level: String? = null,
) {
    companion object {
        const val SyncType = "memorizer_card"
    }

    /** The writable fields: the keys of push `fields`. */
    object Fields {
        val SetId = SyncField("setId", String.serializer())
        val Values = SyncField("values", MapSerializer(String.serializer(), String.serializer()))
        val Level = SyncField("level", String.serializer().nullable)

        val All: List<SyncField<*>> = listOf(SetId, Values, Level)
    }
}

/**
 * One entry of a card's review log, as synced: what happened ([kind]) to the card in one [direction] and
 * the schedule that resulted. Entries are immutable: every field is create-only (sent with the create,
 * rejected when an update changes it); an undo deletes the entry. The card's schedule in a direction is
 * the entry with the greatest `reviewedAt` (ties by id). [grade] is 1…4 for `rate` and `null` otherwise.
 */
@Serializable
data class NetworkMemorizerReview(
    val id: String,
    val cardId: String,
    val createdAt: RinneTimestamp,
    val updatedAt: RinneTimestamp,
    val direction: String,
    val kind: String,
    val reviewedAt: RinneTimestamp,
    val due: RinneTimestamp,
    val state: String,
    val grade: Int? = null,
    val durationMs: Long = 0,
    val stability: Double = 0.0,
    val difficulty: Double = 0.0,
    val step: Int = 0,
    val lapses: Int = 0,
    val reps: Int = 0,
) {
    companion object {
        const val SyncType = "memorizer_review"
    }

    /** The fields of a create; all create-only. */
    object Fields {
        val CardId = SyncField("cardId", String.serializer())
        val Direction = SyncField("direction", String.serializer())
        val Kind = SyncField("kind", String.serializer())
        val ReviewedAt = SyncField("reviewedAt", RinneTimestamp.serializer())
        val Due = SyncField("due", RinneTimestamp.serializer())
        val State = SyncField("state", String.serializer())
        val Grade = SyncField("grade", Int.serializer().nullable)
        val DurationMs = SyncField("durationMs", Long.serializer())
        val Stability = SyncField("stability", Double.serializer())
        val Difficulty = SyncField("difficulty", Double.serializer())
        val Step = SyncField("step", Int.serializer())
        val Lapses = SyncField("lapses", Int.serializer())
        val Reps = SyncField("reps", Int.serializer())

        val All: List<SyncField<*>> =
            listOf(CardId, Direction, Kind, ReviewedAt, Due, State, Grade, DurationMs, Stability, Difficulty, Step, Lapses, Reps)
    }

    object Directions {
        const val Forward = "forward"
        const val Reverse = "reverse"

        val All: List<String> = listOf(Forward, Reverse)
    }

    object Kinds {
        const val Rate = "rate"
        const val Postpone = "postpone"
        const val Reset = "reset"
        const val Skip = "skip"

        val All: List<String> = listOf(Rate, Postpone, Reset, Skip)
    }

    object States {
        const val New = "new"
        const val Learning = "learning"
        const val Review = "review"
        const val Relearning = "relearning"

        val All: List<String> = listOf(New, Learning, Review, Relearning)
    }
}

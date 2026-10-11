package com.rinne.libraries.error.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.rinne.libraries.error.core.RinneError
import com.rinne.libraries.error.core.unwrapCommon
import com.rinne.libraries.logger.core.RootRinneLogger
import com.rinne.libraries.logger.core.extensions.w
import kotlin.reflect.KClass

/** What every Compose error view shows. Built in composition, so it follows the current locale. */
@Immutable
data class RinneErrorUi(
    val title: String,
    val message: String,
    val details: String? = null,
    /** `null`: the view's default icon. */
    val icon: ImageVector? = null,
)

/** Turns one error type into its texts. Registered once per app in [RinneErrorPresenters]. */
interface RinneErrorPresenter<in E : RinneError> {
    @Composable
    fun present(error: E): RinneErrorUi
}

/**
 * The app's presenters, one per error type. [present] unwraps a feature's `Common` variant first,
 * then picks the presenter whose type the error is an instance of; an unregistered type gets
 * [fallback] (and a warning in the log).
 */
@Immutable
class RinneErrorPresenters internal constructor(
    private val presenters: List<Registration<*>>,
    private val fallback: RinneErrorPresenter<RinneError>,
) {
    @PublishedApi
    internal class Registration<E : RinneError>(val type: KClass<E>, val presenter: RinneErrorPresenter<E>)

    /** Whether an error of [type] has a registered presenter (used by the registration tests). */
    fun isRegistered(type: KClass<out RinneError>): Boolean = presenters.any { it.type == type }

    @Composable
    fun present(error: RinneError): RinneErrorUi {
        val unwrapped = error.unwrapCommon()
        val registration = presenters.firstOrNull { it.type.isInstance(unwrapped) }
        return when (registration) {
            null -> {
                RootRinneLogger.default.w("No error presenter for ${unwrapped::class.simpleName}")
                fallback.present(unwrapped)
            }
            else -> registration.presentUnchecked(unwrapped)
        }
    }

    @Composable
    private fun <E : RinneError> Registration<E>.presentUnchecked(error: RinneError): RinneErrorUi {
        @Suppress("UNCHECKED_CAST")
        return presenter.present(error as E)
    }

    class Builder internal constructor() {
        @PublishedApi
        internal val registrations = mutableListOf<Registration<*>>()

        inline fun <reified E : RinneError> register(presenter: RinneErrorPresenter<E>) {
            registrations += Registration(E::class, presenter)
        }
    }
}

/** Builds the app's registry: `rinneErrorPresenters(fallback) { register(…) }`. */
fun rinneErrorPresenters(
    fallback: RinneErrorPresenter<RinneError>,
    block: RinneErrorPresenters.Builder.() -> Unit,
): RinneErrorPresenters {
    val builder = RinneErrorPresenters.Builder().apply(block)
    return RinneErrorPresenters(builder.registrations.toList(), fallback)
}

val LocalRinneErrorPresenters = staticCompositionLocalOf<RinneErrorPresenters> {
    error("No RinneErrorPresenters provided: register them once in the app's root composable")
}

/** The texts of [error] from the app's registry. */
@Composable
fun presentError(error: RinneError): RinneErrorUi = LocalRinneErrorPresenters.current.present(error)

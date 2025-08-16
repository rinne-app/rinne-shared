package com.rinne.libraries.reader.markdown.deprecated

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.Markdown
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.MarkdownSuccess
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.components.MarkdownComponents
import com.rinne.libraries.reader.markdown.deprecated.markdown.compose.components.markdownComponents
import com.rinne.libraries.reader.markdown.deprecated.elements.MarkdownCheckBox
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.ImageTransformer
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownAnimations
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownAnnotator
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownColors
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownDimens
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownExtendedSpans
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownInlineContent
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownPadding
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownState
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.MarkdownTypography
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.NoOpImageTransformerImpl
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.ReferenceLinkHandler
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.ReferenceLinkHandlerImpl
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.State
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.markdownAnimations
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.markdownAnnotator
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.markdownDimens
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.markdownExtendedSpans
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.markdownInlineContent
import com.rinne.libraries.reader.markdown.deprecated.markdown.model.markdownPadding
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser


/**
 * Renders the markdown content using Material 3 styles.
 *
 * @param content The markdown content to be rendered.
 * @param colors The [MarkdownColors] to use for styling.
 * @param typography The [MarkdownTypography] to use for text styles.
 * @param modifier The [Modifier] to apply to the component.
 * @param padding The [MarkdownPadding] to use for padding.
 * @param dimens The [MarkdownDimens] to use for dimensions.
 * @param flavour The [MarkdownFlavourDescriptor] to use for parsing.
 * @param parser The [MarkdownParser] to use for parsing.
 * @param imageTransformer The [ImageTransformer] to use for transforming images.
 * @param annotator The [MarkdownAnnotator] to use for annotating links.
 * @param extendedSpans The [MarkdownExtendedSpans] to use for extended spans.
 * @param inlineContent The [MarkdownInlineContent] to use for inline content.
 * @param components The [MarkdownComponents] to use for custom components.
 * @param animations The [MarkdownAnimations] to use for animations.
 * @param referenceLinkHandler The reference link handler to be used for handling links.
 * @param loading Composable function to display while loading.
 * @param success A composable function to be displayed with the markdown content. It receives the modifier, state and components as parameters. By default this is a [Column].
 * @param error Composable function to display on error.
 */
@Composable
fun Markdown(
    content: String,
    colors: MarkdownColors = markdownColor(),
    typography: MarkdownTypography = markdownTypography(),
    modifier: Modifier = Modifier.fillMaxSize(),
    padding: MarkdownPadding = markdownPadding(),
    dimens: MarkdownDimens = markdownDimens(),
    flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor(),
    parser: MarkdownParser = MarkdownParser(flavour),
    imageTransformer: ImageTransformer = NoOpImageTransformerImpl(),
    annotator: MarkdownAnnotator = markdownAnnotator(),
    extendedSpans: MarkdownExtendedSpans = markdownExtendedSpans(),
    inlineContent: MarkdownInlineContent = markdownInlineContent(),
    components: MarkdownComponents = markdownComponents(checkbox = { MarkdownCheckBox(it.content, it.node, it.typography.text) }),
    animations: MarkdownAnimations = markdownAnimations(),
    referenceLinkHandler: ReferenceLinkHandler = ReferenceLinkHandlerImpl(),
    loading: @Composable (modifier: Modifier) -> Unit = { Box(modifier) },
    success: @Composable (state: State.Success, components: MarkdownComponents, modifier: Modifier) -> Unit = { state, components, modifier ->
        MarkdownSuccess(state = state, components = components, modifier = modifier)
    },
    error: @Composable (modifier: Modifier) -> Unit = { Box(modifier) },
) = Markdown(
    content = content,
    colors = colors,
    typography = typography,
    modifier = modifier,
    padding = padding,
    dimens = dimens,
    flavour = flavour,
    parser = parser,
    imageTransformer = imageTransformer,
    annotator = annotator,
    extendedSpans = extendedSpans,
    inlineContent = inlineContent,
    components = components,
    animations = animations,
    referenceLinkHandler = referenceLinkHandler,
    loading = loading,
    success = success,
    error = error,
)

/**
 * Renders the markdown content using Material 3 styles.
 *
 * @param markdownState The [MarkdownState] to use for parsing.
 * @param colors The [MarkdownColors] to use for styling.
 * @param typography The [MarkdownTypography] to use for text styles.
 * @param modifier The [Modifier] to apply to the component.
 * @param padding The [MarkdownPadding] to use for padding.
 * @param dimens The [MarkdownDimens] to use for dimensions.
 * @param imageTransformer The [ImageTransformer] to use for transforming images.
 * @param annotator The [MarkdownAnnotator] to use for annotating links.
 * @param extendedSpans The [MarkdownExtendedSpans] to use for extended spans.
 * @param inlineContent The [MarkdownInlineContent] to use for inline content.
 * @param components The [MarkdownComponents] to use for custom components.
 * @param animations The [MarkdownAnimations] to use for animations.
 * @param loading Composable function to display while loading.
 * @param success A composable function to be displayed with the markdown content. It receives the modifier, state and components as parameters. By default this is a [Column].
 * @param error Composable function to display on error.
 */
@Composable
fun Markdown(
    markdownState: MarkdownState,
    colors: MarkdownColors = markdownColor(),
    typography: MarkdownTypography = markdownTypography(),
    modifier: Modifier = Modifier.fillMaxSize(),
    padding: MarkdownPadding = markdownPadding(),
    dimens: MarkdownDimens = markdownDimens(),
    imageTransformer: ImageTransformer = NoOpImageTransformerImpl(),
    annotator: MarkdownAnnotator = markdownAnnotator(),
    extendedSpans: MarkdownExtendedSpans = markdownExtendedSpans(),
    inlineContent: MarkdownInlineContent = markdownInlineContent(),
    components: MarkdownComponents = markdownComponents(checkbox = { MarkdownCheckBox(it.content, it.node, it.typography.text) }),
    animations: MarkdownAnimations = markdownAnimations(),
    loading: @Composable (modifier: Modifier) -> Unit = { Box(modifier) },
    success: @Composable (state: State.Success, components: MarkdownComponents, modifier: Modifier) -> Unit = { state, components, modifier ->
        MarkdownSuccess(state = state, components = components, modifier = modifier)
    },
    error: @Composable (modifier: Modifier) -> Unit = { Box(modifier) },
) = Markdown(
    markdownState = markdownState,
    colors = colors,
    typography = typography,
    modifier = modifier,
    padding = padding,
    dimens = dimens,
    imageTransformer = imageTransformer,
    annotator = annotator,
    extendedSpans = extendedSpans,
    inlineContent = inlineContent,
    components = components,
    animations = animations,
    loading = loading,
    success = success,
    error = error,
)

/**
 * Renders the markdown content using Material 3 styles.
 *
 * @param state The [State] to use for parsing.
 * @param colors The [MarkdownColors] to use for styling.
 * @param typography The [MarkdownTypography] to use for text styles.
 * @param modifier The [Modifier] to apply to the component.
 * @param padding The [MarkdownPadding] to use for padding.
 * @param dimens The [MarkdownDimens] to use for dimensions.
 * @param imageTransformer The [ImageTransformer] to use for transforming images.
 * @param annotator The [MarkdownAnnotator] to use for annotating links.
 * @param extendedSpans The [MarkdownExtendedSpans] to use for extended spans.
 * @param inlineContent The [MarkdownInlineContent] to use for inline content.
 * @param components The [MarkdownComponents] to use for custom components.
 * @param animations The [MarkdownAnimations] to use for animations.
 * @param loading Composable function to display while loading.
 * @param success A composable function to be displayed with the markdown content. It receives the modifier, state and components as parameters. By default this is a [Column].
 * @param error Composable function to display on error.
 */
@Composable
fun Markdown(
    state: State,
    colors: MarkdownColors = markdownColor(),
    typography: MarkdownTypography = markdownTypography(),
    modifier: Modifier = Modifier.fillMaxSize(),
    padding: MarkdownPadding = markdownPadding(),
    dimens: MarkdownDimens = markdownDimens(),
    imageTransformer: ImageTransformer = NoOpImageTransformerImpl(),
    annotator: MarkdownAnnotator = markdownAnnotator(),
    extendedSpans: MarkdownExtendedSpans = markdownExtendedSpans(),
    inlineContent: MarkdownInlineContent = markdownInlineContent(),
    components: MarkdownComponents = markdownComponents(checkbox = { MarkdownCheckBox(it.content, it.node, it.typography.text) }),
    animations: MarkdownAnimations = markdownAnimations(),
    loading: @Composable (modifier: Modifier) -> Unit = { Box(modifier) },
    success: @Composable (state: State.Success, components: MarkdownComponents, modifier: Modifier) -> Unit = { state, components, modifier ->
        MarkdownSuccess(state = state, components = components, modifier = modifier)
    },
    error: @Composable (modifier: Modifier) -> Unit = { Box(modifier) },
) = Markdown(
    state = state,
    colors = colors,
    typography = typography,
    modifier = modifier,
    padding = padding,
    dimens = dimens,
    imageTransformer = imageTransformer,
    annotator = annotator,
    extendedSpans = extendedSpans,
    inlineContent = inlineContent,
    components = components,
    animations = animations,
    loading = loading,
    success = success,
    error = error,
)
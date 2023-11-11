package dev.inmo.krontab.predictor.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLElement

private val buildersCache = mutableMapOf<String, ElementBuilder<HTMLElement>>()

@Composable
fun RawElement(
    tag: String,
    attrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    content: (@Composable ElementScope<HTMLElement>.() -> Unit)? = null
) = TagElement(
    buildersCache.getOrPut(tag) {
        ElementBuilder.createBuilder(tag)
    },
    attrs,
    content
)
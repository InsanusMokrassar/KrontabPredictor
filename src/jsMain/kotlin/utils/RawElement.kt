package dev.inmo.krontab.predictor.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLElement

private val buildersCache = mutableMapOf<String, ElementBuilder<out HTMLElement>>()

abstract external class MaterialElement : HTMLElement

@Composable
fun MaterialElement(
    tag: String,
    attrs: (AttrsScope<MaterialElement>.() -> Unit)? = null,
    content: (@Composable ElementScope<MaterialElement>.() -> Unit)? = null
) = TagElement(
    buildersCache.getOrPut(tag) {
        ElementBuilder.createBuilder(tag)
    } as ElementBuilder<MaterialElement>,
    attrs,
    content
)
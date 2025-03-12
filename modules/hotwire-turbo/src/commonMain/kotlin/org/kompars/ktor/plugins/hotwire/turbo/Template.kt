package org.kompars.ktor.plugins.hotwire.turbo

import kotlin.contracts.*
import kotlinx.html.*

// TODO: remove after fix of https://github.com/Kotlin/kotlinx.html/issues/293

@Suppress("LEAKED_IN_PLACE_LAMBDA", "WRONG_INVOCATION_KIND")
@HtmlTagMarker
@OptIn(ExperimentalContracts::class)
public inline fun FlowContent.template(classes: String? = null, crossinline block: TEMPLATE.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    TEMPLATE(attributesMapOf("class", classes), consumer).visit(block)
}

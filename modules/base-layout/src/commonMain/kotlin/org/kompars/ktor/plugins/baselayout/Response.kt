package org.kompars.ktor.plugins.baselayout

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.utils.io.charsets.*
import kotlinx.html.*
import kotlinx.html.stream.*

public suspend fun ApplicationCall.respondBaseLayout(
    layout: String? = null,
    status: HttpStatusCode = HttpStatusCode.OK,
    block: FlowContent.() -> Unit,
) {
    val layoutBuilder = attributes[BaseLayoutRegistry].getValue(layout)

    val html = buildString {
        appendLine("<!DOCTYPE html>")
        appendHTML().html { layoutBuilder(this@respondBaseLayout, block) }
    }

    respondText(
        text = html,
        status = status,
        contentType = ContentType.Text.Html.withCharset(Charsets.UTF_8),
    )
}

package org.kompars.ktor.plugins.hotwire.turbo

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.sse.*
import io.ktor.utils.io.charsets.*
import kotlinx.html.*
import kotlinx.html.stream.*

private const val TURBO_STREAM_CONTENT_TYPE: String = "text/vnd.turbo-stream.html"

public val ContentType.Text.TurboStream: ContentType by lazy {
    ContentType.parse(TURBO_STREAM_CONTENT_TYPE)
}

public val ApplicationRequest.isTurboStream: Boolean
    get() {
        return acceptItems().any { it.value == TURBO_STREAM_CONTENT_TYPE }
    }

public suspend fun ApplicationCall.respondTurboStream(
    status: HttpStatusCode? = null,
    block: TurboStreamBuilder.() -> Unit,
) {
    respondText(
        contentType = ContentType.Text.TurboStream.withCharset(Charsets.UTF_8),
        status = status,
        text = buildTurboStream(block),
    )
}

public suspend fun ServerSSESession.sendTurboStream(
    id: String? = null,
    block: TurboStreamBuilder.() -> Unit,
) {
    send(id = id, data = buildTurboStream(block))
}

public class TurboStream(
    consumer: TagConsumer<*>,
    initialAttributes: Map<String, String> = emptyMap(),
) : CommonAttributeGroupFacadeFlowPhrasingSectioningContent, HTMLTag(
    tagName = "turbo-stream",
    consumer = consumer,
    inlineTag = false,
    emptyTag = false,
    initialAttributes = initialAttributes,
)

public var TurboStream.action: String
    get() = stringAttribute[this, "action"]
    set(action) {
        stringAttribute[this, "action"] = action
    }

public var TurboStream.target: String
    get() = stringAttribute[this, "target"]
    set(target) {
        stringAttribute[this, "target"] = target
    }

public var TurboStream.targets: String
    get() = stringAttribute[this, "targets"]
    set(targets) {
        stringAttribute[this, "targets"] = targets
    }

public var A.turboStream: Boolean
    get() = booleanAttribute[this, "data-turbo-stream"]
    set(turboStream) {
        booleanAttribute[this, "data-turbo-stream"] = turboStream
    }

public inline fun FlowContent.turboStream(
    action: String? = null,
    targets: String? = null,
    target: String? = null,
    crossinline block: TurboStream.() -> Unit = {},
) {
    return TurboStream(consumer, attributesMapOf("action", action, "targets", targets, "target", target)).visit(block)
}

public class TurboStreamSource(
    consumer: TagConsumer<*>,
    initialAttributes: Map<String, String> = emptyMap(),
) : CommonAttributeGroupFacadeFlowSectioningContent, HTMLTag(
    tagName = "turbo-stream-source",
    consumer = consumer,
    inlineTag = false,
    emptyTag = true,
    initialAttributes = initialAttributes,
)

public var TurboStreamSource.src: String
    get() = stringAttribute[this, "src"]
    set(src) {
        stringAttribute[this, "src"] = src
    }

public inline fun FlowContent.turboStreamSource(
    src: String? = null,
    crossinline block: TurboStreamSource.() -> Unit = {},
) {
    return TurboStreamSource(consumer, attributesMapOf("src", src)).visit(block)
}

public class TurboStreamBuilder internal constructor(private val prettyPrint: Boolean = true) {
    private val builder = StringBuilder()

    public fun append(targets: String, block: TEMPLATE.() -> Unit) {
        custom("append", targets) { template(null, block) }
    }

    public fun prepend(targets: String, block: TEMPLATE.() -> Unit) {
        custom("prepend", targets) { template(null, block) }
    }

    public fun replace(targets: String, block: TEMPLATE.() -> Unit) {
        custom("replace", targets) { template(null, block) }
    }

    public fun update(targets: String, block: TEMPLATE.() -> Unit) {
        custom("update", targets) { template(null, block) }
    }

    public fun remove(targets: String) {
        custom("remove", targets)
    }

    public fun before(targets: String, block: TEMPLATE.() -> Unit) {
        custom("before", targets) { template(null, block) }
    }

    public fun after(targets: String, block: TEMPLATE.() -> Unit) {
        custom("after", targets) { template(null, block) }
    }

    public fun refresh() {
        custom("refresh")
    }

    public fun custom(action: String, targets: String? = null, block: TurboStream.() -> Unit = {}) {
        val consumer = builder.appendHTML(prettyPrint)
        val attributes = attributesMapOf("action", action, "targets", targets)

        TurboStream(consumer, attributes).visitAndFinalize(consumer, block)
    }

    override fun toString(): String = builder.toString()
}

public fun buildTurboStream(block: TurboStreamBuilder.() -> Unit): String {
    return TurboStreamBuilder().apply(block).toString()
}

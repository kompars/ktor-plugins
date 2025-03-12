package org.kompars.ktor.plugins.hotwire.turbo

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.utils.io.charsets.*
import kotlinx.html.*
import kotlinx.html.attributes.*
import kotlinx.html.stream.*

private val turboActionAttribute = EnumAttribute(TurboAction.entries.associateBy { it.realValue })
private val turboFrameLoadingAttribute = EnumAttribute(TurboFrameLoading.entries.associateBy { it.realValue })
private val turboFrameRefreshAttribute = EnumAttribute(TurboFrameRefresh.entries.associateBy { it.realValue })
private val turboFrameAutoScrollBlockAttribute = EnumAttribute(TurboFrameAutoScrollBlock.entries.associateBy { it.realValue })
private val turboFrameAutoScrollBehaviorAttribute = EnumAttribute(TurboFrameAutoScrollBehavior.entries.associateBy { it.realValue })

private const val TURBO_FRAME_HEADER_NAME: String = "Turbo-Frame"

public val ApplicationRequest.turboFrame: String?
    get() {
        return header(TURBO_FRAME_HEADER_NAME)
    }

public suspend fun ApplicationCall.respondTurboFrame(
    id: String? = null,
    status: HttpStatusCode? = null,
    block: TurboFrame.() -> Unit,
) {
    respondText(
        contentType = ContentType.Text.Html.withCharset(Charsets.UTF_8),
        status = status,
        text = buildString {
            appendHTML().turboFrame(id = id, block)
        }
    )
}

@Suppress("EnumEntryName")
public enum class TurboAction(override val realValue: String) : AttributeEnum {
    replace("replace"),
    advance("advance"),
}

@Suppress("EnumEntryName")
public enum class TurboFrameLoading(override val realValue: String) : AttributeEnum {
    eager("eager"),
    lazy("lazy"),
}

@Suppress("EnumEntryName")
public enum class TurboFrameRefresh(override val realValue: String) : AttributeEnum {
    replace("replace"),
    morph("morph"),
}

@Suppress("EnumEntryName")
public enum class TurboFrameAutoScrollBlock(override val realValue: String) : AttributeEnum {
    end("end"),
    start("start"),
    center("center"),
    nearest("nearest"),
}

@Suppress("EnumEntryName")
public enum class TurboFrameAutoScrollBehavior(override val realValue: String) : AttributeEnum {
    auto("auto"),
    smooth("smooth"),
}

public class TurboFrame(
    consumer: TagConsumer<*>,
    initialAttributes: Map<String, String> = emptyMap(),
) : CommonAttributeGroupFacadeFlowSectioningContent, HTMLTag(
    tagName = "turbo-frame",
    consumer = consumer,
    inlineTag = false,
    emptyTag = false,
    initialAttributes = initialAttributes,
)

public var TurboFrame.id: String
    get() = stringAttribute[this, "id"]
    set(id) {
        stringAttribute[this, "id"] = id
    }

public var TurboFrame.target: String
    get() = stringAttribute[this, "target"]
    set(target) {
        stringAttribute[this, "target"] = target
    }

public var TurboFrame.src: String
    get() = stringAttribute[this, "src"]
    set(src) {
        stringAttribute[this, "src"] = src
    }

public var TurboFrame.loading: TurboFrameLoading
    get() = turboFrameLoadingAttribute[this, "loading"]
    set(loading) {
        turboFrameLoadingAttribute[this, "loading"] = loading
    }

public var TurboFrame.disabled: Boolean
    get() = tickerAttribute[this, "disabled"]
    set(disabled) {
        tickerAttribute[this, "disabled"] = disabled
    }

public var TurboFrame.refresh: TurboFrameRefresh
    get() = turboFrameRefreshAttribute[this, "refresh"]
    set(refresh) {
        turboFrameRefreshAttribute[this, "refresh"] = refresh
    }

public var TurboFrame.turboAction: TurboAction
    get() = turboActionAttribute[this, "data-turbo-action"]
    set(turboAction) {
        turboActionAttribute[this, "data-turbo-action"] = turboAction
    }

public var TurboFrame.autoScroll: Boolean
    get() = booleanAttribute[this, "autoscroll"]
    set(autoScroll) {
        booleanAttribute[this, "autoscroll"] = autoScroll
    }

public var TurboFrame.autoScrollBlock: TurboFrameAutoScrollBlock
    get() = turboFrameAutoScrollBlockAttribute[this, "data-autoscroll-block"]
    set(autoScrollBlock) {
        turboFrameAutoScrollBlockAttribute[this, "data-autoscroll-block"] = autoScrollBlock
    }

public var TurboFrame.autoScrollBehavior: TurboFrameAutoScrollBehavior
    get() = turboFrameAutoScrollBehaviorAttribute[this, "data-autoscroll-behavior"]
    set(autoScrollBehavior) {
        turboFrameAutoScrollBehaviorAttribute[this, "data-autoscroll-behavior"] = autoScrollBehavior
    }

public inline fun FlowContent.turboFrame(id: String? = null, crossinline block: TurboFrame.() -> Unit) {
    TurboFrame(consumer, attributesMapOf("id", id)).visit(block)
}

public inline fun TagConsumer<*>.turboFrame(id: String? = null, crossinline block: TurboFrame.() -> Unit) {
    TurboFrame(this, attributesMapOf("id", id)).visitAndFinalize(this, block)
}

public var A.turboFrame: String
    get() = stringAttribute[this, "data-turbo-frame"]
    set(turboFrame) {
        stringAttribute[this, "data-turbo-frame"] = turboFrame
    }

public var BUTTON.turboFrame: String
    get() = stringAttribute[this, "data-turbo-frame"]
    set(turboFrame) {
        stringAttribute[this, "data-turbo-frame"] = turboFrame
    }

public var A.turboAction: TurboAction
    get() = turboActionAttribute[this, "data-turbo-action"]
    set(turboAction) {
        turboActionAttribute[this, "data-turbo-action"] = turboAction
    }

package org.kompars.ktor.plugins.baselayout

import io.ktor.server.application.*
import io.ktor.server.application.hooks.*
import io.ktor.util.*
import kotlinx.html.*

private typealias BaseLayoutBuilder = HTML.(ApplicationCall, FlowContent.() -> Unit) -> Unit

public class BaseLayoutConfig internal constructor() {
    internal val registry = mutableMapOf<String?, BaseLayoutBuilder>()

    public fun layout(name: String? = null, builder: BaseLayoutBuilder) {
        registry[name] = builder
    }
}

internal val BaseLayoutRegistry = AttributeKey<Map<String?, BaseLayoutBuilder>>("BaseLayoutRegistry")

public val BaseLayout: RouteScopedPlugin<BaseLayoutConfig> = createRouteScopedPlugin("BaseLayout", ::BaseLayoutConfig) {
    val registry = pluginConfig.registry.toMap()

    on(CallSetup) { call ->
        call.attributes.put(BaseLayoutRegistry, registry)
    }
}

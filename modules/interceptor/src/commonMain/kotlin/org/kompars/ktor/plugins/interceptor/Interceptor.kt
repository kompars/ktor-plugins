package org.kompars.ktor.plugins.interceptor

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

public class InterceptorContext internal constructor(
    public val call: RoutingPipelineCall,
)

public class InterceptorConfig internal constructor() {
    internal var interceptor: suspend InterceptorContext.() -> Unit = {}

    public fun intercept(block: suspend InterceptorContext.() -> Unit) {
        interceptor = block
    }
}

public val Interceptor: RouteScopedPlugin<InterceptorConfig> = createRouteScopedPlugin("Interceptor", ::InterceptorConfig) {
    on(AuthenticationChecked) { call ->
        if (!call.isHandled) {
            pluginConfig.interceptor.invoke(InterceptorContext(call as RoutingPipelineCall))
        }
    }
}

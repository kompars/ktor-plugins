package org.kompars.ktor.plugins.interceptor

import io.ktor.server.routing.*
import io.ktor.utils.io.*

@KtorDsl
public fun Route.intercept(block: suspend InterceptorContext.() -> Unit) {
    install(Interceptor) {
        this.intercept(block)
    }
}

@KtorDsl
public fun Route.route(build: Route.() -> Unit): Route {
    return createChild(TransparentRouteSelector).apply { build() }
}

internal object TransparentRouteSelector : RouteSelector() {
    override suspend fun evaluate(context: RoutingResolveContext, segmentIndex: Int): RouteSelectorEvaluation {
        return RouteSelectorEvaluation.Transparent
    }

    override fun toString(): String {
        return "(transparent)"
    }
}

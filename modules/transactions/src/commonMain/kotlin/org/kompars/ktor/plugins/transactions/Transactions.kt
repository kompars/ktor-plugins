package org.kompars.ktor.plugins.transactions

import io.ktor.server.application.*
import io.ktor.util.pipeline.*

public typealias PipelineProceed = suspend () -> Unit
public typealias CallWrapper = suspend CallWrapperContext.(PipelineProceed) -> Unit
public typealias SkipPredicate = (ApplicationCall) -> Boolean

public class CallWrapperContext internal constructor(
    public val call: ApplicationCall,
)

public class TransactionsConfig internal constructor() {
    internal var skipPredicates: MutableList<SkipPredicate> = mutableListOf()
    internal var callWrapper: CallWrapper = { it() }

    public fun skipWhen(predicate: SkipPredicate) {
        skipPredicates += predicate
    }

    public fun wrap(block: CallWrapper) {
        callWrapper = block
    }
}

public val Transactions: RouteScopedPlugin<TransactionsConfig> = createRouteScopedPlugin("Transactions", ::TransactionsConfig) {
    val skipPredicates = pluginConfig.skipPredicates
    val callWrapper = pluginConfig.callWrapper

    on(WrapCall) { call, proceed ->
        if (skipPredicates.any { it(call) }) {
            proceed()
        } else {
            callWrapper(CallWrapperContext(call), proceed)
        }
    }
}

private object WrapCall : Hook<suspend (ApplicationCall, PipelineProceed) -> Unit> {
    private val phase: PipelinePhase = PipelinePhase("WrapCall")

    override fun install(pipeline: ApplicationCallPipeline, handler: suspend (ApplicationCall, PipelineProceed) -> Unit) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Setup, phase)
        pipeline.intercept(phase) {
            handler(call) { proceed() }
        }
    }
}

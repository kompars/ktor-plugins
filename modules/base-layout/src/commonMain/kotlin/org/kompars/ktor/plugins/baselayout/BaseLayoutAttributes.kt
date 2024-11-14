package org.kompars.ktor.plugins.baselayout

import io.ktor.server.application.*
import io.ktor.util.*
import kotlin.reflect.*

internal val BaseLayoutAttributesAttribute = AttributeKey<MutableMap<String, Any?>>("BaseLayoutAttributes")

public class BaseLayoutAttributeDelegate<T> internal constructor() {
    public operator fun getValue(thisRef: ApplicationCall, property: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        return thisRef.layoutAttributes[property.name] as T
    }

    public operator fun setValue(thisRef: ApplicationCall, property: KProperty<*>, value: T) {
        thisRef.layoutAttributes[property.name] = value
    }
}

public val ApplicationCall.layoutAttributes: MutableMap<String, Any?>
    get() = attributes.computeIfAbsent(BaseLayoutAttributesAttribute, ::mutableMapOf)

public fun <T> layoutAttribute(): BaseLayoutAttributeDelegate<T> {
    return BaseLayoutAttributeDelegate()
}

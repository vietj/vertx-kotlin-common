package io.vertx.lang.kotlin

import io.vertx.core.buffer.*
import io.vertx.core.json.*
import io.vertx.core.streams.*

fun JsonObject(vararg fields: Pair<String, Any?>): JsonObject = JsonObject(linkedMapOf(*fields))
fun JsonArray(vararg values: Any?): JsonArray = io.vertx.core.json.JsonArray(arrayListOf(*values))

object Json

fun Json.obj(vararg fields: Pair<String, Any?>): JsonObject = JsonObject(*fields)
fun Json.obj(fields: Iterable<Pair<String, Any?>>): JsonObject = JsonObject(*fields.toList().toTypedArray())
fun Json.obj(fields: Map<String, Any?>): JsonObject = JsonObject(fields)
fun Json.obj(block: JsonObject.() -> Unit): JsonObject = JsonObject().apply(block)

fun Json.array(vararg values: Any?): JsonArray = JsonArray(*values)
fun Json.array(values: Iterable<Any?>): JsonArray = JsonArray(*values.toList().toTypedArray())
fun Json.array(value: JsonObject): JsonArray = JsonArray(value)
fun Json.array(value: JsonArray): JsonArray = JsonArray(value)
fun Json.array(values: List<Any?>): JsonArray = io.vertx.core.json.JsonArray(values)
fun Json.array(block: JsonArray.() -> Unit): JsonArray = JsonArray().apply(block)

inline fun <T> json(block: Json.() -> T): T = Json.block()

@Suppress("UNCHECKED_CAST") operator fun <T> JsonObject.get(key: String): T = getValue(key) as T
@Suppress("UNCHECKED_CAST") operator fun <T> JsonArray.get(index: Int): T = getValue(index) as T

inline fun <S : WriteStream<Buffer>> S.writeJson(pretty: Boolean = false, block: Json.() -> Any): S {
    write(Buffer.buffer().appendJson(pretty, block))
    return this
}

inline fun Buffer.appendJson(pretty: Boolean = false, block: Json.() -> Any): Buffer =
        block(Json).let { json ->
            if (pretty)
                io.vertx.core.json.Json.encodePrettily(json)
            else
                io.vertx.core.json.Json.encode(json)
        }.let { encoded -> appendString(encoded) }

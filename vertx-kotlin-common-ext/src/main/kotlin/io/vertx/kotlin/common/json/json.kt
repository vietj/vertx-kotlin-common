package io.vertx.kotlin.common.json

import io.vertx.core.buffer.*
import io.vertx.core.json.*
import io.vertx.core.streams.*

object Json

// JsonObject creation

fun JsonObject(vararg fields: Pair<String, Any?>): JsonObject = JsonObject(linkedMapOf(*fields))
fun Json.obj(vararg fields: Pair<String, Any?>): JsonObject = JsonObject(*fields)
fun Json.obj(fields: Iterable<Pair<String, Any?>>): JsonObject = JsonObject(*fields.toList().toTypedArray())
fun Json.obj(fields: Map<String, Any?>): JsonObject = JsonObject(fields)
fun Json.obj(block: JsonObject.() -> Unit): JsonObject = JsonObject().apply(block)

// JsonArray creation

fun JsonArray(vararg values: Any?): JsonArray = io.vertx.core.json.JsonArray(arrayListOf(*values))
fun Json.array(vararg values: Any?): JsonArray = JsonArray(*values)
fun Json.array(values: Iterable<Any?>): JsonArray = JsonArray(*values.toList().toTypedArray())
fun Json.array(value: JsonObject): JsonArray = JsonArray(value)
fun Json.array(value: JsonArray): JsonArray = JsonArray(value)
fun Json.array(values: List<Any?>): JsonArray = io.vertx.core.json.JsonArray(values)
fun Json.array(block: JsonArray.() -> Unit): JsonArray = JsonArray().apply(block)

inline fun <T> json(block: Json.() -> T): T = Json.block()

/**
 * The postscript operator for [JsonObject].
 */
@Suppress("UNCHECKED_CAST") operator fun <T> JsonObject.get(key: String): T = getValue(key) as T

/**
 * The postscript operator for [JsonArray].
 */
@Suppress("UNCHECKED_CAST") operator fun <T> JsonArray.get(index: Int): T = getValue(index) as T

/**
 * Encode an object to json and write it to the write stream.
 *
 * @receiver a Vert.x [WriteStream] of [Buffer]
 * @param pretty wether or not to pretty format the json output
 * @param block the json producing block to use
 * @return a reference to this, so the API can be used fluently
 */
inline fun <S : WriteStream<Buffer>> S.write(pretty: Boolean = false, block: Json.() -> Any): S {
  write(Buffer.buffer().appendJson(pretty, block))
  return this
}

/**
 * End the stream with an object encoded to json.
 *
 * @receiver a Vert.x [WriteStream] of [Buffer]
 * @param pretty wether or not to pretty format the json output
 * @param block the json producing block to use
 * @return a reference to this, so the API can be used fluently
 */
inline fun <S : WriteStream<Buffer>> S.end(pretty: Boolean = false, block: Json.() -> Any): S {
  end(Buffer.buffer().appendJson(pretty, block))
  return this
}

/**
 * Appends the specified json {@code block} to the end of this Buffer. The buffer will expand as necessary to accommodate
 * any bytes written.<p>
 *
 * @receiver a Vert.x [Buffer]
 * @param pretty wether or not to pretty format the json output
 * @param block the json producing block to write
 * @return a reference to this, so the API can be used fluently
 */
inline fun Buffer.appendJson(pretty: Boolean = false, block: Json.() -> Any): Buffer =
    block(Json).let { json ->
      if (pretty)
        io.vertx.core.json.Json.encodePrettily(json)
      else
        io.vertx.core.json.Json.encode(json)
    }.let { encoded -> appendString(encoded) }

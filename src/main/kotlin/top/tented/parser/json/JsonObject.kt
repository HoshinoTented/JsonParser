package top.tented.parser.json

import top.tented.parser.json.exception.JsonTypeException

class JsonObject(map : MutableMap<String, Any?>) : MutableMap<String, Any?> by map {
    inline fun <reified T> checkGet(key : String) : T =
        get(key)?.let { value ->
            value as? T
        } ?: throw JsonTypeException("Have not key of $key(${T::class})")

    fun getString(key : String) : String = checkGet(key)
    fun getNumber(key : String) : Long = checkGet(key)
    fun getJsonObject(key : String) : JsonObject = checkGet(key)
    fun getJsonArray(key : String) : JsonArray = checkGet(key)

    override fun toString() = buildString {
        append("{")
        forEach { key, value ->
            append(key)     //添加键
            append(':')     //添加分隔符
            append(value)   //添加值

            append(',')     //添加逗号
        }
    }.let { it.substring(0, it.length - 1) } + "}"
}
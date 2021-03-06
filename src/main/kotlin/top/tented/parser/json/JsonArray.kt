package top.tented.parser.json

import top.tented.parser.json.exception.JsonTypeException

class JsonArray(list : MutableList<Any?>) : MutableList<Any?> by list {
    inline fun <reified T> checkGetOrNull(index : Int) : T? =
            getOrNull(index)?.let { value ->
                value as? T
            }

    inline fun <reified T> checkGet(index : Int) : T =
            checkGetOrNull<T>(index) ?: throw JsonTypeException("Have not value of index: $index type: ${T::class}")

    //They generated by replace
    fun getString(index : Int) : String = checkGet(index)
    fun getNumber(index : Int) : Number = checkGet(index)
    fun getJsonObject(index : Int) : JsonObject = checkGet(index)
    fun getJsonArray(index : Int) : JsonArray = checkGet(index)
    fun getStringOrNull(index : Int) : String? = checkGetOrNull(index)
    fun getNumberOrNull(index : Int) : Number? = checkGetOrNull(index)
    fun getJsonObjectOrNull(index : Int) : JsonObject? = checkGetOrNull(index)
    fun getJsonArrayOrNull(index : Int) : JsonArray? = checkGetOrNull(index)

    override fun toString() : String = joinToString(prefix = "[", postfix = "]") { it.toString() }
}
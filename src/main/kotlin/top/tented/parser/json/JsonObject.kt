package top.tented.parser.json

class JsonObject(map : MutableMap<String, Any?>) : MutableMap<String, Any?> by map {
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
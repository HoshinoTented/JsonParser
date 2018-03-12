package top.tented.parser.json

class JsonArray(list : MutableList<Any?>) : MutableList<Any?> by list {
    override fun toString() : String = joinToString(prefix = "[", postfix = "]") { it.toString() }
}
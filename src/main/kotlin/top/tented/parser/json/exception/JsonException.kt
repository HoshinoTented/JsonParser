package top.tented.parser.json.exception

open class JsonException(source : String, index : Int, message : String) : Exception("index $index: $message")
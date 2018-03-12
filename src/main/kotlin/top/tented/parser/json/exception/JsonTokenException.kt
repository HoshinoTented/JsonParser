package top.tented.parser.json.exception

import top.tented.parser.json.JsonToken

open class JsonTokenException(index : Int, token : JsonToken, message : String) : Exception("index: $index $token: $message")
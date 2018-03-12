package top.tented.parser.json.exception

import top.tented.parser.json.JsonToken

class JsonUnexpectedTokenException(index : Int, token : JsonToken) : JsonTokenException(index, token, "Unexpected token $token")
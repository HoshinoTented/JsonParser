package top.tented.parser.json.exception

class JsonUnexpectedException(source : String, index : Int, char : Char) : JsonException(source, index, "Unexpected character $char")
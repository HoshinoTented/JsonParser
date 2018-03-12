package top.tented.parser.json

class JsonToken(val token : TokenType, val text : String = token.text) {
	enum class TokenType(val text : kotlin.String) {
            String("\""),
            Integer("0123456789"),
            Split(":"),
            PreFlower("{"),      //花括号(雾
            PostFlower("}"),
            PreSquare("["),
            PostSquare("]"),
            Comma(",")
    }

    override fun toString() : String = "$token($text)"
}
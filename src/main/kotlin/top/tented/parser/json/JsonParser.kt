package top.tented.parser.json

import top.tented.parser.json.exception.JsonTokenException
import top.tented.parser.json.exception.JsonUnexpectedTokenException

class JsonParser(val tokens : List<JsonToken>) {

    var currentIndex = 0
    val currentToken get() = tokens[currentIndex]
    val nextToken get() = tokens[++ currentIndex]
    val siblingToken get() = tokens[currentIndex + 1]

    val valueToken =
            arrayOf(
                    JsonToken.TokenType.String,
                    JsonToken.TokenType.Integer
            )

    /**
     * 重置index
     */
    fun resetIndex() {
        currentIndex = 0
    }

    /**
     * 转换JsonObject
     */
    fun parseObject() : JsonObject = HashMap<String, Any?>().apply {
        fun parsePair() {
            if (currentToken.token == JsonToken.TokenType.String) {
                val key = currentToken.text
                //然后下一个就是冒号啦
                if (nextToken.token == JsonToken.TokenType.Split) {
                    when (nextToken.token) {
                        JsonToken.TokenType.PreFlower -> this[key] = parseObject()
                        JsonToken.TokenType.PreSquare -> this[key] = parseArray()
                        in valueToken -> this[key] = currentToken.text.toIntOrNull() ?: currentToken.text

                        else -> throw JsonTokenException(currentIndex, currentToken, "Value of Pair can not be $currentToken")
                    }
                } else throw JsonTokenException(currentIndex, currentToken, "Should be ${JsonToken.TokenType.Split}")
            } else throw JsonTokenException(currentIndex, currentToken, "Key of Pair should be ${JsonToken.TokenType.String}")
        }
        //开头第一个token, 肯定是花括号啦
        if (currentToken.token == JsonToken.TokenType.PreFlower) {
            while (true) {
                when (nextToken.token) {
                    JsonToken.TokenType.String -> parsePair()       //如果是字符串, 就去Pair
                    JsonToken.TokenType.PostFlower -> return@apply  //如果是花括号...再见~

                    else -> throw JsonUnexpectedTokenException(currentIndex, currentToken)
                }

                //到这里呢, 应该是Pair的ValueToken
                //那就得判断下一个Token是什么了。。。
                when (nextToken.token) {
                    JsonToken.TokenType.Comma -> Unit           //逗号(分隔符)
                    JsonToken.TokenType.PostFlower -> return@apply      //花括号(结束

                    else -> throw JsonUnexpectedTokenException(currentIndex, currentToken)
                }
            }
        } else throw JsonTokenException(currentIndex, currentToken, "The first token must be ${JsonToken.TokenType.PreFlower}")
    }.let(::JsonObject)

    /**
     * 转换JsonArray
     */
    fun parseArray() : JsonArray = ArrayList<Any?>().apply {
        //判断第一个Token是不是方块
        if (currentToken.token == JsonToken.TokenType.PreSquare) {
            while (true) {
                //然后根据下一个token的类型搞一些奇奇怪怪乱七八糟的操作
                when (nextToken.token) {
                    JsonToken.TokenType.PreFlower -> add(parseObject())
                    JsonToken.TokenType.PreSquare -> add(parseArray())
                    in valueToken -> add(currentToken.text)

                    else -> throw JsonUnexpectedTokenException(currentIndex, currentToken)
                }

                //这个时候就要判断是`,`还是`]`了呢。。。
                when (nextToken.token) {
                    JsonToken.TokenType.Comma -> Unit       //逗号emmmmm
                    JsonToken.TokenType.PostSquare -> return@apply      //花括号emmmmmmmmmmmmm

                    else -> throw JsonUnexpectedTokenException(currentIndex, currentToken)
                }
            }
        } else throw JsonTokenException(currentIndex, currentToken, "The first token muse be ${JsonToken.TokenType.PreSquare}")
    }.let(::JsonArray)
}
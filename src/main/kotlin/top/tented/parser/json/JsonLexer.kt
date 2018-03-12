package top.tented.parser.json

import org.intellij.lang.annotations.Language
import top.tented.parser.json.exception.JsonException
import top.tented.parser.json.exception.JsonUnexpectedException

typealias TokenType = JsonToken.TokenType

/**
 * 所有Json类型函数执行完毕之后
 * 都要保证currentChar是这个类型的最后一个字符
 */
class JsonLexer(@Language("JSON") val source : String) {
    companion object {
        val integer = "0123456789"
        val float = "$integer."
    }

    var currentIndex = 0
    val currentChar get() = source[currentIndex]
    val Char.isClean get() = this <= ' '
    val tokens = ArrayList<JsonToken>()

    init {
        if (currentChar.isClean) nextNotClean()
        parse()
    }

    fun parse() {
        when (currentChar) {
            '{' -> jsonObject()
            '[' -> jsonArray()

            else -> throw JsonException(source, currentIndex, "Json should starts with '{' or '[")
        }
    }

    fun notCleanNext() = if (currentChar.isClean) nextNotClean() else currentChar

    /**
     * 进位, 忽略空白符(isClean)
     */
    fun nextNotClean() : Char {
        next()
        while (currentChar.isClean) {
            currentIndex ++
        }

        return currentChar
    }

    /**
     * 进位
     */
    fun next() : Char =
            source[++ currentIndex]

    fun last() : Char =
            source[currentIndex - 1]

    fun revert() : Char =
            source[-- currentIndex]

    fun jsonObject() {
        if (currentChar == '{') {
            tokens.add(JsonToken(TokenType.PreFlower))
            next()
            while (true) {
                when (notCleanNext()) {        //现在是左花括号之后的第一个非空白字符
                    '}' -> {
                        tokens.add(JsonToken(TokenType.PostFlower))
                        return
                    }

                    '"' -> pair()       //如果是字符串开头(引号), 就处理 对

                    else ->
                        if (currentIndex == source.length)
                            throw JsonException(source, currentIndex, "Json must ends with '}")
                        else
                            throw JsonUnexpectedException(source, currentIndex, currentChar)
                }

                //处理完, 现在只剩两种可能了:
                //}字符
                //"或其他不合法的字符
            }
        } else throw JsonException(source, currentIndex, "Internal Error")
    }

    fun jsonArray() {
        if (currentChar == '[') {
            //此时应该是方括号
            tokens.add(JsonToken(TokenType.PreSquare))

            while (currentChar != ']') {     //开始循环。。
                nextNotClean()
                when (currentChar) {
                    '{' -> jsonObject()             //同样的, 根据开头判断类型, 这里是JsonObject
                    '"' -> string()                 //字符串
                    '[' -> jsonArray()              //数组
                    in integer -> integer()              //数字

                    else -> throw JsonUnexpectedException(source, currentIndex, currentChar)
                }

                //不出意外, 这里应该是上面类型的结尾了, 开始找下一个非空字符
                when (nextNotClean()) {
                    //如果是逗号(分隔符
                    ',' -> tokens.add(JsonToken(TokenType.Comma))
                    ']' -> Unit              //右方括号(数组结束符

                    else -> throw JsonUnexpectedException(source, currentIndex, currentChar)
                }
            }

            //这里应该是`]`字符, 要加token了哦
            tokens.add(JsonToken(TokenType.PostSquare))
        } else throw JsonException(source, currentIndex, "Internal Error")
    }

    fun string() {
        if (currentChar == '"') {
            val strValue = StringBuilder("\"")

            //此时是字符串后的第一个字符
            while (next() != '\"') {
                strValue.append(currentChar)
                when (currentChar) {        //判断字符串内字符
                    '\\' -> {
                        next()      //进位
                    }
                }
            }

            tokens.add(JsonToken(TokenType.String, strValue.append('"').toString()))
        } else throw JsonException(source, currentIndex, "Internal Error")
    }

    fun integer() {
        if (currentChar in integer) {
            //此时应该是数字的开头
            val strValue = StringBuilder()      //数字的字符串形式

            while (currentChar in float) {
                strValue.append(currentChar)
                next()
            }

            if (revert() == '.') {        //看看是不是.结尾
                throw JsonException(source, currentIndex, "Number can not ends with comma")
            }

            //这个时候是数字结尾, 要添加Token了
            tokens.add(JsonToken(TokenType.Integer, strValue.toString()))
        } else throw JsonException(source, currentIndex, "Internal Error")
    }

    fun pair() {
        if (currentChar == '"') {
            //先获取`键`
            string()
            //不出差错的话, 这里应该是键的结尾(引号)
            if (nextNotClean() == ':') {      //所以找到下一个非空字符, 根据语法应该是 : (分割符
                tokens.add(JsonToken(TokenType.Split))
                //然后再找到下一个非空字符
                when (nextNotClean()) {     //根据开头推断类型
                    '"' -> string()                //字符串类型。。。
                    '[' -> jsonArray()             //JsonArray类型。。。
                    '{' -> jsonObject()             //JsonObject类型(忘记加了
                    in integer -> integer()        //数字类型

                    else -> throw JsonException(source, currentIndex, "Value of Pair can not starts with $currentChar")
                }

                //好啦, 值也处理完了, 接下来就是判断。。是`}`呢, 还是`,`呢
                when (nextNotClean()) {
                    ',' -> nextNotClean()       //忽略逗号
                    '}' -> Unit         //啥都不干

                    else -> throw JsonUnexpectedException(source, currentIndex, currentChar)
                }
            } else throw JsonUnexpectedException(source, currentIndex, currentChar)
        } else throw JsonException(source, currentIndex, "Internal Error")
    }
}
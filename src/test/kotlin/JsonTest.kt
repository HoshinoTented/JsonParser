import org.junit.Test
import top.tented.parser.json.JsonLexer
import top.tented.parser.json.JsonParser

class JsonTest {
    @Test
    fun lexerTest() {
        JsonLexer(
                """{"a" : {"b" : [{"c": 1}, 2]}}"""
        ).tokens
                .let(::JsonParser)
                .parseObject()
                .let(::println)
    }
}
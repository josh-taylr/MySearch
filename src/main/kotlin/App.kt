import java.io.File
import java.util.*
import kotlin.collections.HashMap

fun main(args: Array<String>) {
    if (args.contains("--grammar")) {
        Parse(GrammarIndex()).parse(File("/Users/Josh/Documents/wsj.xml"))
        return
    }

    val indexListener: (Dictionary) -> Unit = { dictionary: Dictionary ->
        dictionary.search(*args).forEach { println(it) }
    }

    indexListener.let { Parse(InvertFileIndex(it)).parse(File("/Users/Josh/Documents/wsj.xml")) }
}

private class GrammarIndex : Index {

    val grammar = HashMap<String, MutableSet<String>>()
    val tags = Stack<String>()

    override fun beginIndexing() {
        tags.push("Root")
    }

    override fun endIndexing() {
        println(grammar)

        grammar.clear()
        tags.removeAllElements()
    }

    override fun startTag(tag: String) {
        val children = grammar.getOrPut(tags.peek()) { mutableSetOf() }
        children.add(tag)
        tags.push(tag)
    }

    override fun endTag(tag: String) {
        tags.pop()
    }

    override fun word(term: String) = Unit

}
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.*
import kotlin.collections.HashMap

fun main(args: Array<String>) {
    if (args.contains("--grammar")) {
        Parse(GrammarIndex()).parse(File("/Users/Josh/Documents/wsj.xml"))
        return
    }

    val dictionaryFileStream = DictionaryFileStream()
    val dictionaryFile = File("out/index/dictionary.dat")
    var dictionary: Dictionary? = null

    if (args.contains("--build") || !dictionaryFile.exists()) {
        dictionaryFile.delete()
        dictionaryFile.outputStream().buffered().use { stream: BufferedOutputStream ->
            println("Build index...")
            Parse(InvertFileIndex({ result: Dictionary ->
                dictionary = result
                dictionaryFileStream.write(result, stream)
            }), documentCount = Int.MAX_VALUE).parse(File("/Users/Josh/Documents/wsj.xml"))
        }
    }

    if (null == dictionary) {
        dictionaryFile.inputStream().buffered().use { stream: BufferedInputStream ->
            dictionary = dictionaryFileStream.read(stream)
        }
    }

    dictionary!!.search(*args.filterNot { it.contains('-') }.toTypedArray()).forEach { println(it) }
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
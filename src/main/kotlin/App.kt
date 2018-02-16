import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    if (args.contains("--grammar")) {
        Parse(GrammarIndex()).parse(File("/Users/Josh/Documents/wsj.xml"))
        return
    }

    val dictionaryFile = File("out/index/dictionary.dat")
    var dictionary: Dictionary? = null

    if (args.contains("--build") || !dictionaryFile.exists()) {
        dictionaryFile.delete()
        dictionaryFile.outputStream().buffered().use { stream: BufferedOutputStream ->
            println("Build index...")
            Parse(InvertFileIndex({ result: Dictionary ->
                dictionary = result
                VirtualPostingsWriter().write(result, stream)
            })).parse(File("/Users/Josh/Documents/wsj.xml"))
        }
    }

    val postingsFileReader = PostingsFileReader(dictionaryFile)

    measureTimeMillis {
        dictionaryFile.inputStream().buffered().use { stream: BufferedInputStream ->
            if (null == dictionary) {
                dictionary = VirtualPostingsReader(postingsFileReader).read(stream)
            }
        }
    }.let { println("Dictionary loaded in $it milliseconds.") }

    measureTimeMillis {
        dictionary!!.search(*args.filterNot { it.contains('-') }.toTypedArray()).forEach { println(it) }
    }.let { println("Search complete in $it milliseconds.") }

    postingsFileReader.close()
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
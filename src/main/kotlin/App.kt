import java.io.BufferedOutputStream
import java.io.File
import java.io.RandomAccessFile
import java.util.*
import kotlin.collections.HashMap

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
            }), documentCount = Int.MAX_VALUE).parse(File("/Users/Josh/Documents/wsj.xml"))
        }
    }

    val accessFile = RandomAccessFile(dictionaryFile, "r")
    if (null == dictionary) {
        dictionary = VirtualPostingsReader(accessFile).read()
    }

    dictionary!!.search(*args.filterNot { it.contains('-') }.toTypedArray()).forEach { println(it) }

    accessFile.close()
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
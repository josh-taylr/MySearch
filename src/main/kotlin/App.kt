import java.io.BufferedOutputStream
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    if (args.contains("--grammar")) {
        Parse(GrammarIndex()).parse(File(WSJ_FILE))
        return
    }

    val dictionaryFile = File(INDEX_DIR + "/dictionary.dat")

    if (args.contains("--build") || !dictionaryFile.exists()) {
        dictionaryFile.delete()
        dictionaryFile.outputStream().buffered().use { stream: BufferedOutputStream ->
            println("Build index...")
            Parse(InvertFileIndex({ result: Dictionary ->
                DictionaryFileWriter().write(result, stream)
            })).parse(File(WSJ_FILE))
        }
    }

    val dictionary : ISAMSDictionaryFile = run {
        var value: ISAMSDictionaryFile? = null
        measureTimeMillis {
            value = ISAMSDictionaryFile(dictionaryFile, DictionaryFileReader())
        }.let { println("Index read in $it milliseconds.") }
        return@run value!!
    }

    measureTimeMillis {
        args.filterNot { it.contains('-') }
                .mapNotNull { dictionary[it] }
                .reduce {acc, postings -> acc.intersect(postings) }
                .forEach(::println)
    }.let { println("Search complete in $it milliseconds.") }
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
import dictionary.*
import index.InvertFileIndex
import parse.Parse
import rank.TFIDF
import java.io.File
import java.time.Duration
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val dictionaryFile = File("$INDEX_DIR/dictionary.dat")
    val wsjCollection = File(WSJ_FILE)

    if (args.contains("--build") || !dictionaryFile.exists()) {
        dictionaryFile.delete()
        File(INDEX_DIR).mkdirs()
        println("Build index...")
        measureTimeMillis {
            Parse(InvertFileIndex({ result: Dictionary ->
                FileDictionaryWriter().write(result, dictionaryFile)
            })).parse(wsjCollection)
        }.let {
            val duration = Duration.ofMillis(it)
            println("Index built in ${duration.seconds} seconds.")
        }
    }

    val dictionary : FileDictionary = run {
        var value: FileDictionary? = null
        measureTimeMillis {
            value = FileDictionary(dictionaryFile, FileDictionaryReader())
        }.let { println("Index read in $it milliseconds.") }
        return@run value!!
    }

    var documentLengths: Map<DocumentNumber, Int> = emptyMap()
    measureTimeMillis {
        documentLengths = mutableMapOf<DocumentNumber, Int>().apply {
            val hiddenPostings = dictionary["@length"] ?: error("Hidden '@length' posting isn't present in dictionary.")
            hiddenPostings.forEach {
                put(it.documentNumber, it.termFrequency)
            }
        }
    }.let {
        val duration = Duration.ofMillis(it)
        println("Document lengths loaded in ${duration.seconds} seconds.")
    }

    measureTimeMillis {
        TFIDF.rank(dictionary, documentLengths, args.filterNot { '-' in it }.map(String::toLowerCase))
                .take(10)
                .forEach(::println)
    }.let { println("Search complete in $it milliseconds.") }
}
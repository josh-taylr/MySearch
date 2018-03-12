import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val dictionaryFile = File("$INDEX_DIR/dictionary.dat")
    val wsjCollection = File(WSJ_FILE)

    if (args.contains("--build") || !dictionaryFile.exists()) {
        dictionaryFile.delete()
        File(INDEX_DIR).mkdirs()
        println("Build index...")
        Parse(InvertFileIndex({ result: Dictionary ->
            DictionaryFileWriter().write(result, dictionaryFile)
        })).parse(wsjCollection)
    }

    val dictionary : ISAMSDictionaryFile = run {
        var value: ISAMSDictionaryFile? = null
        measureTimeMillis {
            value = ISAMSDictionaryFile(dictionaryFile, DictionaryFileReader())
        }.let { println("Index read in $it milliseconds.") }
        return@run value!!
    }

    val documentLengths: Map<DocumentNumber, Int> = mutableMapOf<DocumentNumber, Int>().apply {
        val hiddenPostings = dictionary["@length"] ?: error("Hidden '@length' posting isn't present in dictionary.")
        hiddenPostings.forEach {
            put(it.documentNumber, it.termFrequency)
        }
    }

    measureTimeMillis {
        TFIDF.rank(dictionary, documentLengths, args.filterNot { '-' in it }.map(String::toLowerCase))
                .forEach(::println)

    }.let { println("Search complete in $it milliseconds.") }
}
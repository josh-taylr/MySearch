import java.io.BufferedOutputStream
import java.io.File
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val dictionaryFile = File(INDEX_DIR + "/dictionary.dat")
    val wsjCollection = File(WSJ_FILE)

    if (args.contains("--build") || !dictionaryFile.exists()) {
        dictionaryFile.delete()
        dictionaryFile.outputStream().buffered().use { stream: BufferedOutputStream ->
            println("Build index...")
            Parse(InvertFileIndex({ result: Dictionary ->
                DictionaryFileWriter().write(result, stream)
            })).parse(wsjCollection)
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
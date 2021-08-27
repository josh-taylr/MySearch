import dictionary.*
import index.InvertFileIndex
import kotlinx.coroutines.*
import parse.Parse
import rank.TFIDF
import java.io.File
import java.time.Duration
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val dicDirectory = File(INDEX_DIR)
    val wsjShards = List(ShardCount) { File(WSJ_SHARDS, "shard-$it.xml") }

    val indexerScope = CoroutineScope(Job() + Dispatchers.IO + CoroutineName("indexer parent"))
    val searchScope = CoroutineScope(Job() + Dispatchers.IO + CoroutineName("search parent"))

    if (args.contains("--build")) {
        File(INDEX_DIR).mkdirs()
        println("Build index...")
        measureTimeMillis {
            runBlocking {
                val jobs = wsjShards.mapIndexed { i, file ->
                    indexerScope.launch {
                        Parse(InvertFileIndex({ result: Dictionary ->
                            FileDictionaryWriter().write(result, File(dicDirectory, "shard-$i.dat"))
                        })).parse(file)
                    }
                }
                jobs.joinAll()
            }
        }.let {
            val duration = Duration.ofMillis(it)
            println("Index built in ${duration.seconds} seconds.")
        }
    }

    val dictionary : FileDictionary = run {
        var value: FileDictionary? = null
        measureTimeMillis {
            value = FileDictionary(File(dicDirectory, "shard-0.dat"), FileDictionaryReader())
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
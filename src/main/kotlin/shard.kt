import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.regex.Pattern.DOTALL
import kotlin.math.roundToInt
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
fun main() {

    val file = File(WSJ_FILE)
    val shardDirectory = File("/Users/josh/Downloads/WSJ")

    var documentCount = 0

    println("Starting...")

    shardDirectory.mkdirs()

    val timeToReadRegex = measureTime {
        val scanner = Scanner(File(WSJ_FILE))
        val pattern = "<DOC>.*?</DOC>".toPattern(DOTALL)
        val windowLength = (file.length() / ShardCount.toDouble()).roundToInt()

        var nextMatch: String? = ""
        repeat(ShardCount) {
            var byteCount = 0
            BufferedOutputStream(FileOutputStream(File(shardDirectory, "shard-$it.xml"))).use { out ->
                while (nextMatch != null && byteCount < windowLength) {
                    nextMatch = scanner.findWithinHorizon(pattern, 0)
                    nextMatch?.let { nextMatch ->
                        val bytes = nextMatch.toByteArray()
                        out.write(bytes)
                        documentCount++
                        byteCount += bytes.count()
                    }
                }
            }
        }

        scanner.close()
    }

    println("It took $timeToReadRegex to shard $documentCount from the wsj archive.")
}
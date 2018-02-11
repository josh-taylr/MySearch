import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class DictionaryFileStream {

    fun write(dictionary: Dictionary, stream: OutputStream) {
        DataOutputStream(stream).run {
            for ((term, postings) in dictionary) {
                // bytes in this term
                write(term.length)
                // byte array of the term
                writeBytes(term)
                // number of posting to read
                writeInt(postings.count())
                // sequence of longs
                postings.forEach { writeLong(documentNumberToLong(it)) }
            }
        }
    }

    fun read(stream: InputStream): Dictionary {
        val map: TreeMap<String, Dictionary.Postings> = TreeMap()
        DataInputStream(stream).run {
            while (true) {
                // read number of bytes to read the term
                val length = read()
                if (EOF == length) break
                // read the term
                val term = ByteArray(length).also(::readFully).let { String(it) }
                // read number of postings
                val count = readInt()
                // read sequence of longs
                val postings = Dictionary.Postings()
                repeat(count) { postings.add(longToDocumentNumber(readLong())) }

                map[term] = postings
            }
        }
        return Dictionary(map)
    }

    private fun longToDocumentNumber(value: Long): String {
        val result = Companion.numberPattern.find(value.toString()) ?: throw IllegalStateException()
        return result.destructured.let { (start, end) -> "WSJ$start-$end" }
    }

    private fun documentNumberToLong(posting: String): Long {
        val result = Companion.documentPattern.find(posting) ?: throw IllegalStateException()
        return result.destructured.let { (start, second) -> start + second }.toLong()
    }

    companion object {
        private val numberPattern = Regex("(\\p{Digit}{6})(\\p{Digit}{4})")
        private val documentPattern = Regex("WSJ(\\p{Digit}{6})-(\\p{Digit}{4})")
        const val EOF = -1
    }
}
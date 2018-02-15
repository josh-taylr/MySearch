import java.io.DataInputStream
import java.io.InputStream
import java.util.*

class DictionaryStreamReader : DictionaryReader {

    override fun read(stream: InputStream): Dictionary {
        val map: TreeMap<String, InMemoryPostings> = TreeMap()
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
                val postings = InMemoryPostings()
                repeat(count) {
                    val value = readLong()
                    val documentNumber = DocumentNumber(value)
                    postings.add(documentNumber)
                }
                map[term] = postings
            }
        }
        return Dictionary(map)
    }

    companion object {
        private const val EOF = -1
    }
}
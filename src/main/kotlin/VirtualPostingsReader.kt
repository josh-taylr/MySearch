import java.io.DataInputStream
import java.io.InputStream
import java.util.*

class VirtualPostingsReader(private val reader: PostingsFileReader) : DictionaryReader {

    override fun read(stream: InputStream): Dictionary {
        val map: TreeMap<String, VirtualPostings> = TreeMap()
        DataInputStream(stream).run {
            // read dictionary offset and move file pointer
            val dictionaryOffset = readLong()
            skip(dictionaryOffset)
            // read the sequence of terms with the location and length of their postings
            while (true) {
                val length = read() // number of bytes to read this term
                if (EOF == length) break
                val term = ByteArray(length).also(::readFully).let { String(it) }
                val postingsLocation = readLong()
                val postingsCount = readInt()
                map[term] = VirtualPostings(reader, postingsLocation, postingsCount)
            }
        }
        return MapDictionary(map)
    }

    companion object {
        private const val EOF = -1
    }
}
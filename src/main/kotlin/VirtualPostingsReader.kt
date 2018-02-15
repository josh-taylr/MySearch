import java.io.RandomAccessFile
import java.util.*

class VirtualPostingsReader(private val file: RandomAccessFile) {

    fun read(): Dictionary {
        val map: TreeMap<String, VirtualPostings> = TreeMap()
        file.run {
            // read dictionary offset and move file pointer
            val dictionaryOffset = readLong()
            seek(dictionaryOffset)
            // read the sequence of terms with the location and length of their postings
            while (true) {
                val length = read() // number of bytes to read this term
                if (EOF == length) break
                val term = ByteArray(length).also(::readFully).let { String(it) }
                val postingsLocation = readLong()
                val postingsCount = readInt()
                map[term] = VirtualPostings(file, postingsLocation, postingsCount)
            }
        }
        return Dictionary(map)
    }

    companion object {
        private const val EOF = -1
    }
}
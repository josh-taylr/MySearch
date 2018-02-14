import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class VirtualPostingsWriter : DictionaryWriter {

    override fun write(dictionary: Dictionary, stream: OutputStream) {
        DataOutputStream(stream).run {
            // write dictionary offset
            val totalPostings = dictionary.sumBy { (_, postings) -> postings.count() }
            writeLong(totalPostings * 8L) // Long = 8 bytes
            // write the sequence of postings to disk
            dictionary.forEach { (_, postings) ->
                postings.forEach {
                    writeLong(it.value)
                }
            }
            // write the sequence of terms with the locations of their postings. Dictionary offset + sum of lengths of written postings
            var postingLoc = 8L // postings start after 8 byte offset
            for ((term, postings) in dictionary) {
                // bytes in this term
                write(term.length)
                // byte array of the term
                writeBytes(term)
                // postings location
                writeLong(postingLoc)
                // postings count
                val count = postings.count()
                writeInt(count)
                // update the offset
                postingLoc += count * 8L // Long = 8 bytes
            }
        }
    }

    override fun read(stream: InputStream): Dictionary {
        val map: TreeMap<String, Dictionary.Postings> = TreeMap()
        DataInputStream(stream).run {
            // read dictionary offset and move file pointer
            val dictionaryOffset = readLong()
            stream.skip(dictionaryOffset)
            // read the sequence of terms with the location and length of their postings
            while (true) {
                val length = read() // number of bytes to read this term
                if (EOF == length) break
                val term = ByteArray(length).also(::readFully).let { String(it) }
                val postingsLocation = readLong()
                val postingsCount = readInt()
                map[term] = VirtualPostings(postingsLocation, postingsCount)
            }
        }
        return Dictionary(map)
    }

    companion object {
        private const val EOF = -1
    }
}
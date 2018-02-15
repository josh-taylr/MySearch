import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class DictionaryFileStream : DictionaryWriter {

    override fun write(dictionary: Dictionary, stream: OutputStream) {
        DataOutputStream(stream).run {
            for ((term, postings) in dictionary) {
                // bytes in this term
                write(term.length)
                // byte array of the term
                writeBytes(term)
                // number of posting to read
                writeInt(postings.count())
                // sequence of longs
                postings.forEach { writeLong(it.value) }
            }
        }
    }

    override fun read(stream: InputStream): Dictionary {
        val map: TreeMap<String, Postings> = TreeMap()
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
                val postings = Postings()
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
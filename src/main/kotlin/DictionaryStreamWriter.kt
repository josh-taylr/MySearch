import java.io.DataOutputStream
import java.io.OutputStream

class DictionaryStreamWriter : DictionaryWriter {

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
}
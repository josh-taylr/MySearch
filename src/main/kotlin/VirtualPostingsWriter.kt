import java.io.DataOutputStream
import java.io.OutputStream

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
}
import java.io.DataOutputStream
import java.io.OutputStream

class VirtualPostingsWriter : DictionaryWriter {

    override fun write(dictionary: Dictionary, stream: OutputStream) {
        DataOutputStream(stream).run {
            // write dictionary offset
            writeLong((dictionary.postingsCount() * (LONG_SIZE / BYTE_SIZE)).toLong())
            // write the sequence of postings to disk
            dictionary.forEach { (_, postings) ->
                writePostings(postings)
            }
            // write the sequence of terms with the locations of their postings. Dictionary offset + sum of lengths of written postings
            var postingLoc = 8L // postings start after 8 byte offset
            for ((term, postings) in dictionary) {
                val count = postings.count()
                writeTerm(term)
                // postings location
                writeLong(postingLoc)
                // postings count
                writeInt(count)
                // update the offset
                postingLoc += count * 8L // Long = 8 bytes
            }
        }
    }
}
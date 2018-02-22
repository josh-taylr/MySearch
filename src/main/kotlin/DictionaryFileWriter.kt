import java.io.DataOutputStream
import java.io.OutputStream

class DictionaryFileWriter(private val blockSize: Int = 1000) : DictionaryWriter {

    private val termInfoSize = (BYTE_SIZE + LONG_SIZE + LONG_SIZE) / BYTE_SIZE

    override fun write(dictionary: Dictionary, stream: OutputStream) {
        DataOutputStream(stream).run {
            val postingsSize = dictionary.postingsCount() * (LONG_SIZE / BYTE_SIZE)
            val termsSize = dictionary.termsSize() + dictionary.count() * termInfoSize
            // write terms index = sum of postings count multiplied size of long + sum of terms lengths with a byte, int, and, long
            writeLong(8L + postingsSize + termsSize)
            // write the sequence of postings
            dictionary.forEach { (_, postings) ->
                writePostings(postings)
            }
            // write terms with posting location and count
            val nodeData = ArrayList<TermData>()
            var postingLoc = 8L // postings start after 8 byte offset
            var nodeLocation = 8L + postingsSize
            var nodeSize = 0L
            var nodeTerm: String? = null
            dictionary.forEachIndexed { index, (term, postings) ->
                if (nodeTerm == null) {
                    nodeTerm = term
                }
                val postingSize = postings.count() * 8L
                writeTerm(term)
                // postings location
                writeLong(postingLoc)
                // postings count
                writeLong(postingSize)
                // update the offset
                postingLoc += postingSize
                nodeSize += (term.length + termInfoSize).toLong()
                // build node index
                if ((index + 1) % blockSize == 0) {
                    nodeData.add(TermData(nodeTerm!!, nodeLocation, nodeSize))
                    nodeLocation += nodeSize
                    nodeSize = 0L
                    nodeTerm = null
                }
            }
            if (nodeSize > 0) {
                nodeData.add(TermData(nodeTerm!!, nodeLocation, nodeSize))
            }
            // write term nodes with term location and length
            nodeData.forEach { (term, location, size) ->
                writeTerm(term)
                writeLong(location)
                writeLong(size)
            }
        }
    }

    data class TermData(val term: String, val location: Long, val length: Long)
}
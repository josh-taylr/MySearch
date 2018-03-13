package dictionary

import java.io.DataOutputStream
import java.io.File
import java.io.RandomAccessFile

class FileDictionaryWriter(private val blockSize: Int = 1000) {

    private val termInfoSize = (BYTE_SIZE + LONG_SIZE + LONG_SIZE) / BYTE_SIZE

    fun write(dictionary: Dictionary, file: File) {
        val postingsSize = ArrayList<Long>(dictionary.size)
        val termsSize = dictionary.termsSize() + dictionary.count() * termInfoSize
        DataOutputStream(file.outputStream()).run {
            // write terms index = sum of postings count multiplied size of long + sum of terms lengths with a byte, int, and, long
            writeLong(Long.MAX_VALUE)
            // write the sequence of postings
            dictionary.forEach { (_, postings) ->
                postingsSize += writePostings(postings)
            }
            // write terms with posting location and count
            val nodeData = ArrayList<TermData>()
            var postingLoc = 8L // postings start after 8 byte offset
            var nodeLocation = 8L + postingsSize.sum()
            var nodeSize = 0L
            var nodeTerm: String? = null
            var count = 0
            dictionary.forEach { term, _ ->
                if (nodeTerm == null) {
                    nodeTerm = term
                }
                val postingSize = postingsSize[count]
                writeTerm(term)
                // postings location
                writeLong(postingLoc)
                // postings count
                writeLong(postingSize)
                // update the offset
                postingLoc += postingSize
                nodeSize += (term.length + termInfoSize).toLong()
                // build node index
                if (++count % blockSize == 0) {
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
        RandomAccessFile(file, "rw").run {
            writeLong(8L + postingsSize.sum() + termsSize)
        }
    }

    data class TermData(val term: String, val location: Long, val length: Long)
}
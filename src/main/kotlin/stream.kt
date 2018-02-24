import java.io.DataInput
import java.io.DataOutput

/**
 * Writes the given postings to this byte stream and returns the number of bytes written.
 */
fun DataOutput.writePostings(postings: Postings): Long {
    var written = 0L
    postings.forEach {
        writeLong(it.value); written += 8
    }
    return written
}

fun DataInput.readPostings(size: Long): Postings = mutablePostingsOf().apply {
    var read = 0L
    while (read < size) {
        val value = readLong(); read += 8
        add(DocumentNumber(value))
    }
}

fun DataOutput.writeTerm(term: String) {
    // bytes in this term
    write(term.length)
    // byte array of the term
    writeBytes(term)
}

fun DataInput.readTerm(): String {
    val termLength = readByte()
    return ByteArray(termLength.toInt()).also(::readFully).let { String(it) }
}
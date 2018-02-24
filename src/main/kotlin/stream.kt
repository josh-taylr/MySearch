import java.io.DataInput
import java.io.DataOutput

fun DataOutput.writePostings(postings: Postings) {
    postings.forEach {
        writeLong(it.value)
    }
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
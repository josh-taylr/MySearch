import java.io.DataInput
import java.io.DataOutput

/**
 * Writes the given postings to this byte stream and returns the number of bytes written.
 */
fun DataOutput.writePostings(postings: Postings): Long {
    var written = 0L
    postings.forEach {
        written += writePosting(it)
    }
    return written
}

fun DataOutput.writePosting(posting: Posting): Int = posting.let { (dn, tf)->
    writeLong(dn.value)
    writeInt(tf)
    return 12
}

fun DataInput.readPostings(size: Long): Postings {
    val postings = mutablePostingsOf()
    for (i in 0 until size / 12) {
        postings.add(readPosting())
    }
    return postings
}

fun DataInput.readPosting(): Posting {
    val dn = readLong()
    val tf = readInt()
    return Posting(DocumentNumber(dn), tf)
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
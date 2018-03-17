package dictionary

import encode.VariableByte
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

fun DataOutput.writeEncodedPostings(postings: Postings): Long {
    val bytes = VariableByte.encode(postings.flatMap { listOf(it.documentNumber.value, it.termFrequency.toLong()) })
    write(bytes.toByteArray())
    return bytes.size.toLong()
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

fun DataInput.readEncodedPostings(size: Long): Postings {
    val bytes = arrayListOf<Byte>()
    for (i in 0 until size) {
        bytes += readByte()
    }
    val numbers = VariableByte.decode(bytes)
    val postings = mutablePostingsOf()
    for (i in 0 until numbers.size step 2) {
        postings += Posting(DocumentNumber(numbers[i]), numbers[i+1].toInt())
    }
    return postings
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
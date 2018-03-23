package dictionary

import encode.VariableByte
import encode.diffs
import encode.expandDiffs
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
    val sorted = postings.sortedBy { it.documentNumber.value }

    val documentNumbers = sorted.map { it.documentNumber.value }
    val documentBytes = VariableByte.encode(documentNumbers.diffs())
    write(documentBytes.toByteArray())

    val termFrequencies = sorted.map { it.termFrequency.toLong() }
    val tfBytes = VariableByte.encode(termFrequencies)
    write(tfBytes.toByteArray())

    return documentBytes.size.toLong() + tfBytes.size.toLong()
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
    val documentNumbers = numbers.take(numbers.size / 2).expandDiffs()
    val termFrequencies = numbers.takeLast(numbers.size / 2)

    return MutablePostings().apply {
        addAll(documentNumbers.zip(termFrequencies) { doc, tf -> Posting(DocumentNumber(doc), tf.toInt()) })
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
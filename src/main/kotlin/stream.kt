import java.io.DataOutputStream

fun DataOutputStream.writePostings(postings: PostingsType) {
    postings.forEach {
        writeLong(it.value)
    }
}

fun DataOutputStream.writeTerm(term: String) {
    // bytes in this term
    write(term.length)
    // byte array of the term
    writeBytes(term)
}
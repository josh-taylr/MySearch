import java.io.RandomAccessFile

/**
 * @param randomAccessFile To be closed by the provider
 */
data class VirtualPostings(private val randomAccessFile: RandomAccessFile,
                           private val location: Long,
                           private val count: Int) :  Postings {

    private val postings: Set<DocumentNumber> by lazy {
        randomAccessFile.run {
            seek(location)
            val set = mutableSetOf<DocumentNumber>()
            repeat(count) {
                val number = DocumentNumber(readLong())
                set.add(number)
            }
            return@run set
        }
    }

    override fun and(other: Postings): Postings = InMemoryPostings(intersect(other).toMutableList())

    override fun or(other: Postings): Postings = InMemoryPostings(union(other).toMutableList())

    override fun iterator(): Iterator<DocumentNumber> = postings.iterator()
}
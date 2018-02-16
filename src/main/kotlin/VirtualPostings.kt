class VirtualPostings(private val postingReader: PostingsFileReader,
                      private val location: Long,
                      private val count: Int) : Postings() {

    override val postings: Set<DocumentNumber> by lazy {
        postingReader.read(location, count)
    }

    override fun and(other: Postings): Postings = InMemoryPostings(postings.intersect(other.postings) as MutableSet<DocumentNumber>)

    override fun or(other: Postings): Postings = InMemoryPostings(union(other) as MutableSet<DocumentNumber>)

    override fun iterator(): Iterator<DocumentNumber> = postings.iterator()
}
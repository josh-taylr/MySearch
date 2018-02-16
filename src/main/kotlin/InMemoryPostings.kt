class InMemoryPostings internal constructor(override val postings: MutableSet<DocumentNumber>) : Postings() {

    constructor() : this(mutableSetOf<DocumentNumber>())

    fun add(documentNumber: DocumentNumber): InMemoryPostings {
        postings.add(documentNumber)
        return this
    }

    override fun and(other: Postings): Postings = InMemoryPostings(postings.intersect(other.postings) as MutableSet<DocumentNumber>)

    override fun or(other: Postings): Postings = InMemoryPostings(postings.union(other.postings) as MutableSet<DocumentNumber>)

    override fun iterator(): Iterator<DocumentNumber> = postings.iterator()
}
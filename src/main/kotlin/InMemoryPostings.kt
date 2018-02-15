data class InMemoryPostings internal constructor(private val documents: MutableList<DocumentNumber>) : Postings {

    constructor() : this(mutableListOf<DocumentNumber>())

    fun add(documentNumber: DocumentNumber): InMemoryPostings {
        if (documentNumber != documents.lastOrNull()) {
            documents.add(documentNumber)
        }
        return this
    }

    override fun and(other: Postings): Postings = InMemoryPostings(intersect(other).toMutableList())

    override fun or(other: Postings): Postings = InMemoryPostings(union(other).toMutableList())

    override fun iterator(): Iterator<DocumentNumber> = documents.iterator()
}
class InMemoryPostings internal constructor(private val documents: MutableList<DocumentNumber>) : Postings {

    constructor() : this(mutableListOf<DocumentNumber>())

    fun add(documentNumber: DocumentNumber): InMemoryPostings {
        if (documentNumber != documents.lastOrNull()) {
            documents.add(documentNumber)
        }
        return this
    }

    override fun and(other: InMemoryPostings): InMemoryPostings = InMemoryPostings(intersect(other).toMutableList())

    override fun or(other: InMemoryPostings): InMemoryPostings = InMemoryPostings(union(other).toMutableList())

    override fun equals(other: Any?): Boolean = when(other) {
        is InMemoryPostings -> documents == other.documents
        else -> false
    }

    override fun hashCode(): Int = documents.hashCode()

    override fun toString(): String = documents.toString()

    override fun iterator(): Iterator<DocumentNumber> {
        return documents.iterator()
    }
}
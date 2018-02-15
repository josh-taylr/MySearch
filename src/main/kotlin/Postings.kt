class Postings internal constructor(private val documents: MutableList<DocumentNumber>) : Iterable<DocumentNumber> {

    constructor() : this(mutableListOf<DocumentNumber>())

    fun add(documentNumber: DocumentNumber): Postings {
        if (documentNumber != documents.lastOrNull()) {
            documents.add(documentNumber)
        }
        return this
    }

    fun and(other: Postings): Postings = Postings(intersect(other).toMutableList())

    fun or(other: Postings): Postings = Postings(union(other).toMutableList())

    override fun equals(other: Any?): Boolean = when(other) {
        is Postings -> documents == other.documents
        else -> false
    }

    override fun hashCode(): Int = documents.hashCode()

    override fun toString(): String = documents.toString()

    override fun iterator(): Iterator<DocumentNumber> {
        return documents.iterator()
    }
}
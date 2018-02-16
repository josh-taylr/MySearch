abstract class Postings : Iterable<DocumentNumber> {

    internal abstract val postings: Set<DocumentNumber>

    abstract fun and(other: Postings): Postings

    abstract fun or(other: Postings): Postings

    override fun iterator(): Iterator<DocumentNumber> = postings.iterator()

    override fun equals(other: Any?): Boolean = when (other) {
        is Postings -> postings == other.postings
        else -> false
    }

    override fun hashCode(): Int = postings.hashCode()

    override fun toString(): String = postings.toString()
}
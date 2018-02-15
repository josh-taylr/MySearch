import java.util.*

/*
    An inverted file index containing a list of terms with with corresponding postings.
 */
class Dictionary(val map: TreeMap<String, InMemoryPostings> = TreeMap()) : Iterable<Pair<String, InMemoryPostings>> {

    fun add(documentNumber: DocumentNumber, term: String): Dictionary {
        val postings = map.getOrPut(term) { InMemoryPostings() }
        postings.add(documentNumber)
        return this
    }

    fun search(vararg term: String): List<DocumentNumber> {
        return term.mapNotNull { map[it] }
                .reduce({acc, postings -> acc.and(postings)})
                .toList()
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is Dictionary -> map == other.map
        else -> false
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()

    override fun iterator(): Iterator<Pair<String, InMemoryPostings>> {
        return map.entries.map { Pair(it.key, it.value) }.iterator()
    }
}

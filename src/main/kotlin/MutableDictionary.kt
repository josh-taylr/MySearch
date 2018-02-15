import java.util.*

/*
    An inverted file index containing a list of terms with with corresponding postings.
 */
class MutableDictionary(val map: TreeMap<String, InMemoryPostings> = TreeMap()) : Dictionary(map) {

    fun add(documentNumber: DocumentNumber, term: String): MutableDictionary {
        val postings = map.getOrPut(term) { InMemoryPostings() }
        postings.add(documentNumber)
        return this
    }
}

open class Dictionary(private val map: TreeMap<String, out Postings> = TreeMap()) : Iterable<Pair<String, Postings>> {

    fun search(vararg term: String): List<DocumentNumber> {
        return term.mapNotNull { map[it] as Postings}
                .reduce({acc, postings -> acc.and(postings)})
                .toList()
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is MutableDictionary -> map == other.map
        else -> false
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()

    override fun iterator(): Iterator<Pair<String, Postings>> {
        return map.entries.map { Pair(it.key, it.value) }.iterator()
    }
}
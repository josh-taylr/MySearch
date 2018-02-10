import java.util.*

/*
    An inverted file index containing a list of terms with with corresponding postings.
 */
class Dictionary(val map: TreeMap<String, Postings> = TreeMap()) : Iterable<Pair<String, Dictionary.Postings>> {

    fun add(documentNumber: String, term: String): Dictionary {
        val postings = map.getOrPut(term) { Postings() }
        postings.add(documentNumber)
        return this
    }

    fun search(vararg term: String): List<String> {
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

    override fun iterator(): Iterator<Pair<String, Postings>> {
        return map.entries.map { Pair(it.key, it.value) }.iterator()
    }

    class Postings internal constructor(private val documents: MutableSet<String>) : Iterable<String> {

        constructor() : this(mutableSetOf())

        fun add(documentNumber: String): Postings {
            documents.add(documentNumber)
            return this
        }

        fun and(other: Postings): Postings = Postings(documents.intersect(other) as MutableSet<String>)

        fun or(other: Postings): Postings = Postings(documents.union(other) as MutableSet<String>)

        override fun equals(other: Any?): Boolean = when(other) {
            is Postings -> documents == other.documents
            else -> false
        }

        override fun hashCode(): Int = documents.hashCode()

        override fun toString(): String = documents.toString()

        override fun iterator(): Iterator<String> {
            return documents.iterator()
        }
    }
}

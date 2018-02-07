import java.util.*

/*
    An inverted file index containing a list of terms with with corresponding postings.
 */
class Dictionary : Iterable<Pair<String, Dictionary.Postings>> {

    private val map = TreeMap<String, Postings>()

    fun add(documentNumber: String, term: String): Dictionary {
        val postings = map.getOrPut(term) { Postings() }
        postings.add(documentNumber)
        return this
    }

    override fun iterator(): Iterator<Pair<String, Postings>> {
        return map.entries.map { Pair(it.key, it.value) }.iterator()
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is Dictionary -> map == other.map
        else -> false
    }

    override fun hashCode(): Int = map.hashCode()

    class Postings {
        private val documents = mutableSetOf<String>()
        fun add(documentNumber: String): Postings {
            documents.add(documentNumber)
            return this
        }

        override fun equals(other: Any?): Boolean = when(other) {
            is Postings -> documents == other.documents
            else -> false
        }

        override fun hashCode(): Int = documents.hashCode()

        override fun toString(): String {
            return documents.toString()
        }
    }
}

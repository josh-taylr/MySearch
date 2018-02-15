import java.util.*

/*
    An inverted file index containing a list of terms with with corresponding postings.
 */
class MutableDictionary(private val map: TreeMap<String, InMemoryPostings> = TreeMap()) : Dictionary(map) {

    fun add(documentNumber: DocumentNumber, term: String): MutableDictionary {
        val postings = map.getOrPut(term) { InMemoryPostings() }
        postings.add(documentNumber)
        return this
    }
}
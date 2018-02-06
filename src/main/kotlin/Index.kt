import java.util.*
import kotlin.collections.ArrayList

/*
    Build a concordance (inverted file) from terms passed between doc tags.
 */
open class Index(private val indexWriter: (Dictionary) -> Unit) {

    private val map = TreeMap<String, Postings>()
    private val tags = Stack<String>()

    private var documentNumber: String? = null

    open fun beginIndexing() {

    }

    open fun endIndexing() {
        indexWriter(Dictionary(map.entries.map { Pair(it.key, it.value) }))
        map.clear()
    }

    open fun startTag(tag: String) {
        tags.push(tag)
    }

    open fun endTag(tag: String) {
        val current = tags.pop()
        if (tag != current) throw TagMismatchException("Expected '$current' tag, but encountered '$tag'")
    }

    open fun word(term: String) {
        if ("DOCNO" == tags.peek()) {
            documentNumber = term
        } else if ("P" == tags.peek()) {
            var postings = map[term]

            // add term to dictionary when not present
            if (postings == null) {
                postings = Postings(ArrayList())
                map[term] = postings
            }

            if (null != documentNumber) {
                postings.documents.add(documentNumber!!)
            } else {
                throw IllegalStateException("Adding term from unknown document")
            }
        }
    }
}

class TagMismatchException(message: String?) : Exception(message)

data class Dictionary(private val records: Collection<Pair<String, Postings>> = emptyList()) :
        Iterable<Pair<String, Postings>> {
    private val map = TreeMap<String, Postings>()

    init {
        map.putAll(records)
    }

    override fun iterator(): Iterator<Pair<String, Postings>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class Postings(val documents: ArrayList<String>)
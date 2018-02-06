import java.util.*

/*
    Build a concordance (inverted file) from terms passed between doc tags.
 */
open class InvertFileIndex(private val indexWriter: (Dictionary) -> Unit) : Index {

    private val map = TreeMap<String, Postings>()
    private val tags = Stack<String>()

    private var documentNumber: String? = null

    override fun beginIndexing() {

    }

    override fun endIndexing() {
        indexWriter(Dictionary(map.entries.map { Pair(it.key, it.value) }))
        map.clear()
    }

    override fun startTag(tag: String) {
        tags.push(tag)
    }

    override fun endTag(tag: String) {
        tags.pop()
    }

    override fun word(term: String) {
        if ("DOCNO" == tags.peek()) {
            documentNumber = term
        } else if ("TEXT" == tags.peek()) {
            cleanTerm(term)?.let { clean: String ->
                if (null == documentNumber) throw IllegalStateException("Adding term from unknown document")

                val postings = map.getOrPut(clean) { Postings(mutableSetOf()) }
                postings.documents.add(documentNumber!!)
            }
        }
    }
}

data class Dictionary(private val records: Collection<Pair<String, Postings>> = emptyList()) :
        Iterable<Pair<String, Postings>> {
    private val map = TreeMap<String, Postings>()

    init {
        map.putAll(records)
    }

    override fun iterator(): Iterator<Pair<String, Postings>> {
        return map.entries.map { Pair(it.key, it.value) }.iterator()
    }
}

data class Postings(val documents: MutableSet<String>)
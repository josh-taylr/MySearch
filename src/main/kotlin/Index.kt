import java.util.*

/*
    Build a concordance (inverted file) from terms passed between doc tags.
 */
open class Index(val indexWriter: (Dictionary) -> Unit) {

    open fun beginIndexing() {

    }

    open fun endIndexing() {

    }

    open fun startTag(tag: String) {

    }

    open fun endTag(tag: String) {

    }

    open fun word(term: String) {

    }
}

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

data class Postings(val documents: List<String>)
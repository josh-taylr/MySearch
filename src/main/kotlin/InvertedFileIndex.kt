import java.util.*

/*
    Build a concordance (inverted file) from terms passed between doc tags.
 */
open class InvertFileIndex(private val indexWriter: (Dictionary) -> Unit) : Index {

    private val tags = Stack<String>()

    private var dictionary = mutableDictionaryOf()
    private var documentNumber: DocumentNumber? = null
    private var termCount: Int = 0

    override fun beginIndexing() {

    }

    override fun endIndexing() {
        indexWriter(dictionary)
        dictionary = mutableDictionaryOf()
    }

    override fun startTag(tag: String) {
        when (tags.push(tag)) {
            "DOC" -> termCount = 0
        }
    }


    override fun endTag(tag: String) {
        when (tags.pop()) {
            "DOC" -> dictionary.getOrPut("@length") { MutablePostings() }.add(Posting(documentNumber!!, termCount))
        }
    }

    override fun word(term: String) {
        if ("DOCNO" == tags.peek()) {
            documentNumber = DocumentNumber.parse(term)
        } else if ("TEXT" == tags.peek()) {
            termCount++
            cleanTerm(term)?.let { clean: String ->
                if (null == documentNumber) throw IllegalStateException("Adding te rm from unknown document")
                dictionary.getOrPut(clean, { MutablePostings() }).add(documentNumber!!)
            }
        }
    }
}
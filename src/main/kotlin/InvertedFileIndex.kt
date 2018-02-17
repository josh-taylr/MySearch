import java.util.*

/*
    Build a concordance (inverted file) from terms passed between doc tags.
 */
open class InvertFileIndex(private val indexWriter: (Dictionary) -> Unit) : Index {

    private val tags = Stack<String>()

    private var dictionary = MutableMapDictionary()
    private var documentNumber: DocumentNumber? = null

    override fun beginIndexing() {

    }

    override fun endIndexing() {
        indexWriter(dictionary)
        dictionary = MutableMapDictionary()
    }

    override fun startTag(tag: String) {
        tags.push(tag)
    }

    override fun endTag(tag: String) {
        tags.pop()
    }

    override fun word(term: String) {
        if ("DOCNO" == tags.peek()) {
            documentNumber = DocumentNumber.parse(term)
        } else if ("TEXT" == tags.peek()) {
            cleanTerm(term)?.let { clean: String ->
                if (null == documentNumber) throw IllegalStateException("Adding te rm from unknown document")
                dictionary.add(documentNumber!!, term = clean)
            }
        }
    }
}
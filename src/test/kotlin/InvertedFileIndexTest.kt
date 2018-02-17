import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class InvertedFileIndexTest {

    private val indexWriter: (Dictionary) -> Unit = mock()

    private lateinit var index: InvertFileIndex

    companion object {
        const val DOCUMENT_NUMBER = "WSJ920102-0154"
        const val TERM = "index"
    }

    @Before
    fun setUp() {
        //given
        index = InvertFileIndex(indexWriter)
        index.beginIndexing()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `with zero documents the index returns an empty dictionary`() {
        //given
        //when
        index.endIndexing()
        //then
        verify(indexWriter).invoke(MapDictionary())
    }

    @Test
    fun `with zero terms the index returns an empty dictionary`() {
        //given
        indexDocumentNumber(DOCUMENT_NUMBER)
        //when
        index.endIndexing()
        //then
        verify(indexWriter).invoke(MapDictionary())
    }

    @Test
    fun `with a term the index returns a dictionary`() {
        //given
        indexDocumentNumber(DOCUMENT_NUMBER)
        indexTerm(TERM)
        //when
        index.endIndexing()
        //then
        val expected = MutableMapDictionary().add(DocumentNumber.parse(DOCUMENT_NUMBER), term = TERM)
        verify(indexWriter).invoke(expected)
    }

    @Test
    fun `a posting contain a document only once`() {
        //given
        indexDocumentNumber(DOCUMENT_NUMBER)
        indexTerm(TERM)
        indexTerm(TERM)
        //when
        index.endIndexing()
        //then
        val expected = MutableMapDictionary().add(DocumentNumber.parse(DOCUMENT_NUMBER), term = TERM)
        verify(indexWriter).invoke(expected)
    }

    @Test
    fun `a term inside text tags should be cleaned`() {
        //given
        indexDocumentNumber(DOCUMENT_NUMBER)
        indexTerm("Hello")
        //when
        index.endIndexing()
        verify(indexWriter).invoke(MutableMapDictionary().add(DocumentNumber.parse(DOCUMENT_NUMBER), term = "hello"))
    }

    @Test
    fun `needs to clear index between collections`() {
        //given
        val inOrder = inOrder(indexWriter)
        indexDocumentNumber(DOCUMENT_NUMBER)
        indexTerm(TERM)
        index.endIndexing()
        //when
        index.beginIndexing()
        indexDocumentNumber(DOCUMENT_NUMBER)
        indexTerm("hello")
        index.endIndexing()
        //then
        inOrder.verify(indexWriter).invoke(MutableMapDictionary().add(DocumentNumber.parse(DOCUMENT_NUMBER), term = TERM))
        inOrder.verify(indexWriter).invoke(MutableMapDictionary().add(DocumentNumber.parse(DOCUMENT_NUMBER), term = "hello"))
    }

    private fun indexDocumentNumber(documentNumber: String) {
        index.startTag("DOCNO")
        index.word(documentNumber)
        index.endTag("DOCNO")
    }

    private fun indexTerm(term: String) {
        index.startTag("TEXT")
        index.word(term)
        index.endTag("TEXT")
    }
}
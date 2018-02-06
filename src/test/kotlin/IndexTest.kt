import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class IndexTest {

    private val indexWriter: (Dictionary) -> Unit = mock()

    private lateinit var index: Index

    companion object {
        const val DOCUMENT_NUMBER = "WSJ920102-0154"
        const val TERM = "index"
    }

    @Before
    fun setUp() {
        //given
        index = Index(indexWriter)
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
        verify(indexWriter).invoke(Dictionary())
    }

    @Test
    fun `with zero terms the index returns an empty dictionary`() {
        //given
        indexDocumentNumber(DOCUMENT_NUMBER)
        //when
        index.endIndexing()
        //then
        verify(indexWriter).invoke(Dictionary())
    }

    @Test
    fun `with a term the index returns a dictionary`() {
        //given
        indexDocumentNumber(DOCUMENT_NUMBER)
        indexTerm(TERM)
        //when
        index.endIndexing()
        //then
        val expected = Dictionary(listOf(
                Pair(TERM, Postings(arrayListOf(DOCUMENT_NUMBER)))
        ))
        verify(indexWriter).invoke(expected)
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
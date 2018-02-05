import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.mockito.InOrder
import java.io.File

class ParseTest {

    @Test
    fun `match nested tags`() {
        //given
        val index = mock<Index>()
        val parser = Parse(index)
        val inOrder: InOrder = inOrder(index)
        //when
        val file = File("src/test/kotlin/single_document.xml")
        parser.parse(file)
        //then
        inOrder.verify(index).startTag("DOC")
        inOrder.verify(index).startTag("DOCNO")
        inOrder.verify(index).endTag("DOCNO")
        inOrder.verify(index).endTag("DOC")
    }

    @Test
    fun `match word in correct tags`() {
        //given
        val index = mock<Index>()
        val parser = Parse(index)
        val inOrder: InOrder = inOrder(index)
        //when
        val file = File("src/test/kotlin/single_document.xml")
        parser.parse(file)
        //then
        inOrder.verify(index).startTag("DOCNO")
        inOrder.verify(index).word("WSJ920102-0154")
        inOrder.verify(index).endTag("DOCNO")
    }

    @Test
    fun `convert escape characters`() {
        //given
        val index = mock<Index>()
        val parser = Parse(index)
        //when
        val file = File("src/test/kotlin/single_document.xml")
        parser.parse(file)
        //then
        verify(index).word("<1>")
        verify(index).word("&")
    }

    @Test
    @Throws(Exception::class)
    fun `parser notifies indexer before parsing begins`() {
        //given
        val index = mock<Index>()
        val parse = Parse(index)
        val inOrder = inOrder(index)
        //when
        val file = File("src/test/kotlin/single_document.xml")
        parse.parse(file)
        //then
        inOrder.verify(index).beginIndexing()
    }

    @Test
    @Throws(Exception::class)
    fun `parser notifies indexer after parsing ends`() {
        //given
        val index = mock<Index>()
        val parse = Parse(index)
        val inOrder = inOrder(index)
        //when
        val file = File("src/test/kotlin/single_document.xml")
        parse.parse(file)
        //then
        inOrder.verify(index).endIndexing()
        inOrder.verifyNoMoreInteractions()
    }
}
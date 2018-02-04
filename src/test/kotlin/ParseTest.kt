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
}
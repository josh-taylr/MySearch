package parse

import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import index.Index
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import org.junit.rules.ExpectedException



class ParseTest {

    private val file = File("src/test/kotlin/parse/single_document.xml")
    private val index = mock<Index>()
    private val inOrder = inOrder(index)

    private lateinit var parser: Parse

    @Rule
    @JvmField
    val thrown = ExpectedException.none()!!

    @Before
    fun setUp() {
        //given
        parser = Parse(index)
    }

    @Test
    fun `match nested tags`() {
        //when
        parser.parse(file)
        //then
        inOrder.verify(index).startTag("DOC")
        inOrder.verify(index).startTag("DOCNO")
        inOrder.verify(index).endTag("DOCNO")
        inOrder.verify(index).endTag("DOC")
    }

    @Test
    fun `match word in correct tags`() {
        //when
        parser.parse(file)
        //then
        inOrder.verify(index).startTag("DOCNO")
        inOrder.verify(index).word("WSJ920102-0154")
        inOrder.verify(index).endTag("DOCNO")
    }

    @Test
    fun `convert escape characters`() {
        //when
        parser.parse(file)
        //then
        verify(index).word("<1>")
        verify(index).word("&")
    }

    @Test
    fun `parser notifies indexer before parsing begins`() {
        //when
        parser.parse(file)
        //then
        inOrder.verify(index).beginIndexing()
    }

    @Test
    fun `parser notifies indexer after parsing ends`() {
        //when
        parser.parse(file)
        //then
        inOrder.verify(index).endIndexing()
        inOrder.verifyNoMoreInteractions()
    }

    @Test
    fun `throws exception when tags don't match`() {
        //given
        thrown.expect(Parse.Companion.TagMismatchException::class.java)
        thrown.expectMessage("Expected 'DOCNO' new, but encountered 'DOC'")
        //when
        parser.parse(File("src/test/kotlin/parse/mismatched_tags.xml"))
    }
}
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.mockito.Mockito.verify
import java.io.File

class ParseTest {

    @Test
    fun `match start tag`() {
        //given
        val index = mock<Index>()
        val parser = Parse(index)
        //when
        val file = File("src/test/kotlin/single_document.xml")
        parser.parse(file)
        //then
        verify(index).startTag("DOC")
    }

    @Test
    fun `match end tag`() {
        //given
        val index = mock<Index>()
        val parser = Parse(index)
        //when
        val file = File("src/test/kotlin/single_document.xml")
        parser.parse(file)
        //then
        verify(index).endTag("DOC")
    }
}
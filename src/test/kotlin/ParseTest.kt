import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.verify
import java.io.File

class ParseTest {

    private var index: Index = Mockito.mock(Index::class.java)

    @Test
    fun `match start tag`() {
        //given
        val parser = Parse(index)
        //when
        val file = File("src/test/kotlin/single_document.xml")
        parser.parse(file)
        //then
        verify(index).startTag("DOC")
    }
}
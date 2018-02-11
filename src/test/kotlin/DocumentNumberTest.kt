import org.junit.Assert.*
import org.junit.Test

class DocumentNumberTest {

    @Test
    fun parseString() {
        val str = "WSJ920102-0154"
        //when
        val documentNumber = DocumentNumber.parse(str)
        //then
        assertEquals(DocumentNumber(9201020154L), documentNumber)
    }

    @Test
    fun convertToString() {
        //given
        val documentNumber = DocumentNumber(9201020154L)
        //when
        val str = documentNumber.toString()
        //then
        assertEquals("WSJ920102-0154", str)
    }
}
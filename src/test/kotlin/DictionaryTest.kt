import org.junit.Assert.*
import org.junit.Test

class DictionaryTest {

    private val documentNumber = "WSJ920102-0154"
    private val term1 = "index"
    private val term2 = "file"

    @Test
    fun `same dictionary should be equal`() {
        //given
        val dictionary = Dictionary().add(DocumentNumber.parse(documentNumber), term1)
        //when
        val result = dictionary == dictionary
        //then
        assertTrue(result)
    }

    @Test
    fun `matching dictionaries should be equal`() {
        //given
        val dictionary1 = Dictionary().add(DocumentNumber.parse(documentNumber), term1)
        val dictionary2 = Dictionary().add(DocumentNumber.parse(documentNumber), term1)
        //when
        val result = dictionary1 == dictionary2
        //then
        assertTrue(result)
    }

    @Test
    fun `different dictionaries shouldn't be equal`() {
        //given
        val dictionary1 = Dictionary().add(DocumentNumber.parse(documentNumber), term1)
        val dictionary2 = Dictionary().add(DocumentNumber.parse(documentNumber), term2)
        //when
        val result = dictionary1 == dictionary2
        //then
        assertFalse(result)
    }
}
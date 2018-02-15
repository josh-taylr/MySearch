import org.junit.Assert.*
import org.junit.Test

class MutableDictionaryTest {

    private val documentNumber = "WSJ920102-0154"
    private val term1 = "index"
    private val term2 = "file"

    @Test
    fun `same dictionary should be equal`() {
        //given
        val dictionary = MutableDictionary().add(DocumentNumber.parse(documentNumber), term1)
        //when
        val result = dictionary == dictionary
        //then
        assertTrue(result)
    }

    @Test
    fun `matching dictionaries should be equal`() {
        //given
        val dictionary1 = MutableDictionary().add(DocumentNumber.parse(documentNumber), term1)
        val dictionary2 = MutableDictionary().add(DocumentNumber.parse(documentNumber), term1)
        //when
        val result = dictionary1 == dictionary2
        //then
        assertTrue(result)
    }

    @Test
    fun `different dictionaries shouldn't be equal`() {
        //given
        val dictionary1 = MutableDictionary().add(DocumentNumber.parse(documentNumber), term1)
        val dictionary2 = MutableDictionary().add(DocumentNumber.parse(documentNumber), term2)
        //when
        val result = dictionary1 == dictionary2
        //then
        assertFalse(result)
    }
}
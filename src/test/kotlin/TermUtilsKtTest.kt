import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TermUtilsKtTest {

    @Test
    fun `terms should be lowercase`() {
        //given
        val term = "Hello"
        //when
        val cleanTerm = cleanTerm(term)
        //then
        assertEquals("hello", cleanTerm)
    }

    @Test
    fun `terms should contain only alphabetic characters`() {
        //given
        val term = "Mr."
        //when
        val cleanTerm = cleanTerm(term)
        //then
        assertEquals("mr", cleanTerm)
    }

    @Test
    fun `terms should be at least 2 characters long or null`() {
        //given
        val term = "a"
        //when
        val cleanTerm = cleanTerm(term)
        //then
        assertNull(cleanTerm)
    }
}
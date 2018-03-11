import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class PostingsTest {

    private lateinit var set: MutablePostings

    @Before
    fun setUp() {
        set = MutablePostings()

        set.add(d2)
        set.add(d3)
        set.add(d3)
        set.add(d3)
    }

    @Test
    fun getSize() {
        assertEquals(2, set.size)
    }

    @Test
    fun countShouldBeZero() {
        assertEquals(0, set.count(d1))
    }

    @Test
    fun countShouldBeOne() {
        assertEquals(1, set.count(d2))
    }

    @Test
    fun countShouldBeMany() {
        assertEquals(3, set.count(d3))
    }

    @Test
    fun addAbsentDocumentReturnTrue() {
        assertTrue(set.add(d1))
    }

    @Test
    fun addPresentDocumentReturnFalse() {
        assertFalse(set.add(d2))
    }

    @Test
    fun addAbsentPostingReturnTrue() {
        assertTrue(set.add(Posting(d1, 5)))
        assertEquals(5, set.count(d1))
    }

    @Test
    fun addPresentPostingReturnFalse() {
        assertFalse(set.add(Posting(d2, 3)))
        assertEquals(4, set.count(d2))
    }

    companion object {
        val d1 = DocumentNumber.parse("WSJ920102-0151")
        val d2 = DocumentNumber.parse("WSJ920102-0152")
        val d3 = DocumentNumber.parse("WSJ920102-0153")
    }
}
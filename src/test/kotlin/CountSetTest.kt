import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class CountSetTest {


    private lateinit var set: CountSet<String>

    @Before
    fun setUp() {
        set = CountSet()

        set.add(e2)
        set.add(e3)
        set.add(e3)
        set.add(e3)
    }

    @Test
    fun getSize() {
        assertEquals(2, set.size)
    }

    @Test
    fun countShouldBeZero() {
        assertEquals(0, set.count(e1))
    }

    @Test
    fun countShouldBeOne() {
        assertEquals(1, set.count(e2))
    }

    @Test
    fun countShouldBeMany() {
        assertEquals(3, set.count(e3))
    }

    @Test
    fun addAbsentReturnTrue() {
        assertTrue(set.add(e1))
    }

    @Test
    fun addPresentReturnFalse() {
        assertFalse(set.add(e2))
    }

    companion object {
        const val e1 = "Element 1"
        const val e2 = "Element 2"
        const val e3 = "Element 3"
    }
}
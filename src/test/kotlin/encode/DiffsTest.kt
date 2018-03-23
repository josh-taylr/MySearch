package encode

import org.junit.Assert.assertThat
import org.hamcrest.collection.IsIterableContainingInOrder.contains as matchOrder
import org.junit.Test

class DiffsTest {

    @Test
    fun encodeSortedList() {
        //given
        val numbers = listOf(27L, 28L, 29L, 32L, 33L)
        //when
        val diffs: List<Long> = numbers.diffs()
        //then
        assertThat(diffs, matchOrder(27L, 1L, 1L, 3L, 1L))
    }

    @Test
    fun decodeDiffs() {
        //given
        val diffs = listOf(129L, 10, 22, 5, 1)
        //when
        val numbers = diffs.expandDiffs()
        //then
        assertThat(numbers, matchOrder(129L, 139L, 161L, 166L, 167L))
    }
}
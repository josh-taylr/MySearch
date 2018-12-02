package encode

import org.junit.Assert.*
import org.junit.Test
import org.hamcrest.collection.IsIterableContainingInOrder.contains as matchOrder

class VariableByteTest {

    private val vb = VariableByte

    @Test
    fun encodeZero() {
        //given
        val number = 0L
        //when
        val bytes= vb.encode(number)
        //then
        assertThat(bytes, matchOrder(0b1000_0000.toByte()))
    }

    @Test
    fun encodeMaxOneByte() {
        //given
        val maxByte = 0b0111_1111.toLong()
        //when
        val bytes = vb.encode(maxByte)
        //then
        assertThat(bytes, matchOrder(0b1111_1111.toByte()))
    }

    @Test
    fun encodeMinTwoByte() {
        //given
        val minTwoByte = 0b1000_0000.toLong()
        //when
        val bytes = vb.encode(minTwoByte)
        //then
        assertThat(bytes, matchOrder(0b0000_0001.toByte(), 0b1000_0000.toByte()))
    }

    @Test
    fun encodeMultiByte() {
        //given
        val num = 129L
        //when
        val bytes = vb.encode(num)
        //then
        assertThat(bytes, matchOrder<Byte>(0b0000_0001.toByte(), 0b1000_0001.toByte()))
    }

    @Test
    fun encodeDocumentNumber() {
        //given
        val num = 9105280171L
        //when
        val bytes = vb.encode(num)
        //then
        assertThat(bytes, matchOrder(
                0b0010_0001.toByte(),
                0b0111_0101.toByte(),
                0b0101_1110.toByte(),
                0b0001_1001.toByte(),
                0b1010_1011.toByte()))
    }

    @Test
    fun encodeList() {
        //given
        val numbers = listOf(9105280171L, 9102140039L, 9203190111L)
        //when
        val bytes = vb.encode(numbers)
        //then
        assertThat(bytes, matchOrder(
                0b0010_0001.toByte(),
                0b0111_0101.toByte(),
                0b0101_1110.toByte(),
                0b0001_1001.toByte(),
                0b1010_1011.toByte(),

                0b0010_0001.toByte(),
                0b0111_0100.toByte(),
                0b0001_1110.toByte(),
                0b0100_0101.toByte(),
                0b1000_0111.toByte(),

                0b0010_0010.toByte(),
                0b0010_0100.toByte(),
                0b0011_0110.toByte(),
                0b0001_0010.toByte(),
                0b1101_1111.toByte()))
    }

    @Test
    fun decodeZero() {
        //given
        val bytes = listOf(0b1000_0000.toByte())
        //when
        val numbers: List<Long> = vb.decode(bytes)
        //then
        assertEquals(listOf(0L), numbers)
    }

    @Test
    fun decodeSingleByte() {
        //given
        val bytes = listOf(0b1000_0101.toByte())
        //when
        val numbers: List<Long> = vb.decode(bytes)
        //then
        assertThat(numbers, matchOrder(5L))
    }

    @Test
    fun decodeMultiBytes() {
        //given
        val bytes = listOf(0b0000_0001.toByte(), 0b1000_0001.toByte())
        //when
        val numbers: List<Long> = vb.decode(bytes)
        //then
        assertThat(numbers, matchOrder(129L))
    }

    @Test
    fun decodeDocumentNumber() {
        //given
        val bytes = listOf(
                0b00100000.toByte(),
                0b00001010.toByte(),
                0b01000011.toByte(),
                0b01111110.toByte(),
                0b11111011.toByte())
        //when
        val numbers: List<Long> = vb.decode(bytes)
        //then
        assertThat(numbers, matchOrder(8612020091L))
    }

    @Test
    fun decodeList() {
        //given
        val bytes = listOf(
                0b0010_0001.toByte(),
                0b0111_0101.toByte(),
                0b0101_1110.toByte(),
                0b0001_1001.toByte(),
                0b1010_1011.toByte(),

                0b0010_0001.toByte(),
                0b0111_0100.toByte(),
                0b0001_1110.toByte(),
                0b0100_0101.toByte(),
                0b1000_0111.toByte(),

                0b0010_0010.toByte(),
                0b0010_0100.toByte(),
                0b0011_0110.toByte(),
                0b0001_0010.toByte(),
                0b1101_1111.toByte()
        )
        //when
        val numbers: List<Long> = vb.decode(bytes)
        //then
        assertThat(numbers, matchOrder(9105280171L, 9102140039L, 9203190111L))
    }

    @Test
    fun encodeAndDecodeAreSymmetric() {
        //when
        val numbers = vb.decode(vb.encode(listOf(9105280171L, 9102140039L, 9203190111L)))
        //then
        assertThat(numbers, matchOrder(9105280171L, 9102140039L, 9203190111L))
    }
}
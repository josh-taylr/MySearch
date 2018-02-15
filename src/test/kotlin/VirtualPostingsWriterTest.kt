import org.junit.Test

import org.junit.Assert.*
import java.io.*

class VirtualPostingsWriterTest {

    private val dictionary = MutableDictionary()
            .add(DocumentNumber.parse("WSJ920102-0154"), "index")
            .add(DocumentNumber.parse("WSJ920102-0154"), "search")
            .add(DocumentNumber.parse("WSJ920102-0155"), "search")
            .add(DocumentNumber.parse("WSJ920102-0155"), "engine")

    private val bytes = ByteArrayOutputStream().also {
        DataOutputStream(it).run {
            writeLong(4 * 8L)

        }
    }.toByteArray()

    @Test
    fun write() {
        //given
        val stream = ByteArrayOutputStream()
        //when
        VirtualPostingsWriter().write(dictionary, stream)
        //then
        assertArrayEquals(bytes, stream.toByteArray())
    }

//    @Test
//    fun read() {
//        //given
//        val stream = ByteArrayInputStream(bytes)
//        val file = RandomAccessFile(, "r")
//        //when
//        val result = VirtualPostingsReader(file).read(stream)
//        //then
//        assertEquals(dictionary, result)
//    }
}
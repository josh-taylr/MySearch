import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class DictionaryFileStreamTest {

    private val dictionary = MutableDictionary()
            .add(DocumentNumber.parse("WSJ920102-0154"), "index")
            .add(DocumentNumber.parse("WSJ920102-0154"), "search")
            .add(DocumentNumber.parse("WSJ920102-0155"), "search")
            .add(DocumentNumber.parse("WSJ920102-0155"), "engine")

    private val bytes = ByteArrayOutputStream().also {
        DataOutputStream(it).run {
            writeByte(6)
            writeBytes("engine")
            writeInt(1)
            writeLong(9201020155L)

            writeByte(5)
            writeBytes("index")
            writeInt(1)
            writeLong(9201020154L)

            writeByte(6)
            writeBytes("search")
            writeInt(2)
            writeLong(9201020154L)
            writeLong(9201020155L)
        }
    }.toByteArray()

    @Test
    fun write() {
        //given
        val stream = ByteArrayOutputStream()
        //when
        DictionaryFileStream().write(dictionary, stream)
        //then
        assertArrayEquals(bytes, stream.toByteArray())
    }

    @Test
    fun read() {
        //given
        val stream = ByteArrayInputStream(bytes)
        //when
        val result = DictionaryFileStream().read(stream)
        //then
        assertEquals(dictionary, result)
    }
}
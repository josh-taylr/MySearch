import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class DictionaryFileStreamTest {

    private val dictionary = Dictionary()
            .add("WSJ920102-0154", "index")
            .add("WSJ920102-0154", "search")
            .add("WSJ920102-0155", "search")
            .add("WSJ920102-0155", "engine")

    private val dictionaryBytes = "engine WSJ920102-0155 index WSJ920102-0154 search WSJ920102-0154 WSJ920102-0155 "

    @Test
    fun write() {
        //given
        val stream = ByteArrayOutputStream()
        //when
        DictionaryFileStream().write(dictionary, stream)
        //then
        assertEquals(dictionaryBytes, stream.toString())
    }

    @Test
    fun read() {
        //given
        val stream = ByteArrayInputStream(dictionaryBytes.toByteArray())
        //when
        val result = DictionaryFileStream().read(stream)
        //then
        assertEquals(result, dictionary)
    }
}
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.ByteArrayOutputStream

class DictionaryFileWriterTest {

    @Test
    fun write() {
        //given
        val stream = ByteArrayOutputStream()
        //when
        DictionaryFileWriter(2).write(testDictionary, stream)
        //then
        assertArrayEquals(testDictionaryBytes, stream.toByteArray())
    }
}
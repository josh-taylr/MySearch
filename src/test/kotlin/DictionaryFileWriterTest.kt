import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class DictionaryFileWriterTest {

    private val file = File.createTempFile("my_search_test", null)

    @Test
    fun write() {
        //when
        DictionaryFileWriter(2).write(testDictionary, file)
        //then
        val readBytes = file.readBytes()
        val testDictionaryBytes1 = testDictionaryBytes
        assertArrayEquals(testDictionaryBytes1, readBytes)
    }
}
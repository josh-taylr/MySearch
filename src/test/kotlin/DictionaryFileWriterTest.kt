import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.File

class DictionaryFileWriterTest {

    private val file = File.createTempFile("my_search_test", null)

    @Test
    fun write() {
        //when
        DictionaryFileWriter(2).write(testDictionary, file)
        //then
        assertArrayEquals(testDictionaryBytes, file.readBytes())
    }
}
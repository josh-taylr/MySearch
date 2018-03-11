import org.junit.Test
import java.io.File
import java.util.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class DictionaryFileReaderTest {

    private val reader = DictionaryFileReader()
    private val file = File("/tmp/dictionary_file.dat")

    @BeforeTest
    fun setupClass() {
        file.outputStream().buffered().use {
            it.write(testDictionaryBytes)
        }
    }

    @AfterTest
    fun tearDown() {
        file.delete()
    }

    @Test
    fun readDictionary() {
        //when
        val result = reader.readDictionary(file)
        //then
        val expected = TreeMap<String, DictionaryBlock>().apply {
            put("engine",DictionaryBlock(92L, 45L))
            put("postings", DictionaryBlock(137L, 48L))
            put("term", DictionaryBlock(185L, 21L))
        }
        assertEquals(expected, result)
    }

    @Test
    fun readDictionaryBlock() {
        //when
        val result = reader.readDictionary(file, DictionaryBlock(137L, 48L))
        //then
        val expected = TreeMap<String, PostingsBlock>().apply {
            put("postings", PostingsBlock(44L, 12L))
            put("search", PostingsBlock(56L, 24L))
        }
        assertEquals(expected, result)
    }

    @Test
    fun readPostings() {
        //when
        val result = reader.readPostings(file, PostingsBlock(56, 24))
        //then
        val expected = postingsOf("WSJ920102-0154", "WSJ920102-0155")
        assertEquals(expected, result)
    }
}
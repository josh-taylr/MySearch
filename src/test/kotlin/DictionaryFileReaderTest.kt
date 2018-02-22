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
    fun readDictionaryBlock() {
        //when
        val result = reader.readDictionary(file)
        //then
        val expected = TreeMap<String, DictionaryBlock>().apply {
            put("engine",DictionaryBlock(64L, 45L))
            put("postings", DictionaryBlock(109L, 48L))
            put("term", DictionaryBlock(157L, 21L))
        }
        assertEquals(expected, result)
    }

    @Test
    fun readDictionary() {
        //when
        val result = reader.readDictionary(file, DictionaryBlock(109L, 48L))
        //then
        val expected = TreeMap<String, PostingsBlock>().apply {
            put("postings", PostingsBlock(32L, 8L))
            put("search", PostingsBlock(40L, 16L))
        }
        assertEquals(expected, result)
    }

    @Test
    fun readPostings() {
        //when
        val result = reader.readPostings(file, PostingsBlock(40, 16))
        //then
        val expected = setOf(DocumentNumber(9201020154), DocumentNumber(9201020155))
        assertEquals(expected, result)
    }
}
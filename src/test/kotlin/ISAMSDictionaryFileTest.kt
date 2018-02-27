import org.junit.Test
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class ISAMSDictionaryFileTest {

    private val reader = DictionaryFileReader()
    private val file = File("/tmp/dictionary_file.dat")
    private val dictionary = ISAMSDictionaryFile(file, reader)

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
    fun getEntries() {
        //when
        val entries = dictionary.entries
        //then
        val expected = setOf(
                ISAMSDictionaryFile.Entry("engine", postingsOf("WSJ920102-0155")),
                ISAMSDictionaryFile.Entry("index", postingsOf("WSJ920102-0154", "WSJ920102-0155")),
                ISAMSDictionaryFile.Entry("postings", postingsOf("WSJ920102-0154")),
                ISAMSDictionaryFile.Entry("search", postingsOf("WSJ920102-0154", "WSJ920102-0155")),
                ISAMSDictionaryFile.Entry("term", postingsOf("WSJ920102-0154"))
        )
        assertEquals(expected, entries)
    }

    @Test
    fun get() {
        //when
        val postings = dictionary["search"]
        //then
        assertEquals(postingsOf("WSJ920102-0154", "WSJ920102-0155"), postings)
    }
}
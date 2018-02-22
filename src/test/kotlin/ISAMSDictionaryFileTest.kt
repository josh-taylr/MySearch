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
                ISAMSDictionaryFile.Entry("engine", setOf(DocumentNumber(9201020155))),
                ISAMSDictionaryFile.Entry("index", setOf(DocumentNumber(9201020154), DocumentNumber(9201020155))),
                ISAMSDictionaryFile.Entry("postings", setOf(DocumentNumber(9201020154))),
                ISAMSDictionaryFile.Entry("search", setOf(DocumentNumber(9201020154), DocumentNumber(9201020155))),
                ISAMSDictionaryFile.Entry("term", setOf(DocumentNumber(9201020154)))
        )
        assertEquals(expected, entries)
    }

    @Test
    fun get() {
        //when
        val postings = dictionary["search"]
        //then
        assertEquals(setOf(DocumentNumber(9201020154), DocumentNumber(9201020155)), postings)
    }
}
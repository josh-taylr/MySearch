package dictionary

import org.junit.Test
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

class FileDictionaryTest {

    private val reader = FileDictionaryReader()
    private val file = File("/tmp/dictionary_file.dat")
    private val dictionary = FileDictionary(file, reader)

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
                FileDictionary.Entry("engine", postingsOf("WSJ920102-0155")),
                FileDictionary.Entry("index", postingsOf("WSJ920102-0154", "WSJ920102-0155")),
                FileDictionary.Entry("postings", postingsOf("WSJ920102-0154")),
                FileDictionary.Entry("search", postingsOf("WSJ920102-0154", "WSJ920102-0155")),
                FileDictionary.Entry("term", postingsOf("WSJ920102-0154"))
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
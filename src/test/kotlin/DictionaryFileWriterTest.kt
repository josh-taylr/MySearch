import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class DictionaryFileWriterTest {

    private val dictionary = MutableMapDictionary()
            .add(DocumentNumber.parse("WSJ920102-0154"), "index")
            .add(DocumentNumber.parse("WSJ920102-0154"), "search")
            .add(DocumentNumber.parse("WSJ920102-0155"), "search")
            .add(DocumentNumber.parse("WSJ920102-0155"), "engine")
            .add(DocumentNumber.parse("WSJ920102-0154"), "postings")
            .add(DocumentNumber.parse("WSJ920102-0154"), "term")
            .add(DocumentNumber.parse("WSJ920102-0155"), "index")

    private val bytes = ByteArrayOutputStream().also {
        DataOutputStream(it).run {
            val termCount = 5
            val postingsCount = 7
            val characterCount = 29
            val nodeInfoCount = 1L + 8 + 4
            // dictionary offset
            val dictionaryOffset = 8L + (postingsCount * 8)
            writeLong(dictionaryOffset + (characterCount + (termCount * (1 + 8 + 4))))
            // all postings
            writeLong(9201020155L)
            writeLong(9201020154L)
            writeLong(9201020155L)
            writeLong(9201020154L)
            writeLong(9201020154L)
            writeLong(9201020155L)
            writeLong(9201020154L)
            // all terms with pointers to postings
            write(6)
            writeBytes("engine")
            writeLong(8L)
            writeInt(1)
            write(5)
            writeBytes("index")
            writeLong(16L)
            writeInt(2)
            write(8)
            writeBytes("postings")
            writeLong(32L)
            writeInt(1)
            write(6)
            writeBytes("search")
            writeLong(40L)
            writeInt(2)
            write(4)
            writeBytes("term")
            writeLong(56L)
            writeInt(1)
            // nodes with pointers to terms
            write(6)
            writeBytes("engine")
            writeLong(dictionaryOffset)
            writeLong(2)

            write(8)
            writeBytes("postings")
            writeLong(dictionaryOffset + 11 + (nodeInfoCount * 2))
            writeLong(2)

            write(4)
            writeBytes("term")
            writeLong(dictionaryOffset + 11 + (nodeInfoCount * 2) + 14 + (nodeInfoCount * 2))
            writeLong(1)
        }
    }.toByteArray()

    @Test
    fun write() {
        //given
        val stream = ByteArrayOutputStream()
        //when
        DictionaryFileWriter(2).write(dictionary, stream)
        //then
        val expected = String(bytes)
        val result = String(stream.toByteArray())
        assertEquals(expected, result)
    }
}
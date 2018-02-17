import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.io.*

@RunWith(MockitoJUnitRunner::class)
open class VirtualPostingsWriterTest {

    @Mock
    lateinit var reader: PostingsFileReader

    private val dictionary = MutableMapDictionary()
            .add(DocumentNumber.parse("WSJ920102-0154"), "index")
            .add(DocumentNumber.parse("WSJ920102-0154"), "search")
            .add(DocumentNumber.parse("WSJ920102-0155"), "search")
            .add(DocumentNumber.parse("WSJ920102-0155"), "engine")

    private val bytes = ByteArrayOutputStream().also {
        DataOutputStream(it).run {
            // dictionary offset
            writeLong(4 * 8L)
            // all postings
            writeLong(9201020155L)
            writeLong(9201020154L)
            writeLong(9201020154L)
            writeLong(9201020155L)
            // all terms with pointers to postings
            write(6)
            writeBytes("engine")
            writeLong(8L)
            writeInt(1)
            write(5)
            writeBytes("index")
            writeLong(16L)
            writeInt(1)
            write(6)
            writeBytes("search")
            writeLong(24L)
            writeInt(2)
        }
    }.toByteArray()

    @Test
    fun write() {
        //given
        val stream = ByteArrayOutputStream()
        //when
        VirtualPostingsWriter().write(dictionary, stream)
        //then
        assertArrayEquals(bytes, stream.toByteArray())
    }

    @Test
    fun read() {
        //given
        val stream = ByteArrayInputStream(bytes)
        whenever(reader.read(8L, 1)).thenReturn(setOf(DocumentNumber(9201020155L)))
        whenever(reader.read(16L, 1)).thenReturn(setOf(DocumentNumber(9201020154L)))
        whenever(reader.read(24L, 2)).thenReturn(setOf(DocumentNumber(9201020154L), DocumentNumber(9201020155L)))
        //when
        val result = VirtualPostingsReader(reader).read(stream)
        //then
        assertEquals(dictionary, result)
    }
}
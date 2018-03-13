package dictionary

import org.junit.Assert.*
import org.junit.Test
import java.io.*
import java.nio.ByteBuffer

class StreamKtTest {

    private val posting = Posting(DocumentNumber(9201020154), 27)

    private val postings = postingsOf(
            "WSJ920102-0154" to 27,
            "WSJ920102-0155" to 13,
            "WSJ920102-0175" to 2
    )

    private val postingBytes = ByteBuffer.allocate(12).apply {
        putLong(9201020154)
        putInt(27)
    }.array().let { assertNotNull(it);  it!! }

    private val postingsBytes = ByteBuffer.allocate(36).apply {
        putLong(9201020154)
        putInt(27)
        putLong(9201020155)
        putInt(13)
        putLong(9201020175)
        putInt(2)
    }.array().let { assertNotNull(it);  it!! }

    @Test
    fun writePosting() {
        //given
        val stream = ByteArrayOutputStream()
        val outputStream = DataOutputStream(stream)
        //when
        outputStream.writePosting(posting)
        //then
        assertArrayEquals(postingBytes, stream.toByteArray())
    }
    @Test
    fun writePostings() {
        //given
        val stream = ByteArrayOutputStream()
        val outputStream = DataOutputStream(stream)
        //when
        outputStream.writePostings(postings)
        //then
        assertArrayEquals(postingsBytes, stream.toByteArray())
    }

    @Test
    fun readPosting() {
        //given
        val stream = DataInputStream(ByteArrayInputStream(postingBytes))
        //when
        val result = stream.readPosting()
        //then
        assertEquals(posting, result)
    }

    @Test
    fun readPostings() {
        //given
        val dataStream = DataInputStream(ByteArrayInputStream(postingsBytes))
        //when
        val result = dataStream.readPostings(postingsBytes.size.toLong())
        //then
        assertEquals(postings, result)
    }
}
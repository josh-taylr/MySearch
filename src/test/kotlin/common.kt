import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

val testDictionary = dictionaryOf(
        "engine" to postingsOf("WSJ920102-0155"),
        "index" to postingsOf("WSJ920102-0154", "WSJ920102-0155"),
        "postings" to postingsOf("WSJ920102-0154"),
        "search" to postingsOf("WSJ920102-0154", "WSJ920102-0155"),
        "term" to postingsOf("WSJ920102-0154")
)

val testDictionaryBytes: ByteArray = ByteArrayOutputStream().also {
    DataOutputStream(it).run {
        val termCount = testDictionary.keys.size
        val postingsCount = testDictionary.values.sumBy { it.size }
        val characterSize = testDictionary.keys.sumBy { it.length }
        val nodeInfoSize = 1L + 8L + 8L
        val dictionaryOffset = 8L + (postingsCount * (8 + 4))
        writeLong(dictionaryOffset + (characterSize + (termCount * nodeInfoSize)))
        // all postings
        writeLong(9201020155L)
        writeInt(1)
        writeLong(9201020154L)
        writeInt(1)
        writeLong(9201020155L)
        writeInt(1)
        writeLong(9201020154L)
        writeInt(1)
        writeLong(9201020154L)
        writeInt(1)
        writeLong(9201020155L)
        writeInt(1)
        writeLong(9201020154L)
        writeInt(1)
        // all terms with pointers to postings
        write(6)
        writeBytes("engine")
        writeLong(8L)
        writeLong(12L)
        write(5)
        writeBytes("index")
        writeLong(20L)
        writeLong(24L)
        write(8)
        writeBytes("postings")
        writeLong(44L)
        writeLong(12L)
        write(6)
        writeBytes("search")
        writeLong(56L)
        writeLong(24L)
        write(4)
        writeBytes("term")
        writeLong(80L)
        writeLong( 12L)
        // nodes with pointers to terms
        write(6)
        writeBytes("engine")
        writeLong(dictionaryOffset)
        writeLong(11 + (nodeInfoSize * 2))

        write(8)
        writeBytes("postings")
        writeLong(dictionaryOffset + 11 + (nodeInfoSize * 2))
        writeLong(14 + (nodeInfoSize * 2))

        write(4)
        writeBytes("term")
        writeLong(dictionaryOffset + 11 + (nodeInfoSize * 2) + 14 + (nodeInfoSize * 2))
        writeLong(4 + nodeInfoSize)
    }
}.toByteArray()
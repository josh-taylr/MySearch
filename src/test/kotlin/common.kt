import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

val testDictionary = MutableMapDictionary()
        .add(DocumentNumber.parse("WSJ920102-0154"), "index")
        .add(DocumentNumber.parse("WSJ920102-0154"), "search")
        .add(DocumentNumber.parse("WSJ920102-0155"), "search")
        .add(DocumentNumber.parse("WSJ920102-0155"), "engine")
        .add(DocumentNumber.parse("WSJ920102-0154"), "postings")
        .add(DocumentNumber.parse("WSJ920102-0154"), "term")
        .add(DocumentNumber.parse("WSJ920102-0155"), "index")

val testDictionaryBytes: ByteArray = ByteArrayOutputStream().also {
    DataOutputStream(it).run {
        val termCount = 5
        val postingsCount = 7
        val characterCount = 29
        val nodeInfoCount = 1L + 8L + 8L
        // testDictionary offset
        val dictionaryOffset = 8L + (postingsCount * 8L)
        writeLong(dictionaryOffset + (characterCount + (termCount * nodeInfoCount)))
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
        writeLong(8L)
        write(5)
        writeBytes("index")
        writeLong(16L)
        writeLong(16L)
        write(8)
        writeBytes("postings")
        writeLong(32L)
        writeLong(8L)
        write(6)
        writeBytes("search")
        writeLong(40L)
        writeLong(16L)
        write(4)
        writeBytes("term")
        writeLong(56L)
        writeLong( 8L)
        // nodes with pointers to terms
        write(6)
        writeBytes("engine")
        writeLong(dictionaryOffset)
        writeLong(11 + (nodeInfoCount * 2))

        write(8)
        writeBytes("postings")
        writeLong(dictionaryOffset + 11 + (nodeInfoCount * 2))
        writeLong(14 + (nodeInfoCount * 2))

        write(4)
        writeBytes("term")
        writeLong(dictionaryOffset + 11 + (nodeInfoCount * 2) + 14 + (nodeInfoCount * 2))
        writeLong(4 + nodeInfoCount)
    }
}.toByteArray()
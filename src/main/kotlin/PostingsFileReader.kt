import java.io.Closeable
import java.io.File
import java.io.RandomAccessFile

open class PostingsFileReader(file: File) : Closeable {

    private val accessFile: RandomAccessFile = RandomAccessFile(file, "r")

    open fun read(location: Long, count: Int) : Set<DocumentNumber> {
        val postings = mutableSetOf<DocumentNumber>()
        accessFile.run {
            seek(location)
            repeat(count) {
                val number = DocumentNumber(readLong())
                postings.add(number)
            }
        }
        return postings
    }

    override fun close() = accessFile.close()
}
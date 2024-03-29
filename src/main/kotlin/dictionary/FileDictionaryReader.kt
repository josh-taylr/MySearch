package dictionary

import java.io.DataInput
import java.io.File
import java.io.RandomAccessFile
import java.util.*

class FileDictionaryReader(internal val readPostings: DataInput.(size: Long) -> Postings = DataInput::readEncodedPostings) {

    fun readDictionary(file: File): NavigableMap<String, DictionaryBlock> {
        val map = mutableMapOf<String, DictionaryBlock>()
        RandomAccessFile(file, "r").run {
            seek(0) // reset

            val dictionaryPosition = readLong()
            seek(dictionaryPosition)

            while (true) {
                val termLength = read()
                if (EOF == termLength) break
                val term = ByteArray(termLength).also(::readFully).let { String(it) }
                val position = readLong()
                val size = readLong()
                map[term] = DictionaryBlock(position, size)
            }
        }
        return map
    }

    fun readDictionary(file: File, block: DictionaryBlock): Map<String, PostingsBlock> {
        val map = mutableMapOf<String, PostingsBlock>()
        RandomAccessFile(file, "r").run {
            seek(block.position)
            while (filePointer < (block.position + block.size)) {
                val term = readTerm()
                val position = readLong()
                val size = readLong()
                map[term] = PostingsBlock(position, size)
            }
        }
        return map
    }

    fun readPostings(file: File, block: PostingsBlock): Postings {
        return RandomAccessFile(file, "r").run {
            seek(block.position)
            this.readPostings(block.size)
        }
    }

    private fun <K, V> mutableMapOf() = TreeMap<K, V>()
}
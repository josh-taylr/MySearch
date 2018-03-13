package dictionary

import java.io.File
import java.util.*
import kotlin.collections.AbstractMap

/**
 *
 */
class FileDictionary(private val file: File, private val fileReader: FileDictionaryReader) : AbstractMap<String, Postings>() {

    private val map: NavigableMap<String, DictionaryBlock> by lazy {
        fileReader.readDictionary(file)
    }

    override val entries by lazy {
        map.flatMap {
            val block = loadBlock(it.value)
            block.toList()
        }
                .map { (term, block) ->
                    val value = loadBlock(block)
                    Entry(term, value)
                }
                .toSet()
    }

    override fun get(key: String): Postings? {
        return map.floorEntry(key).value
                .let { loadBlock(it) }[key]
                .let { loadBlock(it!!) }
    }

    private fun loadBlock(block: DictionaryBlock): Map<String, PostingsBlock> {
        return fileReader.readDictionary(file, block)
    }

    private fun loadBlock(block: PostingsBlock): Postings {
        return fileReader.readPostings(file, block)
    }

    override fun toString(): String {
        return map.toString()
    }

    data class Entry(override val key: String, override val value: Postings) : Map.Entry<String, Postings>
}
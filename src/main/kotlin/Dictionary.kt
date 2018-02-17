import java.util.*

typealias DictionaryEntry = Pair<String, Postings>

interface Dictionary : Iterable<DictionaryEntry> {

    fun search(vararg term: String): List<DocumentNumber>
}

abstract class AbstractDictionary : Dictionary {

    abstract fun entries(): Set<DictionaryEntry>

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is AbstractDictionary -> entries() == other.entries()
            else -> false
        }
    }

    override fun hashCode(): Int {
        return entries().hashCode()
    }

    override fun toString(): String {
        return entries().toString()
    }
}

open class MapDictionary(private val map: TreeMap<String, out Postings> = TreeMap()) : AbstractDictionary() {

    override fun search(vararg term: String): List<DocumentNumber> {
        return term.mapNotNull { map[it] }
                .reduce {acc, postings -> acc.and(postings)}
                .toList()
    }

    override fun entries(): Set<DictionaryEntry> {
        return map.entries
                .map { (term, p) -> DictionaryEntry(term, p) }
                .toSet()
    }

    override fun iterator(): Iterator<DictionaryEntry> {
        return IteratorDecorator(map)
    }

    private class IteratorDecorator(map: TreeMap<String, out Postings>) : Iterator<DictionaryEntry> {

        val iterator = map.iterator()

        override fun hasNext(): Boolean {
            return iterator.hasNext()
        }

        override fun next(): DictionaryEntry {
            val (term, postings) = iterator.next()
            return DictionaryEntry(term, postings)
        }
    }
}

/*
    An inverted file index containing a list of terms with with corresponding postings.
 */
class MutableMapDictionary(private val map: TreeMap<String, InMemoryPostings> = TreeMap()) : MapDictionary(map) {

    fun add(documentNumber: DocumentNumber, term: String): MutableMapDictionary {
        val postings = map.getOrPut(term) { InMemoryPostings() }
        postings.add(documentNumber)
        return this
    }
}

fun Dictionary.postingsCount() = sumBy { (_, postings) -> postings.count() }

/**
 * The sum of term lengths in this dictionary.
 */
fun Dictionary.termsSize() = sumBy { (term, _) -> term.length }
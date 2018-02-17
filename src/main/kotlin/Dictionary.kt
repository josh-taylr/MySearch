import java.util.*

open class Dictionary(private val map: TreeMap<String, out Postings> = TreeMap()) : Iterable<Pair<String, Postings>> {

    fun search(vararg term: String): List<DocumentNumber> {
        return term.mapNotNull { map[it] }
                .reduce({acc, postings -> acc.and(postings)})
                .toList()
    }

    override fun equals(other: Any?): Boolean = when (other) {
        is Dictionary -> map == other.map
        else -> false
    }

    override fun hashCode(): Int = map.hashCode()

    override fun toString(): String = map.toString()

    override fun iterator(): Iterator<Pair<String, Postings>> {
        return map.entries.map { Pair(it.key, it.value) }.iterator()
    }
}

fun Dictionary.postingsCount() = sumBy { (_, postings) -> postings.count() }

/**
 * The sum of term lengths in this dictionary.
 */
fun Dictionary.termsSize() = sumBy { (term, _) -> term.length }
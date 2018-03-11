import java.util.*

typealias Dictionary = Map<String, Postings>

typealias MutableDictionary = MutableMap<String, MutablePostings>

fun emptyDictionary(): Dictionary = emptyMap()

fun dictionaryOf(vararg entries: Pair<String, Postings>): Dictionary = TreeMap<String, Postings>().apply {
    putAll(entries)
}

fun mutableDictionaryOf(): MutableDictionary = TreeMap()

typealias Postings = Set<Posting>

class MutablePostings : Postings, AbstractMutableSet<Posting>() {

    private val map = mutableMapOf<DocumentNumber, Int>()

    override val size: Int
        get() = map.size

    fun add(documentNumber: DocumentNumber): Boolean = null == map.put(documentNumber, count(documentNumber) + 1)

    override fun add(element: Posting): Boolean = null == element.run {
        map.put(documentNumber, count(documentNumber) + element.termFrequency)
    }

    override fun iterator(): MutableIterator<Posting> = map.entries
            .map { (e, count) -> Posting(e, count) }
            .toMutableList()
            .iterator()

    fun count(documentNumber: DocumentNumber): Int = map.getOrDefault(documentNumber, 0)
}

fun postingsOf(vararg documentNumber: String): Postings = mutableSetOf<Posting>().apply {
    addAll(documentNumber.map { Posting(DocumentNumber.parse(it), 1) })
}

fun postingsOf(vararg postings: Pair<String, Int>): Postings = mutableSetOf<Posting>().apply {
    addAll(postings.map { Posting(DocumentNumber.parse(it.first), it.second) })
}

fun mutablePostingsOf(): MutablePostings = MutablePostings()

data class Posting(val documentNumber: DocumentNumber, val termFrequency: Int)

fun Dictionary.postingsCount() = entries.sumBy { (_, postings) -> postings.count() }

fun Dictionary.termsSize() = entries.sumBy { (term, _) -> term.length }

/* Intersect the postings while summing the elements */
//TODO write tests
infix fun Postings.intersect(other: Postings): Postings {
    return MutablePostings().apply {
        val p1 = this@intersect.associateBy { it.documentNumber }
        val p2 = other.associateBy { it.documentNumber }
        p1.keys.intersect(p2.keys).forEach { documentNumber ->
            val termFrequency = (p1[documentNumber]?.termFrequency ?: 0) + (p2[documentNumber]?.termFrequency ?: 0)
            add(Posting(documentNumber, termFrequency))
        }
    }
}

/**
 * Return every posting from this collections plus every posting from the other collection.
 *
 * When postings for the same document exist in both collections returned posting contains a posting for
 * that document with the sum of the two term frequencies.
 */
//TODO write tests
infix fun Postings.union(other: Postings): Postings {
    return MutablePostings().apply {
        val p1 = this@union.associateBy { it.documentNumber }
        val p2 = other.associateBy { it.documentNumber }
        p1.keys.union(p2.keys).forEach { documentNumber ->
            val termFrequency = (p1[documentNumber]?.termFrequency ?: 0) + (p2[documentNumber]?.termFrequency ?: 0)
            add(Posting(documentNumber, termFrequency))
        }
    }
}
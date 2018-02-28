import java.util.*

typealias Dictionary = Map<String, Postings>

typealias MutableDictionary = MutableMap<String, MutablePostings>

fun emptyDictionary(): Dictionary = emptyMap()

fun dictionaryOf(vararg entries: Pair<String, Postings>): Dictionary = TreeMap<String, Postings>().apply {
    putAll(entries)
}

fun mutableDictionaryOf(): MutableDictionary = TreeMap()

typealias Postings = Set<DocumentNumber>

typealias MutablePostings = MutableSet<DocumentNumber>

fun postingsOf(vararg documentNumber: String): Postings = mutableSetOf<DocumentNumber>().apply {
    addAll(documentNumber.map { DocumentNumber.parse(it) })
}

fun mutablePostingsOf(): MutablePostings = CountSet()

fun Dictionary.postingsCount() = entries.sumBy { (_, postings) -> postings.count() }

fun Dictionary.termsSize() = entries.sumBy { (term, _) -> term.length }
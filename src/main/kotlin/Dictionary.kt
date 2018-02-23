import java.util.*

typealias Dictionary = Map<String, PostingsType>

typealias MutableDictionary = MutableMap<String, MutablePostingsType>

fun emptyDictionary(): Dictionary = emptyMap()

fun dictionaryOf(vararg entries: Pair<String, PostingsType>): Dictionary = TreeMap<String, PostingsType>().apply {
    putAll(entries)
}

fun mutableDictionaryOf(): MutableDictionary = TreeMap()

typealias PostingsType = Set<DocumentNumber>

typealias MutablePostingsType = MutableSet<DocumentNumber>

fun postingsOf(vararg documentNumber: String): PostingsType = mutableSetOf<DocumentNumber>().apply {
    addAll(documentNumber.map { DocumentNumber.parse(it) })
}

fun mutablePostingsOf(): MutablePostingsType = mutableSetOf()

fun Dictionary.postingsCount() = entries.sumBy { (_, postings) -> postings.count() }

fun Dictionary.termsSize() = entries.sumBy { (term, _) -> term.length }
package rank

import dictionary.Dictionary
import dictionary.DocumentNumber

interface RankingStrategy {

    fun rank(dictionary: Dictionary, documentLength: Map<DocumentNumber, Int>, terms: Iterable<String>): List<Result>
}